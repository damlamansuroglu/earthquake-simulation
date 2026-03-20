/*
 * ======================================================================================
 * CLASS: Earthquake
 * ======================================================================================
 * Description:
 * This class represents a single earthquake event in the simulation.
 * It stores all the data attributes provided in the input XML file.
 *
 * The attributes include:
 * - ID: Unique identifier for the earthquake.
 * - Time: The time of occurrence (in hours relative to the simulation start).
 * - Place: Textual description of the location.
 * - Coordinates: Latitude, Longitude, and Depth.
 * - Magnitude: The intensity of the earthquake on the Richter scale.
 * ======================================================================================
 */
public class Earthquake {

    // Unique ID of the earthquake (e.g., "001")
    public String id;

    // Time of the earthquake in hours (Integer)
    public int time;

    // Description of the location (e.g., "4km East of San Francisco")
    public String place;

    // Geographical coordinates (Latitude)
    public double latitude;

    // Geographical coordinates (Longitude)
    public double longitude;

    // Depth of the earthquake in km (Parsed from coordinates string)
    public double depth;

    // Magnitude of the earthquake
    public double magnitude;

    /*
     * ----------------------------------------------------------------------------------
     * CONSTRUCTOR
     * ----------------------------------------------------------------------------------
     * Initializes a new Earthquake object with all required details.
     *
     * @param id        : The unique ID string.
     * @param time      : Time in hours (int).
     * @param place     : Location description string.
     * @param lat       : Latitude coordinate.
     * @param lon       : Longitude coordinate.
     * @param depth     : Depth of the earthquake.
     * @param mag       : Magnitude value.
     * ----------------------------------------------------------------------------------
     */
    public Earthquake(String id, int time, String place, double lat, double lon, double depth, double mag) {
        this.id = id;
        this.time = time;
        this.place = place;
        this.latitude = lat;
        this.longitude = lon;
        this.depth = depth;
        this.magnitude = mag;
    }
}
