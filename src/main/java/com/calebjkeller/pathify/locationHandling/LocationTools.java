/*
 * Copyright (C) 2020 caleb
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.calebjkeller.pathify.locationHandling;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Caleb Keller
 */
public class LocationTools {
    
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
                        loc.address.get("houseNumber") + " " +
                        loc.address.get("streetName");
        
        String cityName = loc.address.get("cityName");
        String zipCode = loc.address.get("zipCode");
        
        return getArcGISCandidates(address, cityName, zipCode);
    }
    
    public static String[] getArcGISCandidates(String streetAddress, String cityName, String zipCode) {
        
        String parameterFormat = "f=json&address=%s&city=%s&zip=%s&state=Ohio";
        String[] parameters = {streetAddress, cityName, zipCode};
        
        return getArcGISCandidates(parameterFormat, parameters);
    }
    
    public static String[] getArcGISCandidates(String address) {
        
        String parameterFormat = "f=json&singleLine=%s";
        String[] parameters = {address};
        
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
    
    public static double[] geocodeAddress(String address) {
        
        String url = "http://www.mapquestapi.com/geocoding/v1/address?key=%s&location=%s"; 
        String charset = java.nio.charset.StandardCharsets.UTF_8.name();
        
        try {
            
            String key = Files.readAllLines(Paths.get("ApiKeys.txt")).get(2);
            address = URLEncoder.encode(address);
            
            url = String.format(url, key, address);
                                                      

            // Open a connection to the API
            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("Accept-Charset", charset);
            Scanner responseStream = new Scanner(connection.getInputStream());
            
            String response = "";
            
            while (responseStream.hasNext()) {
                response += responseStream.nextLine();
            }
            
            responseStream.close();
            
            JSONObject asJson = (JSONObject) new JSONParser().parse(response);
            JSONArray resultList = (JSONArray) asJson.get("results");
            JSONObject thisResult = (JSONObject) resultList.get(0);
            JSONArray locations = (JSONArray) thisResult.get("locations");
            JSONObject topLocation = (JSONObject) locations.get(0);
            JSONObject latLng = (JSONObject) topLocation.get("latLng");
            
            double[] coordinates = {(double) latLng.get("lat"), (double) latLng.get("lng")};
            
            return coordinates;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }                    

    }
    
    public static double[] getCentroid(Route route) {
        double x = 0;
        double y = 0;
        int total = 0;
        
        for (Location loc: route.getLocations()) {
            double[] coords = LocationTools.geocodeAddress(loc.getSafeAddress());
            x += coords[0];
            y += coords[1];
            total += 1;
        }
        
        double[] centroid = {x/total, y/total};
        
        return centroid;
    }
    
}
