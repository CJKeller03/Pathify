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
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * Contains information about a route visiting multiple Locations.
 * 
 * @author Caleb Keller
 */
public class Route {
    
    public static final double METERS_PER_MILE = 1609.344;
    
    ArrayList<Location> locations;
    
    long totalDistance; //In meters
    long approximateTime; //In seconds
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
    
    public ArrayList<Location> getLocations() {
        return this.locations;
    }
    
    public ArrayList<Location> getStops() {
        return (ArrayList) this.locations.subList(1, this.locations.size());
    }
    
    public Location getOrigin() {
        return this.locations.get(0);
    }
    
    public void setDistanceMeters(long dist) {
        this.totalDistance = dist;
    }
    
    public long getDistanceMeters() {
        return this.totalDistance;
    }
    
    public double getDistanceMiles() {
        DecimalFormat df = new DecimalFormat("##0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return Double.valueOf(df.format(this.totalDistance / METERS_PER_MILE));
    }
    
    public void setTime(long time) {
        this.approximateTime = time;
    }
    
    public long getTime() {
        return 0l;
    }
    
    public String getTimeString() {
        
        long hours = this.approximateTime / 3600;
        long minutes = (this.approximateTime % 3600) / 60;
        long seconds = (this.approximateTime % 3600) % 60;
        
        return String.valueOf(hours) + " hours, " +
               String.valueOf(minutes) + " minutes, and " +
               String.valueOf(seconds) + " seconds";
        
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
        if (this.locations.size() <= 1) {
            return null;
        }
        
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
        if (this.locations.size() <= 1) {
            return null;
        }        
        
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
    
    public BufferedImage getMap(int width, int height) {
        if (this.locations.size() <= 1) {
            return null;
        }
        
        width = Math.min(width, 640);
        height = Math.min(height, 640);
        
        String url = "https://maps.googleapis.com/maps/api/staticmap?%s&size=%sx%s&scale=2&key=%s";
        String charset = java.nio.charset.StandardCharsets.UTF_8.name();
        String key;
        
        try {
            key = Files.readAllLines(Paths.get("ApiKeys.txt")).get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        ArrayList<String> markers = new ArrayList<String>();
        if (this.locations.size() < 15) { 
            
            try {
                for (Location loc: this.locations) {
                    markers.add(URLEncoder.encode(loc.getSafeAddress()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            
        } else {
            
            try {
                for (Location loc: this.locations) {
                    double[] coords = LocationTools.geocodeAddress(loc.getSafeAddress());
                    markers.add(URLEncoder.encode(String.valueOf(coords[0]) + "," + String.valueOf(coords[1])));
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }            
            
        }
        
        ArrayList<String> markerParams = new ArrayList<String>();
        markerParams.add("markers=color:blue%7Csize:mid%7C" + markers.get(0));
        for (int i = 1; i < markers.size(); i++) {
            String letter = (i-1) >= 0 && (i-1) < 26 ? String.valueOf((char)((i-1) + 'A')) : null;
            markerParams.add("markers=color:red%7Csize:mid%7Clabel:" + letter + "%7C" + markers.get(i));
        }
        
        url = String.format(url, String.join("&", markerParams), 
                                 String.valueOf(width),
                                 String.valueOf(height),
                                 key);
        
        try {
            return ImageIO.read(new URL(url));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }
    
}
