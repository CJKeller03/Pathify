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
package com.calebjkeller.pathify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * A class for storing and retrieving properly formatted addresses and the 
 * travel costs between them.
 * 
 * @author Caleb Keller
 */
public class AddressData implements Serializable {
    
    // Stores all properly formatted addresses
    public HashSet<String> addresses;
    
    // Stores the costs associated with traveling from one address in the
    // above list to the other (formatted as distance, time)
    public HashMap<String, long[]> costMap;
    
    // The object used to store the data (necessary for enabling serialization)
    private static AddressData adf;
    
    private static File saveFile;
    
    /**
     * Create a new, empty address data file and store the data in the provided file.
     * @param in The file to store the object in
     */
    public static void createNew(File in) {
        saveFile = in;
        adf = new AddressData(new HashSet<String>(), new HashMap<String, long[]>());
    }
    
    /**
     * Load the address data from an address data file stored on disk.
     * @param in The file to read the address data from
     */
    public static void loadADF(File in) {
        try {
            saveFile = in;
            adf = (AddressData) new ObjectInputStream(new FileInputStream(in)).readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Create an AddressData object. This is used solely to enable serialization
     * so that the data can be saved to disk.
     * @param addresses A set of addresses to initialize with
     * @param costMap A map storing travel costs to initialize with
     */
    private AddressData(HashSet<String> addresses, HashMap<String, long[]> costMap) {
        this.addresses = addresses;
        this.costMap = costMap;
    }
    
    /**
     * Save the current address data to disk.
     * @param out The file to save the data to
     */
    public static void save(File out) {
        if (out != null) {
            saveFile = out;
        }
        
        try {
            new ObjectOutputStream(new FileOutputStream(saveFile)).writeObject(adf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Check if the input address is recognized as a standard, existing, address.
     * @param address The address to check
     * @return Whether the address is recognized
     */
    public static boolean addressExists(String address) {
        return adf.addresses.contains(address);
    }
    
    /**
     * Check if there is a known travel cost for this address - address key.
     * @param key The origin - destination pair to check for
     * @return Whether there is a travel cost associated with this key
     */
    public static boolean hasCost(String key) {
        return adf.costMap.containsKey(key);
    }
    
    /**
     * Retrieve the travel cost associated with this origin - destination key.
     * @param key The origin - destination pair to retrieve the cost for
     * @return The travel cost
     */
    public static long[] getCost(String key) {
        return adf.costMap.get(key);
    }
    
    /**
     * Add an address to the list of recognized addresses.
     * @param address The address to add
     */
    public static void addAddress(String address) {
        adf.addresses.add(address);
    }
    
    /**
     * Add multiple addresses to the list of recognized addresses.
     * @param addresses The list of addresses to add
     */
    public static void addAddresses(ArrayList<String> addresses) {
        adf.addresses.addAll(addresses);
    }
    
    /**
     * Associate a travel cost with a origin - destination key.
     * @param key The key to associate the cost with
     * @param cost The travel cost
     */
    public static void addCost(String key, long[] cost) {
        adf.costMap.put(key, cost);
    }
    
    /**
     * Add multiple key - cost pairs to the cost map.
     * @param costs A map encoding the key - cost pairs
     */
    public static void addCosts(HashMap<String, long[]> costs) {
        adf.costMap.putAll(costs);
    }
    
    /**
     * Get the AddressData object used to store all of the data.
     * @return An AddressData object
     */
    public static AddressData getADF() {
        return adf;
    }
    
    
}
