import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

/**
 * Created by Himanshu on 3/6/2016.
 */
public class WorkerThread implements Runnable {
    final static Logger log = Logger.getLogger(WorkerThread.class);

    Socket soc;
    CacheServer cacheServer;

    public WorkerThread(CacheServer cacheServer, Socket soc) {
        this.soc = soc;
        this.cacheServer = cacheServer;
    }

    public void run() {
        try {
            InputStream ip = soc.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(ip));
            while (true) {
                String msg = br.readLine();
                System.out.println("msg: " + msg);
                if (null != msg) {
                    //if (msg.contains("GET")) {
                    //    String contentName = msg.split(":")[2];
                        String contentName = msg;
                        if(cacheServer.hasContent(contentName)) {
                            System.out.println("Content present in cache server");
                            String contentPath = cacheServer.getContentPath(contentName);
                            File fr = new File(contentPath);
                            byte[] bb = new byte[(int) fr.length()];
                            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fr));
                            bis.read(bb, 0, bb.length);
                            OutputStream out = soc.getOutputStream();
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
                            bw.write(String.valueOf(bb), 0, bb.length);
                        } else {
                            System.out.println("Content requested from origin server");
                            Socket parentSoc = new Socket(cacheServer.getParentIPAdd(), 60000);
                            OutputStream out = parentSoc.getOutputStream();
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
                            bw.write(msg, 0, msg.length());
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
            log.error("Exception in communicating");
        }
    }


}
