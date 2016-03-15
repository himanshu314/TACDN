import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by ravikumarsingh on 3/7/16.
 */

class ServerReq extends Thread{

    Socket clientSocket;
    AmazonS3 s3;
    String bucketName;
    static ArrayList<com.amazonaws.services.s3.model.Bucket> bucketArrayList = new ArrayList<com.amazonaws.services.s3.model.Bucket>();

    /**
     * The constructor for ServerReq Class
     * @param  clientSocket, s3. bucketName
     * @return Object
     */
    public ServerReq(Socket clientSocket, AmazonS3 s3, String bucketName) {
        this.clientSocket = clientSocket;
        this.s3 = s3;
        this.bucketName = bucketName;
    }

    /**
     * listingTheBucket function lists all the Bucket
     * @param  s3
     * @return void
     */
    private static void listingTheBucket(AmazonS3 s3) {
        for( com.amazonaws.services.s3.model.Bucket bucket : s3.listBuckets()){
            bucketArrayList.add(bucket);
        }
    }

    /**
     * sendDataToClient function sends the data back to Client
     * @param  s3, file
     * @return boolean
     * @throws java.io.IOException
     */
    public void sendDataToClient(AmazonS3 s3, String file) throws IOException {
        try {
            for (int i = 0; i < 1; i++) {
                S3Object object = s3.getObject(new GetObjectRequest(bucketName, file));
                if( object.getKey().equalsIgnoreCase(file)) {
                    storingTheObjectToDisk1(object.getObjectContent(), file);
                    break;
                }
            }
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which" + " means your request made it " + "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means"+ " the client encountered " + "an internal error while trying to " + "communicate with S3, " + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }

    /**
     * storingTheObjectToDisk1 function store the file requested from AWS S3 to its chache
     * @param  objectContent , key
     * @return void
     * @throws java.io.IOException
     */
    private static void storingTheObjectToDisk1(InputStream objectContent, String key) {
        BufferedOutputStream bos = null;
        byte[] buff = new byte[50*1024];
        int count;
        try {
            bos = new BufferedOutputStream(new FileOutputStream("/home/ubuntu/" + key));
            while( (count = objectContent.read(buff)) != -1)
            {
                bos.write(buff, 0, count);
            }
            bos.close();
            objectContent.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * listingKeysOfAllTheObject function looking for all requested file in every bucket present in AWS S3
     * @param  s3 file
     * @return boolean
     * @throws AmazonServiceException
     */
    private boolean listingKeysOfAllTheObject(AmazonS3 s3, String file) {
        try {
            for (int i = 0; i < 1; i++) {

                ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName);
                ObjectListing objectListing;
                do {
                    objectListing = s3.listObjects(listObjectsRequest);
                    for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                        if (objectSummary.getKey().equalsIgnoreCase(file)) {
                            return true;
                        }
                    }
                    listObjectsRequest.setMarker(objectListing.getNextMarker());
                } while (objectListing.isTruncated());
            }
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, " +
                    "which means your request made it " +
                    "to Amazon S3, but was rejected with an error response " +
                    "for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, " +
                    "which means the client encountered " +
                    "an internal error while trying to communicate" +
                    " with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        return false;
    }

    private void gettingDatafromS3(AmazonS3 s3, String fileName) throws IOException {
        Path path = Paths.get("/home/ubuntu/" + fileName);
        //first checking if the Origin Server is having the file in its disk
        if(Files.notExists(path)){
            // not present in the disk
            if(listingKeysOfAllTheObject(s3, fileName)){
                sendDataToClient(s3, fileName);
            }
            else {
                System.out.println("file is not present S3, can't serve you request!");
            }
        }
    }

    /**
     * run function receives the file requested by client and send the file back to client
     * @return void
     * @throws java.lang.Exception
     */
    public void run() {

        String fileName;
        DataInputStream in = null;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        BufferedOutputStream out =null;
        byte[] arr = new byte[5000];
        int count;
        try {
            // Reading the file name from Cache Server
            in = new DataInputStream(clientSocket.getInputStream());
            fileName = in.readUTF();

            // calling the function to download the file into the disk
            gettingDatafromS3(s3, fileName);

            //read file from disk
            fis = new FileInputStream("/home/ubuntu/" + fileName);
            bis = new BufferedInputStream(fis);
            //output stream for socket
            out = new BufferedOutputStream(clientSocket.getOutputStream());
            // writing to streams
            while ((count = bis.read(arr)) > 0) {
                out.write(arr, 0, count);
            }
            // flushing and closing all the open streams
            out.flush();
            out.close();
            fis.close();
            bis.close();
            clientSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
public class Origin_Server extends Thread {

    ServerSocket serverSocket;
    AmazonS3 s3;
    int portNumber;

    /**
     * The constructor for Origin_Server Class
     * @return Object
     */
    public Origin_Server(int portNumber) {
        this.portNumber = portNumber;
        this.serverSocket = null;
    }

    /**
     * The Setting up the Origin_Server Class
     * @return void
     */
    private void setUp() {
        AWSCredentials credentials;
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
        s3 = new AmazonS3Client(credentials);
    }

    /**
     * The run function for Origin_Server Class
     * @return void
     */
    public void run() {
        try {
            serverSocket = new ServerSocket(portNumber);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                (new ServerReq(clientSocket, s3, "contentfilesbucket")).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The main function for Origin_Server Class
     * @return void
     */
    public static void main(String[] args) {
        int portNumber = Integer.parseInt(args[0]);
        Origin_Server origin_server = new Origin_Server(portNumber);
        try{

            origin_server.setUp();
            origin_server.start();

        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
