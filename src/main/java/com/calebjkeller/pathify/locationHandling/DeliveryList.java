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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 * @author caleb
 */
public class DeliveryList {
    
    private ArrayList<Location> locations;
    private boolean isSafe;
    
    public DeliveryList(File csvList) {
        locations = this.generateLocations(csvList);
        isSafe = false;
    }
    
    public DeliveryList(File csvList, File addressTable) {
        isSafe = true;
    }
    
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
    
    public ArrayList<Location> getLocations() {
        return this.locations;
    }
    
    public Location getLocation(int index) {
        return this.locations.get(index);
    }    
    
    public void setSafeAddresses(String[] addresses) {
        
    }
    
    public int getSize() {
        return locations.size();
    }
    
    
    
}
