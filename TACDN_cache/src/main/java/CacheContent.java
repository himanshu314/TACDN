/**
 * Created by Himanshu on 3/3/2016.
 */
public class CacheContent {
    private String contentName;
    private String contentPath;
    private String contentVersion;
    private int TTL;

    public CacheContent(String contentName, String contentPath, int TTL) {
        this.contentName = contentName;
        this.contentPath = contentPath;
        this.TTL = TTL;
    }

    public String getContentName() {
        return contentName;
    }

    public String getContentPath() {
        return contentPath;
    }

    public int getTTL() {
        return TTL;
    }

    public void setTTL(int TTL) {
        this.TTL = TTL;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }
}
