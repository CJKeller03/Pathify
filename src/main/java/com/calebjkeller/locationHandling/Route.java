/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calebjkeller.locationHandling;

import java.util.ArrayList;

/**
 * Contains information about a route visiting multiple Locations.
 * 
 * @author Caleb Keller
 */
public class Route {
    ArrayList<Location> locations;
    
    int totalDistance;
    int approximateTime;
    int numBoxes;
    
    public Route() {
        this.locations = new ArrayList<Location>();
        this.totalDistance = 0;
        this.approximateTime = 0;
        this.numBoxes = 0;
    }
    
    public Route(ArrayList<Location> locations, int distance, int time, int numBoxes) {
        this.locations = locations;
        this.totalDistance = distance;
        this.approximateTime = time;
        this.numBoxes = numBoxes;
    }
    
    public void addLocation(Location loc) {
        this.locations.add(loc);
    }
    
    public void setDistance(int dist) {
        this.totalDistance = dist;
    }
    
    public void setTime(int time) {
        this.approximateTime = time;
    }
    
    public void setNumBoxes(int numBoxes) {
        this.numBoxes = numBoxes;
    }
    
}
