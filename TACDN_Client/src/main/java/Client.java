
import java.io.*;
import java.net.Socket;

/**
 * Created by ravikumarsingh on 3/9/16.
 */

public class Client {

    String csIp, fileName;

    Client(String fileName) {
        this.fileName = fileName;
        csIp = "10.100.20.30";
    }

    /**
     * requestFile function sends the request to CDNs
     * @return void
     * @throws java.lang.Exception
     */
    void requestFile() {
        byte[] arr;

        InputStream is = null;
        BufferedOutputStream bos = null;

        try {
            Socket socket = new Socket(csIp,60000);
            //send file name to content server
            OutputStream outputServer = socket.getOutputStream();
            DataOutputStream out = new DataOutputStream(outputServer);
            out.writeUTF(fileName);

            //read file at client
            is = socket.getInputStream();
            //write file to client disk - buffer
            bos = new BufferedOutputStream(new FileOutputStream(fileName));

            int bufferSize = 5000;
            byte[] bytes = new byte[bufferSize];

            int count;
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
        Client c = new Client(args[0]);
        c.requestFile();
    }
}
