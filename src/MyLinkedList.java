/*
 * MyLinkedList class implements a Doubly Linked List data structure.
 * We are implementing this manually because using java.util.LinkedList is restricted.
 * * This class supports operations for both Watchers and Earthquakes, such as:
 * - Adding elements to the end of the list.
 * - Deleting a watcher by name.
 * - Removing old earthquakes (older than 6 hours).
 * - Finding the largest earthquake in the list.
 */
public class MyLinkedList {
    public Node head; // Points to the first node in the list
    public Node tail; // Points to the last node in the list

    /*
     * Constructor to initialize an empty list.
     */
    public MyLinkedList() {
        this.head = null;
        this.tail = null;
    }

    /*
     * Checks if the list is empty.
     * @return true if the list contains no nodes, false otherwise.
     */
    public boolean isEmpty() {
        return head == null;
    }

    /*
     * Adds a new Watcher to the end of the list.
     * @param w: The Watcher object to add.
     */
    public void addWatcher(Watcher w) {
        Node newNode = new Node(w); // Create a new node wrapping the watcher

        if (isEmpty()) {
            // If the list is empty, the new node becomes both head and tail
            head = tail = newNode;
        } else {
            // If list is not empty, attach new node to the end
            tail.next = newNode;    // Current tail points to new node
            newNode.prev = tail;    // New node points back to current tail
            tail = newNode;         // Update tail to be the new node
        }
    }

    /*
     * Adds a new Earthquake to the end of the list.
     * @param e: The Earthquake object to add.
     */
    public void addEarthquake(Earthquake e) {
        Node newNode = new Node(e); // Create a new node wrapping the earthquake

        if (isEmpty()) {
            // If the list is empty, the new node becomes both head and tail
            head = tail = newNode;
        } else {
            // Append to the end of the list
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }

    /*
     * Removes a Watcher from the list based on their name.
     * This traverses the list to find the matching name and unlinks the node.
     * @param name: The name of the watcher to delete.
     */
    public void deleteWatcher(String name) {
        Node current = head; // Start traversal from the head

        while (current != null) {
            // Check if this node is a watcher node and matches the name
            if (current.watcherData != null && current.watcherData.name.equals(name)) {

                // Case 1: The node to remove is the only node in the list
                if (current == head && current == tail) {
                    head = tail = null;
                }
                // Case 2: The node to remove is the head (first node)
                else if (current == head) {
                    head = head.next; // Move head forward
                    head.prev = null; // Remove reference to the old head
                }
                // Case 3: The node to remove is the tail (last node)
                else if (current == tail) {
                    tail = tail.prev; // Move tail backward
                    tail.next = null; // Remove reference to the old tail
                }
                // Case 4: The node is in the middle of the list
                else {
                    current.prev.next = current.next; // Link previous node to next node
                    current.next.prev = current.prev; // Link next node to previous node
                }
                return; // Exit method after deletion
            }
            current = current.next; // Move to the next node
        }
    }

    /*
     * Removes earthquakes that are older than 6 hours relative to the current simulation time.
     * Since the list is ordered by time, we only need to check from the head (oldest).
     * @param currentTime: The current time of the simulation.
     */
    public void removeOldEarthquakes(int currentTime) {
        // Continue removing from head as long as the head is "too old"
        while (head != null) {
            if (head.earthquakeData != null) {
                // Check if the earthquake happened more than 6 hours ago
                if ((currentTime - head.earthquakeData.time) > 6) {
                    // Remove the head node
                    if (head == tail) {
                        head = tail = null; // List becomes empty
                    } else {
                        head = head.next; // Move head forward
                        head.prev = null; // Clear back-reference
                    }
                } else {
                    // If the oldest earthquake (at head) is recent enough,
                    // then all subsequent earthquakes are also recent. Stop checking.
                    break;
                }
            } else {
                break;
            }
        }
    }

    /*
     * Finds the earthquake with the largest magnitude in the current list.
     * @return The Earthquake object with the highest magnitude, or null if list is empty.
     */
    public Earthquake findLargestEarthquake() {
        if (isEmpty()) return null; // Return null if there are no earthquakes

        Node current = head;
        Earthquake maxEq = current.earthquakeData; // Assume the first one is the largest initially

        while (current != null) {
            // Compare current node's magnitude with the current maximum
            if (current.earthquakeData.magnitude > maxEq.magnitude) {
                maxEq = current.earthquakeData; // Update max if current is larger
            }
            current = current.next;
        }
        return maxEq;
    }
}