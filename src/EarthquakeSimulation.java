import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
 * ======================================================================================
 * CLASS: EarthquakeSimulation (MAIN DRIVER)
 * ======================================================================================
 * Overview:
 * This class serves as the entry point for the Earthquake Notification System.
 * It implements a Discrete Event Simulation where time advances based on events
 * rather than fixed increments (Option 2 in the assignment description).
 *
 * Key Functionalities:
 * 1. Reads input filenames from the user (No hardcoding).
 * 2. Parses two distinct file formats (XML-like for earthquakes, text for watchers).
 * 3. Manages a custom Doubly Linked List for storing data.
 * 4. Implements a "Time Jump" mechanism to efficiently skip idle periods.
 * 5. Handles proximity notifications based on Euclidean distance and magnitude threshold.
 * ======================================================================================
 */
public class EarthquakeSimulation {

    /*
     * ----------------------------------------------------------------------------------
     * INNER CLASS: EventStream
     * ----------------------------------------------------------------------------------
     * Purpose:
     * Handles file I/O operations and acts as a "Lookahead Buffer".
     * It allows the main simulation loop to "peek" at the timestamp of the next event
     * without consuming it, enabling the comparison of earthquake vs. watcher event times.
     * ----------------------------------------------------------------------------------
     */
    static class EventStream {
        Scanner scanner;
        boolean isEarthquakeFile; // Flag to identify the file type

        // Buffers to temporarily hold the next event data
        Earthquake bufferEq = null;
        String bufferWatcherLine = null;

        // Timestamp of the pending event (Integer.MAX_VALUE indicates End of File)
        int nextTimestamp = Integer.MAX_VALUE;

        // Constructor: Opens file and reads the first record immediately.
        public EventStream(String filename, boolean isEq) {
            this.isEarthquakeFile = isEq;
            try {
                scanner = new Scanner(new File(filename));
                readNext(); // Prime the buffer with the first event
            } catch (FileNotFoundException e) {
                System.out.println("Error: Input file '" + filename + "' not found.");
                System.out.println("Please ensure the file exists in the project root directory.");
            }
        }

        /*
         * Method: readNext
         * Parses the next valid entry from the file.
         * - For Earthquake files: Parses XML tags (<id>, <time>, etc.).
         * - For Watcher files: Reads the line and extracts the timestamp.
         */
        void readNext() {
            if (scanner == null || !scanner.hasNext()) {
                nextTimestamp = Integer.MAX_VALUE; // End of stream reached
                bufferEq = null;
                bufferWatcherLine = null;
                return;
            }

            if (isEarthquakeFile) {
                // --- XML PARSING LOGIC FOR EARTHQUAKE DATA ---
                try {
                    String id = "", timeStr = "", place = "", coords = "", magStr = "";

                    // Consume lines until the closing tag </earthquake> is found
                    while(scanner.hasNext()) {
                        String line = scanner.nextLine().trim();
                        if(line.startsWith("<id>")) id = extractVal(line);
                        else if(line.startsWith("<time>")) timeStr = extractVal(line);
                        else if(line.startsWith("<place>")) place = extractVal(line);
                        else if(line.startsWith("<coordinates>")) coords = extractVal(line);
                        else if(line.startsWith("<magnitude>")) magStr = extractVal(line);
                        else if(line.startsWith("</earthquake>")) break;
                    }

                    if (id.isEmpty()) { readNext(); return; } // specific safety check

                    // Parse numerical values
                    int time = Integer.parseInt(timeStr.trim());
                    double mag = Double.parseDouble(magStr.trim());

                    // Parse Coordinates: "Lat, Lon, Depth"
                    String[] coordParts = coords.split(",");
                    double lat = Double.parseDouble(coordParts[0].trim());
                    double lon = Double.parseDouble(coordParts[1].trim());
                    double depth = Double.parseDouble(coordParts[2].trim());

                    // Create Earthquake Object
                    bufferEq = new Earthquake(id, time, place, lat, lon, depth, mag);
                    nextTimestamp = time;

                } catch (Exception e) {
                    nextTimestamp = Integer.MAX_VALUE; // Stop on error
                }

            } else {
                // --- LINE PARSING LOGIC FOR WATCHER COMMANDS ---
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) { readNext(); return; }

                bufferWatcherLine = line;
                String[] parts = line.trim().split("\\s+");

                // First token is always the timestamp
                nextTimestamp = Integer.parseInt(parts[0]);
            }
        }

        // Helper: Extracts string between XML tags <tag>VALUE</tag>
        private String extractVal(String line) {
            int start = line.indexOf(">") + 1;
            int end = line.lastIndexOf("<");
            if (start > end) return "";
            return line.substring(start, end).trim();
        }
    }

    // ==================================================================================
    // MAIN METHOD
    // ==================================================================================
    public static void main(String[] args) {


        // --- STEP 1: USER INPUT FOR FILENAMES ---
        // As per assignment requirements, filenames are NOT hardcoded.
        Scanner inputScanner = new Scanner(System.in);

        System.out.print("Enter earthquake filename (e.g., 1-earthquake-file.txt): ");
        String eqFileName = inputScanner.next();

        System.out.print("Enter watcher filename (e.g., 1-watcher-file.txt): ");
        String watcherFileName = inputScanner.next();

        // --- STEP 2: INITIALIZATION ---
        // Initialize custom Doubly Linked Lists for simulation
        MyLinkedList watcherList = new MyLinkedList();
        MyLinkedList earthquakeList = new MyLinkedList();

        // Initialize streams to read from the provided files
        EventStream eqStream = new EventStream(eqFileName, true);
        EventStream watcherStream = new EventStream(watcherFileName, false);

        int currentTime = 0; // Simulation Clock

        /*
         * ------------------------------------------------------------------------------
         * MAIN SIMULATION LOOP (Event-Driven)
         * Runs until both input streams are exhausted.
         * ------------------------------------------------------------------------------
         */
        while (eqStream.nextTimestamp != Integer.MAX_VALUE || watcherStream.nextTimestamp != Integer.MAX_VALUE) {

            boolean processEarthquake = false;

            // --- STEP 3: TIME ADVANCEMENT (Discrete Event Simulation) ---
            // Determine which event occurs next. The simulation clock jumps
            // directly to the timestamp of the earliest event.
            if (eqStream.nextTimestamp < watcherStream.nextTimestamp) {
                processEarthquake = true;
                currentTime = eqStream.nextTimestamp;
            } else {
                processEarthquake = false;
                currentTime = watcherStream.nextTimestamp;
            }

            // --- STEP 4: LAZY CLEANUP (CRITICAL LOGIC) ---
            // Before processing the current event, we remove earthquakes that are
            // older than 6 hours relative to the NEW currentTime.
            // This ensures all queries (like 'query-largest') work on valid data.
            earthquakeList.removeOldEarthquakes(currentTime);

            // --- STEP 5: EVENT PROCESSING ---
            if (processEarthquake) {
                // ==========================================================
                // CASE A: Processing an Earthquake Event
                // ==========================================================
                Earthquake eq = eqStream.bufferEq;

                // Add earthquake to the list
                earthquakeList.addEarthquake(eq);
                System.out.println("Earthquake " + eq.place + " is inserted into the earthquake-list");

                // Check proximity for all registered watchers
                Node currentWatcher = watcherList.head;
                while (currentWatcher != null) {
                    Watcher w = currentWatcher.watcherData;

                    // Calculate Euclidean Distance
                    // Formula: sqrt((lat1 - lat2)^2 + (lon1 - lon2)^2)
                    double dist = Math.sqrt(Math.pow(w.latitude - eq.latitude, 2) + Math.pow(w.longitude - eq.longitude, 2));

                    // Calculate Notification Threshold
                    // Formula: 2 * (Magnitude^3)
                    double threshold = 2 * Math.pow(eq.magnitude, 3);

                    // Trigger notification if within range
                    if (dist < threshold) {
                        System.out.println("Earthquake " + eq.place + " is close to " + w.name);
                    }
                    currentWatcher = currentWatcher.next;
                }

                // Move to the next earthquake record
                eqStream.readNext();

            } else {
                // ==========================================================
                // CASE B: Processing a Watcher Command
                // ==========================================================
                String line = watcherStream.bufferWatcherLine;
                String[] parts = line.trim().split("\\s+");
                String type = parts[1]; // Command type (add, delete, query-largest)

                if (type.equals("add")) {
                    // Syntax: TIME add LAT LON NAME
                    double lat = Double.parseDouble(parts[2]);
                    double lon = Double.parseDouble(parts[3]);
                    String name = parts[4];
                    watcherList.addWatcher(new Watcher(lat, lon, name));
                    System.out.println(name + " is added to the watcher-list");

                } else if (type.equals("delete")) {
                    // Syntax: TIME delete NAME
                    String name = parts[2];
                    watcherList.deleteWatcher(name);
                    System.out.println(name + " is removed from the watcher-list");

                } else if (type.equals("query-largest")) {
                    // Syntax: TIME query-largest

                    // 1. Try to find the largest earthquake in the list
                    Earthquake largest = earthquakeList.findLargestEarthquake();

                    // 2. Check if an earthquake was found or if the list is empty
                    if (largest == null) {
                        // If no earthquake is found, print "No record on list" as required by the sample output
                        System.out.println("No record on list");
                    } else {
                        // If an earthquake IS found, print the header first, then the details
                        System.out.println("Largest earthquake in the past 6 hours:");
                        System.out.println("Magnitude " + largest.magnitude + " at " + largest.place);
                    }
                }

                // Move to the next watcher command
                watcherStream.readNext();
            }
        } // End of While Loop

        inputScanner.close();
    } // End of Main Method
}