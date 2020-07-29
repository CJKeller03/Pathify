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

import com.calebjkeller.pathify.locationHandling.Location;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.IntStream;

/**
 * A class used for storing and representing the data used by the ortools solver.
 * @author Caleb Keller
 */
public class SolverDataModel {
    // Initialize vars
    ArrayList<Location> locations;
    String[] addressOrdering;
    HashMap<String, ArrayList<Location>> addressMapping;
    
    public long[][][] costMatrix;
    public long[] demands;
    public long[] vehicleCapacities;
    
    public int vehicleNumber;
    public int depot = 0;
    
    /**
     * Create a SolverDataModel using the cost map from the AddressData class.
     * @param locations The locations it is necessary to visit.
     * @param vehicles An array representing the number and capacity of available vehicles.
     */
    public SolverDataModel(ArrayList<Location> locations,int[] vehicles) {
        this(AddressData.getADF().costMap, locations, vehicles);
    }
    
    /**
     * Create a SolverDataModel using a provided cost map.
     * @param costMap The cost map to use for generating the cost matrix.
     * @param locations The locations it is necessary to visit.
     * @param vehicles An array representing the number and capacity of available vehicles.
     */
    public SolverDataModel(HashMap<String, long[]> costMap, ArrayList<Location> locations,
                           int[] vehicles){
        
        // Init vars
        this.locations = (ArrayList) locations.clone();
        this.locations.get(0).setDemand(0);
        this.addressMapping = new HashMap<String, ArrayList<Location>>();
        
        // Associate every unique address with the locations that refer to it
        for (int i = 1; i < this.locations.size(); i++) {
            Location loc = this.locations.get(i);
            String key = loc.getCompleteAddress();
            
            if (this.addressMapping.containsKey(key)) {
                this.addressMapping.get(key).add(loc);
            } else {
                ArrayList tmpList = new ArrayList<Location>();
                tmpList.add(loc);
                this.addressMapping.put(key, tmpList);
            }
        }
        
        // Put every unique address into an arraylist
        ArrayList<String> tmpAddressOrdering = new ArrayList<String>(this.addressMapping.keySet());
        
        ArrayList tmpList = new ArrayList<Location>();
        tmpList.add(this.locations.get(0));
        
        // Add the origin to the address - location(s) mapping
        this.addressMapping.put(this.locations.get(0).getCompleteAddress(), tmpList);
        
        // Add the origin's address to the 0th index in the arraylist
        tmpAddressOrdering.add(0, this.locations.get(0).getCompleteAddress());
        this.addressOrdering = tmpAddressOrdering.toArray(new String[tmpAddressOrdering.size()]);
        
        int numUniqueAddresses = addressOrdering.length;
        
        this.costMatrix = new long[numUniqueAddresses][numUniqueAddresses][2];
        
        // Generate the cost matrix for unique addresses using the cost map
        for (int i = 0; i < numUniqueAddresses; i++) {
            for (int j = 0; j < numUniqueAddresses; j++) {
                if (j != 0) {
                    String key = addressOrdering[i] +
                                 addressOrdering[j];
                    
                    long[] cost = costMap.get(key);
                    
                    if (cost == null) {
                        System.err.println("unable to find cost for: " + key);
                        this.costMatrix[i][j][0] = 0;
                        this.costMatrix[i][j][1] = 0;
                    } else {
                        this.costMatrix[i][j] = cost;
                    }
                    
                } else {
                    this.costMatrix[i][j][0] = 0;
                    this.costMatrix[i][j][1] = 0;
                }
            }
        }
        
        this.demands = new long[numUniqueAddresses];
        
        // Get the demand for each location that must be visited
        for (int i = 0; i < this.demands.length; i++) {
            int totalDemand = 0;
            for (Location loc: this.addressMapping.get(addressOrdering[i])) {
                totalDemand += loc.getDemand();
            }
            this.demands[i] = totalDemand;
        }
        
        // Generate the vehicle capacities array
        this.vehicleCapacities = new long[IntStream.of(vehicles).sum()];
        this.vehicleNumber = this.vehicleCapacities.length;        
        
        int curVehicle = 0;
        for (int i = 0; i < vehicles.length; i++) {
            for (int j = 0; j < vehicles[i]; j++) {
                this.vehicleCapacities[curVehicle] = i;
                curVehicle++;
            }
        }
    }
    
    /**
     * Get the location(s) associated with an index in the cost matrix
     * @param index The index in the cost matrix that refers to the desired locations
     * @return 
     */
    public ArrayList<Location> getLocations(int index) {
        return this.addressMapping.get(this.getAddress(index));
    }
    
    /**
     * Get the address associated with an index in the cost matrix
     * @param index The index in the cost matrix that refers to the desired address
     * @return 
     */
    public String getAddress(int index) {
        return this.addressOrdering[index];
    }
}
