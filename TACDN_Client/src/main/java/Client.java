
import java.io.*;
import java.net.Socket;

/**
 * Created by ravikumarsingh on 3/9/16.
 */

public class Client {

    String ipAddress, fileName;
    int portName;

    Client(String ipAddress, int portName, String fileName) {
        this.ipAddress = ipAddress;
        this.portName = portName;
        this.fileName = fileName;
    }

    /**
     * requestFile function sends the request to CDNs
     * @return void
     * @throws java.lang.Exception
     */
    void requestFile() {
        byte[] arr;
        Socket socket = null;
        InputStream is = null;
        BufferedOutputStream bos = null;
        DataOutputStream out = null;
        int bufferSize = 5000;
        byte[] bytes = new byte[bufferSize];
        int count;

        try {
            socket = new Socket(ipAddress,portName);
            //sending the file name to content server
            out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(fileName);

            //read file at client
            is = socket.getInputStream();
            //write file to client disk - buffer
            bos = new BufferedOutputStream(new FileOutputStream(fileName));
            while ((count = is.read(bytes)) > 0) {
                bos.write(bytes, 0, count);
            }
            // closing all open streams
            bos.flush();
            bos.close();
            is.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The main method begins execution of the tests.
     * @param args not used
     * @return void
     */
    public static void main(String args[]) {
        if( args.length <3){
            System.out.println("Please provide the IPAddress, portNumber of the Server and the file needed");
            System.exit(0);
        }
        Client c = new Client(args[0], Integer.parseInt(args[1]), args[2]);
        c.requestFile();
    }
}
