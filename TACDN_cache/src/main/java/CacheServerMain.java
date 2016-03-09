/**
 * Created by Himanshu on 3/9/2016.
 */
public class CacheServerMain {
    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("Give IP address of current server, parent and port number respectively");
            System.exit(0);
        }
        String serverIP = args[0];
        String parentIP = args[1];
        CacheServer cs = new CacheServer(serverIP, "FirstServer", parentIP);
        cs.startCS(Integer.valueOf(args[2]));
    }
}
