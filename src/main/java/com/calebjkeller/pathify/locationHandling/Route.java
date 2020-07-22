/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calebjkeller.pathify.locationHandling;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Contains information about a route visiting multiple Locations.
 * 
 * @author Caleb Keller
 */
public class Route {
    ArrayList<Location> locations;
    
    long totalDistance;
    long approximateTime;
    long numBoxes;
    
    public Route() {
        this.locations = new ArrayList<Location>();
        this.totalDistance = 0;
        this.approximateTime = 0;
        this.numBoxes = 0;
    }
    
    public Route(ArrayList<Location> locations, long distance, long time, long numBoxes) {
        this.locations = locations;
        this.totalDistance = distance;
        this.approximateTime = time;
        this.numBoxes = numBoxes;
    }
    
    public void addLocation(Location loc) {
        this.locations.add(loc);
        this.numBoxes += loc.boxDemand;
    }
    
    public void addLocations(ArrayList<Location> locations) {
        for (Location loc: locations) {
            this.locations.add(loc);
        }
    }
    
    public void setDistance(long dist) {
        this.totalDistance = dist;
    }
    
    public long getDistance() {
        return this.totalDistance;
    }
    
    public void setTime(int time) {
        this.approximateTime = time;
    }
    
    public long getTime() {
        return 0l;
    }
    
    public void setNumBoxes(long numBoxes) {
        this.numBoxes = numBoxes;
    }
    
    public long getNumBoxes() {
        return this.numBoxes;
    }
    
    public String toString() {
        String out = "";
        for (Location loc: this.locations) {
            out += loc.firstName + " ";
            out += loc.lastName + " -> ";
            out += loc.testPosition[0] + " " + loc.testPosition[1] + " ), ";
        }
        out += "\n";
        out += "Total distance: " + this.totalDistance;
        
        return out;
    }
    
    public String[][] getAsTable() {
        
        String[][] out = new String[this.locations.size()][4];
        
        int i = 0;
        for (Location loc: this.locations) {
            String tags = "<html>%s</html>";
            out[i][0] = String.format(tags, loc.firstName + " " + loc.lastName);
            out[i][1] = String.format(tags, loc.getSafeAddress());
            out[i][2] = String.format(tags, loc.phoneNumber);
            out[i][3] = String.format(tags, loc.notes);
            i++;
        }
        
        return out;
    }
    
    public BufferedImage getGoogleMapsQRCode(int width, int height) {
        String url = "https://www.google.com/maps/dir/?api=1&origin=%s&waypoints=%s&destination=%s";
        String charset = java.nio.charset.StandardCharsets.UTF_8.name();
        
        ArrayList<String> addresses = new ArrayList<String>();
        for (Location loc: this.locations) {
            addresses.add(loc.getSafeAddress());
        }
        
        String waypoints = String.join("|", addresses.subList(1, addresses.size() - 1));
        
        try {
            url = String.format(url,
                                URLEncoder.encode(addresses.get(0), charset),
                                URLEncoder.encode(waypoints, charset),
                                URLEncoder.encode(addresses.get(addresses.size() - 1), charset));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        return this.generateQRCode(url, width, height);
    }
    
    public BufferedImage getMapquestQRCode(int width, int height) {
        String url = "http://www.mapquest.com/directions?q=%s";
        String charset = java.nio.charset.StandardCharsets.UTF_8.name();
        
        ArrayList<String> addresses = new ArrayList<String>();
        for (Location loc: this.locations) {
            addresses.add(loc.getSafeAddress());
        }
        
        try {
            for (int i = 0; i < addresses.size(); i++) {
                addresses.set(i, URLEncoder.encode(addresses.get(i),charset));
            }
            
            url = String.format(url, String.join("to:",(addresses.subList(0, addresses.size()))));
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        return this.generateQRCode(url, width, height);
    }    
    
    private BufferedImage generateQRCode(String url, int width, int height) {
        QRCodeWriter writer = new QRCodeWriter();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // create an empty image
        
        int white = 255 << 16 | 255 << 8 | 255;
        int black = 0;
        
        BitMatrix bitMatrix;
        
        try {
            bitMatrix = writer.encode(url, BarcodeFormat.QR_CODE, width, height);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                image.setRGB(i, j, bitMatrix.get(i, j) ? black : white); // set pixel one by one
            }
        }
        
        return image;
    }
    
}
