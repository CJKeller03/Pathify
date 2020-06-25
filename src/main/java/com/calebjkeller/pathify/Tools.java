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
 * @author caleb
 */
public class Tools {
    
    public static ArrayList<Location> importDeliveryList(String filename) throws IOException {
        
        FileReader fReader = new FileReader(filename);
        LineNumberReader lnr = new LineNumberReader(fReader);
        
        ArrayList<Location> locations = new ArrayList<Location>();
        
        String headerAsString = lnr.readLine().replaceAll("[^ ,#:()a-zA-Z0-9]", "");
        String[] header = headerAsString.split(",");
        
        String line;
        while ((line = lnr.readLine()) != null) {
            locations.add(new Location(line.replaceAll("[^ ,#:()a-zA-Z0-9]", "").split(","), header));
        }
        
        return locations;
    }
    
    public static HashMap<String, Long[]> generateDistanceMatrix(ArrayList<Location> locations) {
        
        String key;
        try {
            key = new BufferedReader(new FileReader("GoogleApiKey.txt")).readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
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
        
        System.out.println(key);
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json";
        String charset = java.nio.charset.StandardCharsets.UTF_8.name();
        String parameters = "origins=%s&destinations=%s&key=%s";
        
        int uniqueAddresses = addresses.size();
        
        String queries = String.join("|", addresses);
        
        try {
            parameters = String.format(parameters, 
                                        URLEncoder.encode(queries, charset),
                                        URLEncoder.encode(queries, charset),
                                        URLEncoder.encode(key, charset));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        String response = "";
        
        try {
            
            URLConnection connection = new URL(url + "?" + parameters).openConnection();
            //URLConnection connection = new URL("https://jsonplaceholder.typicode.com/posts/1").openConnection();
            connection.setRequestProperty("Accept-Charset", charset);
            Scanner responseStream = new Scanner(connection.getInputStream());
            
            while (responseStream.hasNext()) {
                response += responseStream.nextLine() + "\n";
            }
            responseStream.close();
            System.out.println(response);
            
            FileWriter file = new FileWriter("APIResponse.txt");
            file.write(response);
            file.flush();
            file.close();
            
            response = response.replace("\n", "");
            
            System.out.println(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        return getDistanceMatrixFromJSON(response);
    }
    
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
    
    public static HashMap<String, Long[]> getDistanceMatrixFromJSON(String JSONString) {
        
        JSONObject data;
        
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
        
        JSONArray origins = (JSONArray) data.get("origin_addresses");
        JSONArray destinations = (JSONArray) data.get("destination_addresses");
        
        JSONArray rows = (JSONArray) data.get("rows");
        
        for (int i = 0; i < rows.size(); i++) {
            JSONObject row = (JSONObject) rows.get(i);
            JSONArray elements = (JSONArray) row.get("elements");
            
            for (int j = 0; j < elements.size(); j++) {
                JSONObject element = (JSONObject) elements.get(j);
                
                JSONObject distData = (JSONObject) element.get("distance");
                JSONObject durData = (JSONObject) element.get("duration");
                
                Long distance = (Long) distData.get("value");
                Long duration = (Long) durData.get("value");
                
                Long[] tmp = {distance, duration};
                addressMatrix.put((String) origins.get(i) + (String) destinations.get(j), tmp);
                
            }
            
        }
        
        return addressMatrix;
        
    }
}
