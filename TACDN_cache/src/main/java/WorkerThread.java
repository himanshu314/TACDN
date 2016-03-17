import java.io.*;
import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * Created by Himanshu on 3/6/2016.
 */
public class WorkerThread implements Runnable {
    final static Logger log = Logger.getLogger(WorkerThread.class);

    Socket soc;
    CacheServer cacheServer;

    public WorkerThread(CacheServer cacheServer, Socket soc) {
        System.out.println("Creating new worker thread");
        this.soc = soc;
        this.cacheServer = cacheServer;
    }

    public void run() {
        try {

            DataInputStream dis = new DataInputStream(soc.getInputStream());
            System.out.println("Inside run() of worker thread");
            while (true) {
                String msg = dis.readUTF().trim();
                System.out.println("msg: " + msg);
                System.out.flush();
                if (null != msg) {
                    //if (msg.contains("GET")) {
                    //    String contentName = msg.split(":")[2];
                        String contentName = msg;
                        if(cacheServer.hasContent(contentName)) {
                            System.out.println("Content present in cache server");
                            String contentPath = cacheServer.getContentPath(contentName);

                            BufferedOutputStream clientOut = new BufferedOutputStream(soc.getOutputStream());
                            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(contentPath));
                            int count2;
                            byte[] bytes2 = new byte[5000];
                            while ((count2 = bis.read(bytes2)) > 0) {
                                System.out.println("Writing content worth: " + count2 + " bytes to input file");
                                clientOut.write(bytes2, 0, count2);
                            }
                            bis.close();
                            clientOut.close();
                            soc.close();
                            break;
                        } else {
                            System.out.println("Content requested from origin server");
                            Socket parentSoc = new Socket(cacheServer.getParentIPAdd(), cacheServer.getPortNum());
                            if(null == parentSoc) {
                                System.out.println("Parent socket acquired");
                            }
                            OutputStream out = parentSoc.getOutputStream();
                            System.out.println("Got the output stream");
                            DataOutputStream dos = new DataOutputStream(out);
                            dos.writeUTF(msg);
                            System.out.println("Wrote msg: " + msg + " to outputstream");

                            //write file to disk - buffer
                            InputStream is = parentSoc.getInputStream();
                            String filePath = System.getProperty("user.dir") + "/" + msg.trim();
                            System.out.println(filePath);
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
                            int count1;
                            byte[] bytes1 = new byte[5000];
                            while ((count1 = is.read(bytes1)) > 0) {
                                System.out.println("Writing content worth: " + count1 + " bytes to input file");
                                bos.write(bytes1, 0, count1);
                            }
                            System.out.println("File received, updating contentList");
                            CacheContent cc = new CacheContent(msg, filePath, 40);
                            cacheServer.getContentList().put(msg, cc);
                            cacheServer.getLru().addingTheContentInTheCache(cacheServer.pageCount.getAndIncrement(), cc);
                            bos.close();

                            //Send data to client
                            BufferedOutputStream clientOut = new BufferedOutputStream(soc.getOutputStream());
                            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath));
                            int count2;
                            byte[] bytes2 = new byte[5000];
                            while ((count2 = bis.read(bytes2)) > 0) {
                                System.out.println("Writing content worth: " + count2 + " bytes to input file");
                                clientOut.write(bytes2, 0, count2);
                            }
                            bis.close();
                            clientOut.close();
                            soc.close();
                            break;
                        }
                   // } else if (msg.contains("UPDT")) {


                  //  } else if (msg.contains("REF")) {

                  //  } else if (msg.contains("ISPRSNT")) {

                   // } else if (msg.contains("PUT")) {

                  //  } else if (msg.contains("REPLY")) {

                  //  } else {
                  //      log.debug("Message doesn't contain anything");
                  //  }
                } else {
                    log.debug("Message is NULL");
                }
            }
        /*} catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }*/
    } catch (IOException e) {
            e.printStackTrace();
            log.error("Exception in communicating");
        }
    }


}
