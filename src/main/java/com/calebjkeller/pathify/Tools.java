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
import java.io.LineNumberReader;
import java.net.URLEncoder;
import java.util.HashMap;

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
        
        System.out.println(headerAsString);
        
        String line;
        while ((line = lnr.readLine()) != null) {
            locations.add(new Location(line.replaceAll("[^ ,#:()a-zA-Z0-9]", "").split(","), header));
        }
        
        return locations;
    }
    
    public static HashMap<String, String> generateDistanceMatrix(ArrayList<String> addresses) {
        
        String key;
        try {
            key = new BufferedReader(new FileReader("GoogleApiKey.txt")).readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        System.out.println(key);
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json";
        String charset = java.nio.charset.StandardCharsets.UTF_8.name();
        String parameters = "origins=%s&destinations=%s&key=%s";
        int totalAddresses = addresses.size();
        
        for (int i = 0; i < totalAddresses; i++) {
            String origin = addresses.get(i);
            String destinations = String.join("|",addresses.subList(i + 1, totalAddresses));
            
            try {
                String.format(parameters, 
                        URLEncoder.encode(origin, charset),
                        URLEncoder.encode(destinations, charset),
                        URLEncoder.encode(key, charset));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            
            System.out.println(parameters);
        }
        
        return new HashMap<>();
    }
}
