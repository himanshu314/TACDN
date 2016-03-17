import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Himanshu on 3/8/2016.
 */
public class MainThread implements Runnable {

    final static Logger log = Logger.getLogger(WorkerThread.class);

    private ServerSocket serSoc;
    private CacheServer cacheServer;
    private String ipOS;

    public MainThread(int portNum, CacheServer cacheServer, String ipOS) {
        this.cacheServer = cacheServer;
        this.ipOS = ipOS;
        try {
            serSoc = new ServerSocket(portNum);
        } catch (IOException e) {
            log.error("Error in main-thread creation");
        }
    }

    public boolean connectParent(String ipOS) {

        return false;
    }

    public void run() {
        while (true) {
            System.out.println("We are still in main thread");
            System.out.flush();
            try {
                Socket cliSoc = serSoc.accept();
                System.out.println("Accepted connection from client");
                WorkerThread cliThread = new WorkerThread(cacheServer, cliSoc);
                cliThread.run();
            } catch (IOException e) {
                log.error("error in server-thread creation");
            }
        }
    }
}
