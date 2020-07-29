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
 *
 * @author caleb
 */
public class AddressData implements Serializable {
    
    public HashSet<String> addresses;
    public HashMap<String, long[]> costMap;
    private static AddressData adf;
    private static File saveFile;
    
    public static void createNew(File in) {
        saveFile = in;
        adf = new AddressData(new HashSet<String>(), new HashMap<String, long[]>());
    }
    
    public static void loadADF(File in) {
        try {
            saveFile = in;
            adf = (AddressData) new ObjectInputStream(new FileInputStream(in)).readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private AddressData(HashSet<String> addresses, HashMap<String, long[]> costMap) {
        this.addresses = addresses;
        this.costMap = costMap;
    }
    
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
    
    public static boolean addressExists(String address) {
        return adf.addresses.contains(address);
    }
    
    public static boolean hasCost(String key) {
        return adf.costMap.containsKey(key);
    }
    
    public static long[] getCost(String key) {
        return adf.costMap.get(key);
    }
    
    public static void addAddress(String address) {
        adf.addresses.add(address);
    }
    
    public static void addAddresses(ArrayList<String> addresses) {
        adf.addresses.addAll(addresses);
    }
    
    public static void addCost(String key, long[] cost) {
        adf.costMap.put(key, cost);
    }
    
    public static void addCosts(HashMap<String, long[]> costs) {
        adf.costMap.putAll(costs);
    }
    
    public static AddressData getADF() {
        return adf;
    }
    
    
}
