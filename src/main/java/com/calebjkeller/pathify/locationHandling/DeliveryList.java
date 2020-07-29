/*
 * Copyright (C) 2020 Caleb Keller
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * A class for handling the data within the delivery list csv file.
 * @author Caleb Keller
 */
public class DeliveryList {
    
    private ArrayList<Location> locations;
    private boolean isSafe;
    
    /**
     * Create a DeliveryList object from the data in a csv file.
     * @param csvList The file to read data from
     */
    public DeliveryList(File csvList) {
        locations = this.generateLocations(csvList);
        isSafe = false;
    }
    
    /**
     * Generate a location object from the data in each row of a csv file.
     * @param csvList The file to read data from
     * @return A list of Location objects for each row of the csv file
     */
    private ArrayList generateLocations(File csvList) {
        ArrayList locations = new ArrayList<Location>();
        BufferedReader reader;
        
        try {
            reader = new BufferedReader(new FileReader(csvList));
            String[] header = reader.readLine().replaceAll("[^ ,#:()a-zA-Z0-9]", "").split(",");
            
            String line;
            
            while ((line = reader.readLine()) != null) {
                line = line.replaceAll("[^ ,#:()a-zA-Z0-9]", "");
                locations.add(new Location(line.split(","), header));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        return locations;
    }
    
    /**
     * Get the locations stored in this DeliveryList.
     * @return A list of Location objects
     */
    public ArrayList<Location> getLocations() {
        return this.locations;
    }
    
    /**
     * Get a Location object from the list.
     * @param index The index of the Location to retrieve
     * @return The Location object stored at the desired index
     */
    public Location getLocation(int index) {
        return this.locations.get(index);
    }    
    
    public void setSafeAddresses(String[] addresses) {
        
    }
    
    /**
     * Get the number of Locations stored in this DeliveryList.
     * @return The size of the Location list
     */
    public int getSize() {
        return locations.size();
    }
    
    
    
}
