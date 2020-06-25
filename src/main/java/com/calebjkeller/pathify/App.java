/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calebjkeller.pathify;

import com.calebjkeller.locationHandling.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

        
/**
 *
 * @author caleb
 */
public class App {
    public static void main(String[] args) {
        
        String pathToCsv = "M25OC Home Delivery List - CSV (Non-Locked only).csv";
        ArrayList<Location> locations;
        
        try {
            locations = Tools.importDeliveryList(pathToCsv);
            
            
            for (Location loc : locations) {
                System.out.println(loc.toString());
            }     
            
            
            HashMap<String, Long[]> addressMatrix = Tools.generateDistanceMatrix(locations);
            
            for (HashMap.Entry<String, Long[]> entry : addressMatrix.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue()[0] + " , " + entry.getValue()[1]);
            }
            
            //Tools.generateDistanceMatrix(locations);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
