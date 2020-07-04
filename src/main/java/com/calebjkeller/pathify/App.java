/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calebjkeller.pathify;

import com.calebjkeller.pathify.locationHandling.Location;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

        
/**
 *
 * @author Caleb Keller
 */
public class App {
    public static void main(String[] args) {
         
        String test = "1: %s 2: %s 3: %s";
        
        String[] params = {"a","b","c"};
        test = String.format(test, params);
        
        System.out.println(test);
        
        
        String pathToCsv = "M25OC Home Delivery List - CSV (Non-Locked only).csv";
        ArrayList<Location> locations;
        
        try {
            locations = Tools.importDeliveryList(pathToCsv);
            
            
            
            
            /*
            for (Location loc : locations) {
                System.out.println(loc.toString());
            }     
            */
            
            //HashMap<String, Long[]> addressMatrix = Tools.generateDistanceMatrix(locations);
            
            /*
            
            for (HashMap.Entry<String, Long[]> entry : addressMatrix.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue()[0] + " , " + entry.getValue()[1]);
            }
            
            try {
                FileOutputStream fileOut =
                new FileOutputStream("addressMatrix.ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(addressMatrix);
                out.close();
                fileOut.close();
                
             } catch (IOException i) {
                i.printStackTrace();
             }     
            
            */

            //Tools.generateDistanceMatrix(locations);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
