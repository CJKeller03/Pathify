/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calebjkeller.pathify;

import java.util.ArrayList;

        
/**
 *
 * @author caleb
 */
public class App {
    public static void main(String[] args) {
        String pathToCsv = "M25OC Home Delivery List - CSV (Incomplete).csv";
        ArrayList<String[]> mat;
        try {
            mat = Tools.importAddressList(pathToCsv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        

    }
}
