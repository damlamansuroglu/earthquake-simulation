/*
 * The Node class represents a single element (node) in the Doubly Linked List.
 * Since we need to store both Watchers and Earthquakes in lists, this Node
 * is designed to hold either type of data.
 * * It contains references (pointers) to the next node and the previous node,
 * allowing bidirectional traversal.
 */
public class Node {
    // Data fields: One of these will be null depending on what this node stores.
    public Watcher watcherData;      // Stores Watcher object if this is a watcher node
    public Earthquake earthquakeData; // Stores Earthquake object if this is an earthquake node

    // Pointers for the Doubly Linked List structure
    public Node next; // Reference to the next node in the list
    public Node prev; // Reference to the previous node in the list

    /*
     * Constructor for creating a Node that holds a Watcher.
     * @param w: The Watcher object to be stored.
     */
    public Node(Watcher w) {
        this.watcherData = w;
        this.earthquakeData = null; // No earthquake data in this node
        this.next = null;
        this.prev = null;
    }

    /*
     * Constructor for creating a Node that holds an Earthquake.
     * @param e: The Earthquake object to be stored.
     */
    public Node(Earthquake e) {
        this.watcherData = null; // No watcher data in this node
        this.earthquakeData = e;
        this.next = null;
        this.prev = null;
    }
}