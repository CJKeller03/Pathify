/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calebjkeller.pathify;

import com.calebjkeller.locationHandling.*;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Scanner;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;



/**
 *
 * @author Caleb Keller
 */
public class Tools {
    
    /**
     * Generate an ArrayList of Location objects from the data stored in
     * a .csv file. The first line of the file is assumed to be the header, which
     * is used to identify the data stored in each column.
     * 
     * @param filename The name (and possibly location) of the .csv file.
     * @return The ArrayList of Location objects.
     * @throws IOException 
     */
    public static ArrayList<Location> importDeliveryList(String filename) throws IOException {
        
        // Create the line number reader (a simple bufferedreader could be used here).
        FileReader fReader = new FileReader(filename);
        LineNumberReader lnr = new LineNumberReader(fReader);
        
        ArrayList<Location> locations = new ArrayList<Location>();
        
        // Remove all characters that shouldn't be present in the file.
        // This is necessary because for some reason, the first line of the .csv
        // file contains some garbage at the beginning, which messes up the 
        // code that interprets the header to put the data in the correct place.
        
        String headerAsString = lnr.readLine().replaceAll("[^ ,#:()a-zA-Z0-9]", "");
        String[] header = headerAsString.split(",");
        
        String line;
        while ((line = lnr.readLine()) != null) {
            locations.add(new Location(line.replaceAll("[^ ,#:()a-zA-Z0-9]", "").split(","), header));
        }
        
        return locations;
    }
    
    /**
     * Generate a HashMap linking origin - destination pairs to the distance
     * and approximate amount of time between them from a list of Location objects.
     * The pairs are made by concatenating the origin address and destination
     * address together into a single string, which is used as the key.
     * The pairs are asymmetric, which means that the distance/time from A -> B
     * isn't necessarily the same as the distance/time from B -> A
     * (because of things like one-way streets).
     * 
     * @param locations An ArrayList of location objects
     * @return A HashMap representing the distance/time between different addresses.
     */
    public static HashMap<String, Long[]> generateDistanceMatrix(ArrayList<Location> locations) {
        
        // Read the API key to be used from a file
        String key;
        try {
            key = new BufferedReader(new FileReader("GoogleApiKey.txt")).readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        // Get the address from every Location. If an address appears more than
        // once, it is ignored (in the case of apartment buildings or similar).
        // If a Location's address is invalid, skip it and print a warning to the console.
        ArrayList<String> addresses = new ArrayList<String>();
        for (Location loc : locations) {
            if (loc.hasValidAddress()) {
                String thisAddress = loc.getCompleteAddress();
                if (!addresses.contains(thisAddress)) {
                    addresses.add(loc.getUniqueAddress());
                }
            } else {
                System.out.println("WARNING! Location with invalid address:");
                System.out.println(loc.toString());
            }
        }
        
        // Set up the url
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json";
        String charset = java.nio.charset.StandardCharsets.UTF_8.name();
        String parameterFormat = "origins=%s&destinations=%s&key=%s";
        
        HashMap<String, Long[]> addressMatrix = new HashMap<String, Long[]>();
        
        for (int i = 0; i < addresses.size(); i++) {
            
            String origin = addresses.get(i);
            
            ArrayList<String> destinationList = new ArrayList<String>(addresses);
            destinationList.remove(i);            
            
            for (int j = 0; j < destinationList.size(); j += 25) {

                String destinations = String.join("|", destinationList.subList(j, Math.min(j + 25, destinationList.size())));
                String parameters;

                try {
                    parameters = String.format(parameterFormat, 
                                                URLEncoder.encode(origin, charset),
                                                URLEncoder.encode(destinations, charset),
                                                URLEncoder.encode(key, charset));
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

                String response = "";

                try {
                    // Open a connection to the API
                    URLConnection connection = new URL(url + "?" + parameters).openConnection();
                    //URLConnection connection = new URL("https://jsonplaceholder.typicode.com/posts/1").openConnection();
                    connection.setRequestProperty("Accept-Charset", charset);
                    Scanner responseStream = new Scanner(connection.getInputStream());

                    // Read the response into a String
                    while (responseStream.hasNext()) {
                        response += responseStream.nextLine() + "\n";
                    }
                    responseStream.close();
                    System.out.println(response);

                    // Write the response to a file to save it in case there is an error
                    // So that a new request doesn't have to be sent.
                    String filename = "responses/APIResponse-" +
                            java.time.LocalDateTime.now().toString().replace(":","-") +
                            ".txt";
                    FileWriter file = new FileWriter(filename);
                    file.write(connection.getURL().toString() + "\n");
                    file.write(response);
                    file.flush();
                    file.close();

                    // Remove the newlines for easier processing
                    response = response.replace("\n", "");

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

                addressMatrix.putAll(getDistanceMatrixFromJSON(response));
                
            }
        }

        
        return addressMatrix;
    }
    
    /**
     * Generate a HashMap linking origin - destination pairs to the distance
     * and approximate amount of time between them from a JSON file.
     * @param filename The name (and possibly location) of the JSON file.
     * @return A HashMap representing the distance/time between different addresses.
     */
    public static HashMap<String, Long[]> getDistanceMatrixFromFile(String filename) {
        
        Scanner fReader;
        
        try {
            fReader = new Scanner(new FileReader(filename));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        String JSONString = "";
        
        while (fReader.hasNext()) {
            JSONString += fReader.nextLine();
        }
        
        fReader.close();
        
        return getDistanceMatrixFromJSON(JSONString);
    }
    
    /**
     * Generate a HashMap linking origin - destination pairs to the distance
     * and approximate amount of time between them from a JSON String.
     * @param JSONString A String representing a JSON object.
     * @return A HashMap representing the distance/time between different addresses.
     */
    public static HashMap<String, Long[]> getDistanceMatrixFromJSON(String JSONString) {
        
        JSONObject data;
        
        // Parse the JSON string.
        try {
            data = (JSONObject) new JSONParser().parse(JSONString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        if (!data.get("status").equals("OK")) {
            return null;
        }
        
        HashMap<String, Long[]> addressMatrix = new HashMap<String, Long[]>();
        
        // Get the origin and destination addresses encoded in this object.
        JSONArray origins = (JSONArray) data.get("origin_addresses");
        JSONArray destinations = (JSONArray) data.get("destination_addresses");
        
        // The API response is encoded into a 2D array of JSON objects, where
        // each row represents an origin address, and each element in the row
        // is a destination address.
        JSONArray rows = (JSONArray) data.get("rows");
        
        for (int i = 0; i < rows.size(); i++) {
            JSONObject row = (JSONObject) rows.get(i);
            JSONArray elements = (JSONArray) row.get("elements");
            
            for (int j = 0; j < elements.size(); j++) {
                JSONObject element = (JSONObject) elements.get(j);
                
                JSONObject distData = (JSONObject) element.get("distance");
                JSONObject durData = (JSONObject) element.get("duration");
                
                // Get the actual value of the distance and duration.
                // These are always represented in meters and seconds.
                Long distance = (Long) distData.get("value");
                Long duration = (Long) durData.get("value");
                
                // Generate the key by concatenating the origin and destination
                // addresses together, and add the data to the HashMap.
                Long[] tmp = {distance, duration};
                addressMatrix.put((String) origins.get(i) + (String) destinations.get(j), tmp);
                
            }
            
        }
        
        return addressMatrix;
        
    }
}
