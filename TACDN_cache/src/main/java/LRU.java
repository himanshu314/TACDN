import java.util.HashMap;

/**
 * Created by ravikumarsingh on 3/16/16.
 */

class Node{
    int pageNumber;
    CacheContent content;
    Node forwardPointer = null;
    Node backwardPointer = null;

    public Node(int pageNumber, CacheContent content) {
        this.content = content;
        this.pageNumber = pageNumber;
    }
}

public class LRU {

    int sizeOfTheCache;
    Node front =null;
    Node back =null;
    HashMap<Integer, Node> hashMap = new HashMap<Integer, Node>();
    CacheServer cc;


    public LRU(int sizeOfTheCache, CacheServer cc) {
        this.sizeOfTheCache = sizeOfTheCache;
        this.cc = cc;
    }

    CacheContent getTheContent(int pageNumber){
        if(hashMap.containsKey(pageNumber)){
            Node node = hashMap.get(pageNumber);
            removeTheNode(node);
            puttingTheNodeInTheFront(node);
            return node.content;
        }
        return null;
    }

    void addingTheContentInTheCache(int pageNumber, CacheContent content){
        if( hashMap.containsKey(pageNumber)){
            Node previous_Entry = hashMap.get(pageNumber);
            previous_Entry.content = content;
            removeTheNode(previous_Entry);
            puttingTheNodeInTheFront(previous_Entry);
        } else {
            Node node = new Node(pageNumber, content);
            if (hashMap.size() >= sizeOfTheCache) {
                hashMap.remove(back.pageNumber);
                removeTheNode(back);
                puttingTheNodeInTheFront(node);
            } else {
                puttingTheNodeInTheFront(node);
            }
            hashMap.put(pageNumber, node);
        }
    }

    private void removeTheNode(Node node) {
        if( node.forwardPointer == null){
            node.backwardPointer.forwardPointer =null;
            front = node.backwardPointer;
            node.backwardPointer =null;
            node =null;
        }
        else if( node.backwardPointer ==null){
            node.forwardPointer.backwardPointer =null;
            back= node.forwardPointer;
            node.forwardPointer =null;
            cc.getContentList().remove(node.content.getContentName());
            node =null;
        }
        else {
            Node front = node.forwardPointer;
            Node back = node.backwardPointer;
            back.forwardPointer = front;
            front.backwardPointer = back;
            node.backwardPointer = null;
            node.forwardPointer = null;
            node = null;
        }
    }


    private void puttingTheNodeInTheFront(Node node) {
        node.forwardPointer = null;
        node.backwardPointer= front;
        if( front !=null) {
            front.forwardPointer = node;
        }
        front = node;

        if( back ==null){
            back = front;
        }
    }

   /* void prinitingThedoubleLinkedList(){
        while( front !=null){
            System.out.println("pageNumber " + front.pageNumber + "content" + front.content);
            front = front.backwardPointer;
        }
    }*/

    /*public static void main(String[] args) {
        LRU lru = new LRU(3);
        lru.addingTheContentInTheCache(1, "one");
        lru.addingTheContentInTheCache(2, "two");
        lru.addingTheContentInTheCache(3, "three");
        lru.addingTheContentInTheCache(4, "foruth");

        lru.getTheContent(2);
        lru.prinitingThedoubleLinkedList();

    }*/

}
