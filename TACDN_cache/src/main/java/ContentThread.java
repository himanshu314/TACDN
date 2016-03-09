import org.apache.log4j.Logger;

import java.util.HashMap;

/**
 * Created by Himanshu on 3/9/2016.
 */
public class ContentThread implements Runnable {
    final static Logger log = Logger.getLogger(WorkerThread.class);

    CacheContent cc;
    CacheServer cs;

    public ContentThread(CacheContent cc, CacheServer cs) {
        this.cc = cc;
        this.cs = cs;
    }

    public void run() {
        int TTL = cc.getTTL();
        while (TTL > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("Error in sleeping thread!");
            }
            TTL--;
            cc.setTTL(TTL);
        }
        HashMap<String, CacheContent> contentList = cs.getContentList();
        synchronized (contentList) {
            contentList.remove(cc.getContentName());
        }
    }
}
