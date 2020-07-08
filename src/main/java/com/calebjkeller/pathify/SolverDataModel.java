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

import com.calebjkeller.pathify.locationHandling.Location;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.IntStream;

/**
 *
 * @author caleb
 */
public class SolverDataModel {
    ArrayList<Location> locations;
    String[] addressOrdering;
    HashMap<String, ArrayList<Location>> addressMapping;
    
    public long[][] costMatrix;
    public long[] demands;
    public long[] vehicleCapacities;
    
    public int vehicleNumber;
    public int depot = 0;
    
    public SolverDataModel(HashMap<String, long[]> costMap, ArrayList<Location> locations,
                           int[] vehicles){
        
        this.locations = (ArrayList) locations.clone();
        this.locations.get(0).setDemand(0);
        this.addressMapping = new HashMap<String, ArrayList<Location>>();
        
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
        
        ArrayList<String> tmpAddressOrdering = new ArrayList<String>(this.addressMapping.keySet());
        
        ArrayList tmpList = new ArrayList<Location>();
        tmpList.add(this.locations.get(0));
        this.addressMapping.put(this.locations.get(0).getCompleteAddress(), tmpList);
        
        tmpAddressOrdering.add(0, this.locations.get(0).getCompleteAddress());
        this.addressOrdering = tmpAddressOrdering.toArray(new String[tmpAddressOrdering.size()]);
        
        int numUniqueAddresses = addressOrdering.length;
        
        this.costMatrix = new long[numUniqueAddresses][numUniqueAddresses];
        
        for (int i = 0; i < numUniqueAddresses; i++) {
            for (int j = 0; j < numUniqueAddresses; j++) {
                if (j != 0) {
                    String key = addressOrdering[i] +
                                 addressOrdering[j];
                    
                    long[] cost = costMap.get(key);
                    
                    if (cost == null) {
                        System.err.println("unable to find cost for: " + key);
                        this.costMatrix[i][j] = 0;
                    } else {
                        this.costMatrix[i][j] = cost[0];
                    }
                    
                } else {
                    this.costMatrix[i][j] = 0;
                }
            }
        }
        
        this.demands = new long[numUniqueAddresses];
        
        for (int i = 0; i < this.demands.length; i++) {
            int totalDemand = 0;
            for (Location loc: this.addressMapping.get(addressOrdering[i])) {
                totalDemand += loc.getDemand();
            }
            this.demands[i] = totalDemand;
        }
        
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
    
    public ArrayList<Location> getLocations(int index) {
        return this.addressMapping.get(this.getAddress(index));
    }
    
    public String getAddress(int index) {
        return this.addressOrdering[index];
    }
}
