/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calebjkeller.pathify;

import com.calebjkeller.locationHandling.*;
import components.ListDialog;

import java.io.BufferedReader;
import java.io.File;
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
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;



/**
 *
 * @author Caleb Keller
 */
public class Tools {
    
    /**
     * Generate an ArrayList of Location objects from the data stored in
     * a csv file. The first line of the file is assumed to be the header, which
     * is used to identify the data stored in each column.
     * 
     * @param filename The name (and possibly location) of the .csv file.
     * @return The ArrayList of Location objects.
     * @throws IOException 
     */
    public static ArrayList<Location> importDeliveryList(File csvFile, JFrame frame) {
        
        // Create the line number reader (a simple bufferedreader could be used here).
        System.out.println("Started");
        
        ArrayList<Location> locations = new ArrayList<Location>();
        
        try {
            FileReader fReader = new FileReader(csvFile);
            LineNumberReader lnr = new LineNumberReader(fReader);
            
            // Remove all characters that shouldn't be present in the file.
            // This is necessary because for some reason, the first line of the .csv
            // file contains some garbage at the beginning, which messes up the 
            // code that interprets the header to put the data in the correct place.

            String headerAsString = lnr.readLine().replaceAll("[^ ,#:()a-zA-Z0-9]", "");
            String[] header = headerAsString.split(",");

            String line;
            while ((line = lnr.readLine()) != null) {
                System.out.println(line);
                
                Location curLocation = new Location(line.replaceAll("[^ ,#:()a-zA-Z0-9]", "").split(","), header);
                String[] arcGISCandidates = getArcGISCandidates(curLocation);

                boolean foundRealAddress = false;
                String inputStreetAddress = curLocation.inputAddressComponents.get("houseNumber") + " " +
                                            curLocation.inputAddressComponents.get("streetName");
                inputStreetAddress = inputStreetAddress.toLowerCase();
                
                String inputCityName = curLocation.inputAddressComponents.get("cityName");
                inputCityName = inputCityName.toLowerCase();
                
                String inputZipCode = curLocation.inputAddressComponents.get("zipCode");
                
                
                while (!foundRealAddress) {
                    for (String address : arcGISCandidates) {
                        String[] splitAddress = address.split(",");
                        //System.out.println(realStreetAddress + " : " + inputStreetAddress);
                        
                        

                        if (splitAddress[0].toLowerCase().equals(inputStreetAddress) &&
                            splitAddress[1].toLowerCase().replace(" ", "").equals(inputCityName) &&
                            splitAddress[3].replace(" ", "").equals(inputZipCode)) {

                            foundRealAddress = true;

                            curLocation.verifiedAddressComponents.put("streetAddress", splitAddress[0]);
                            curLocation.verifiedAddressComponents.put("cityName", splitAddress[1]);
                            curLocation.verifiedAddressComponents.put("state", splitAddress[2]);
                            curLocation.verifiedAddressComponents.put("zipCode", splitAddress[3]);
                            break;
                        }

                    }

                    if (!foundRealAddress) {
                        String[] options = java.util.Arrays.copyOf(arcGISCandidates, arcGISCandidates.length+1);
                        options[options.length - 1] = "None of the above";
                        System.out.println(curLocation.getCompleteAddress());
                        String userInput = ListDialog.showDialog(frame,
                                              null,
                                              "The address: " + inputStreetAddress + " " +
                                                      curLocation.inputAddressComponents.get("cityName") + " " +
                                                      curLocation.inputAddressComponents.get("zipCode") + " " +
                                                      " could not be found.\n Please select"
                                                      + " the correct address from the list"
                                                      + " below.",
                                              "Verify address",
                                              options,
                                              "None of the above",
                                              "                                        ");

                        if (!userInput.equals("None of the above") &&
                                !userInput.equals("Stop")) {

                            String[] splitAddress = userInput.split(",");
                            curLocation.verifiedAddressComponents.put("streetAddress", splitAddress[0]);
                            curLocation.verifiedAddressComponents.put("cityName", splitAddress[1]);
                            curLocation.verifiedAddressComponents.put("state", splitAddress[2]);
                            curLocation.verifiedAddressComponents.put("zipCode", splitAddress[3]);
                            
                            locations.add(curLocation);
                            
                            foundRealAddress = true;

                        } else if (userInput.equals("Stop")) {
                            return null;
                        } else if (userInput.equals("None of the above")) {

                            String message = "Please enter the correct address for " + 
                                              curLocation.firstName + " " +
                                              curLocation.lastName + ".\n" +
                                              "(Including the city and zip code, if known)\n" +
                                              "The supplied address was:\n" +
                                              inputStreetAddress + " , " +
                                              curLocation.inputAddressComponents.get("cityName") + " , " +
                                              curLocation.inputAddressComponents.get("zipCode");

                            userInput = (String) JOptionPane.showInputDialog(
                                                        frame,
                                                        message,
                                                        "Provide correct address",
                                                        JOptionPane.PLAIN_MESSAGE,
                                                        null,
                                                        null,
                                                        "");
                            
                            if (userInput != null && userInput.length() > 0) {
                                String parameters = "f=json&SingleLine=%s";
                                String[] paramArr = {userInput};
                                arcGISCandidates = getArcGISCandidates(parameters, paramArr);
                            } else {
                                return null;
                            }


                        }
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
                    
                    // Whoops, this line leaked my original API key...
                    //file.write(connection.getURL().toString() + "\n");
                    
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
    
    /**
     * Find the standard postal address that refers to the same location as the
     * input address. If the address does not exist, but a similar address likely
     * to refer to the same location is found, the Location object's 
     * verifiedAddressComponents entries will be set, and this method will return false.
     * If no address can be found, this method will return false without setting
     * the verifiedAddressComponents entries.
     * 
     * @return Whether the verification was successful.
     */
    public static String[] getArcGISCandidates(Location loc) {

        String address = 
                        loc.inputAddressComponents.get("houseNumber") + " " +
                        loc.inputAddressComponents.get("streetName");
        
        String cityName = loc.inputAddressComponents.get("cityName");
        String zipCode = loc.inputAddressComponents.get("zipCode");
        
        return getArcGISCandidates(address, cityName, zipCode);
    }
    
    public static String[] getArcGISCandidates(String streetAddress, String cityName, String zipCode) {
        
        String parameterFormat = "f=json&address=%s&city=%s&zip=%s&state=Ohio";
        String[] parameters = {streetAddress, cityName, zipCode};
        
        return getArcGISCandidates(parameterFormat, parameters);
    }
    
    public static String[] getArcGISCandidates(String parameterFormat, String[] parameters) {
        
        String url = "http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer/findAddressCandidates"; 
        String charset = java.nio.charset.StandardCharsets.UTF_8.name();
        
        try {
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = URLEncoder.encode(parameters[i], charset);
            }
            
            String encodedParameters = String.format(parameterFormat, parameters);
                                                      

            // Open a connection to the API
            URLConnection connection = new URL(url + "?" + encodedParameters).openConnection();
            connection.setRequestProperty("Accept-Charset", charset);
            Scanner responseStream = new Scanner(connection.getInputStream());
            System.out.println(connection.toString());
            
            String response = "";
            
            while (responseStream.hasNext()) {
                response += responseStream.nextLine();
            }
            
            responseStream.close();
            
            JSONObject asJson = (JSONObject) new JSONParser().parse(response);
            JSONArray addressList = (JSONArray) asJson.get("candidates");
            
            String[] candidates = new String[addressList.size()];
            
            for (int i = 0; i < addressList.size(); i++) {
                JSONObject result = (JSONObject) addressList.get(i);
                 candidates[i] = (String) result.get("address");
                 
            }
            
            return candidates;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }                
    }    
}
