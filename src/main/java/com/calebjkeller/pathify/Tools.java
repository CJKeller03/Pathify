/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calebjkeller.pathify;

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
    public static ArrayList<String[]> importAddressList(String filename) throws IOException {

        FileReader fReader = new FileReader(filename);
        LineNumberReader lnr = new LineNumberReader(fReader);
        
        String[] header = lnr.readLine().split(",");
        
        ArrayList<String[]> mat = new ArrayList<String[]>();
        
        String line;
        while ((line = lnr.readLine()) != null) {
            mat.add(line.split(","));
        }
        
        return mat;
    }
    
    public static HashMap<String, String> generateDistanceMatrix(ArrayList<String> addresses, String apiKey) {
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json";
        String charset = java.nio.charset.StandardCharsets.UTF_8.name();
        String parameters = "origins=%s&destinations=%s";
        int totalAddresses = addresses.size();
        
        for (int i = 0; i < totalAddresses; i++) {
            String origin = addresses.get(i);
            String destinations = String.join("|",addresses.subList(i + 1, totalAddresses));
            
            try {
                String.format(parameters, 
                        URLEncoder.encode(origin, charset),
                        URLEncoder.encode(destinations, charset));
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            System.out.println(parameters);
        }
        
        return new HashMap<>();
    }
}
