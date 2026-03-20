/*
 * ======================================================================================
 * CLASS: Watcher
 * ======================================================================================
 * Overview:
 * This class represents a "Watcher" (a user) in the Earthquake Notification System.
 * It serves as a data object that holds the user's geographical location and
 * their identification name.
 *
 * Instances of this class are stored in the 'watcher-list' (Doubly Linked List)
 * and are checked against new earthquakes to generate proximity notifications.
 * ======================================================================================
 */
public class Watcher {

    // The latitude coordinate of the watcher's location (e.g., 40.7128).
    public double latitude;

    // The longitude coordinate of the watcher's location (e.g., -74.0060).
    public double longitude;

    // The unique name or identifier of the watcher (e.g., "Tom", "Jane").
    public String name;

    /*
     * ----------------------------------------------------------------------------------
     * CONSTRUCTOR: Watcher
     * ----------------------------------------------------------------------------------
     * Initializes a new Watcher object with the specified location coordinates and name.
     *
     * This constructor is designed to be called when parsing the "add" command
     * from the input file.
     *
     * @param latitude  : A double value representing the user's latitude coordinate.
     * @param longitude : A double value representing the user's longitude coordinate.
     * @param name      : A String representing the name of the watcher.
     * ----------------------------------------------------------------------------------
     */
    public Watcher(double latitude, double longitude, String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }
}