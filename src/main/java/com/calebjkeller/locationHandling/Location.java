/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.calebjkeller.locationHandling;

/**
 * Used to store information about a location that needs deliveries.
 * 
 * @author Caleb Keller
 */
public class Location {
    static final String[] defaultHeader = {"First Name", "Last Name", "House #",
                                            "Street", "Apt #         Lot #",
                                            "City", "Zip Code", "Phone #", "Kids",
                                            "Adults", "Seniors", "Total", "Notes:"};
    
    String firstName;
    String lastName;
    String phoneNumber;
    
    String houseNumber;
    String streetName;
    String aptNumber;
    String cityName;
    String zipCode;
    
    double latitude;
    double longitude;
    
    int numChildren;
    int numAdults;
    int numSeniors;
    
    String notes;
    
    public Location(String[] table) {
        this.overwriteDataFromTable(table, defaultHeader);
    }
    
    public Location(String[] table, String[] header) {
        this.overwriteDataFromTable(table, header);
    }
    
    /**
     * Set all of this Location object's variables from a data table with
     * headers identifying what kind of information is in each index of the table.
     * Currently, only very specific headings work.
     * 
     * @param table An array of Strings containing data about a Location.
     * @param header An array of Strings describing the type of data at each index.
     */
    public void overwriteDataFromTable(String[] table, String[] header) {
        int numArgs = Math.min(table.length, header.length);
        
        for (int i = 0; i < numArgs; i++) {
            switch (header[i]) {
                case "First Name":
                    this.firstName = table[i];
                    break;
                case "Last Name":
                    this.lastName = table[i];
                    break;
                case "House #":
                    this.houseNumber = table[i];
                    break;
                case "Street":
                    this.streetName = table[i];
                    break;
                case "Apt #         Lot #":
                    this.aptNumber = table[i];
                    break;
                case "City":
                    this.cityName = table[i];
                    break;
                case "Zip Code":
                    this.zipCode = table[i];
                    break;
                case "Phone #":
                    this.phoneNumber = table[i];
                    break;
                case "Kids":
                    this.numChildren = Integer.parseInt(table[i]);
                    break;
                case "Adults":
                    this.numAdults = Integer.parseInt(table[i]);
                    break;
                case "Seniors":
                    this.numSeniors = Integer.parseInt(table[i]);
                    break;
                case "Notes:":
                    this.notes = table[i];
                    break;
            }
        }
    }
    
    /**
     * Get the address that uniquely identifies this Location.
     * The zip code is included is it is known.
     * 
     * @return A String representing this Location's address.
     */
    public String getUniqueAddress() {
        String out = "";
        out += this.houseNumber + " ";
        out += this.streetName + " ";
        
        if (this.zipCode != null) {
            out += this.zipCode;
        }
        
        return out;
    }
    
    /**
     * Get the full address for this location, excluding the state it is in.
     * The zip code and city are included if they are known.
     * 
     * @return A String representing this Location's address.
     */
    public String getCompleteAddress(){
        String out = "";
        out += this.houseNumber + " ";
        out += this.streetName + " ";
        
        if (this.cityName != null) {
            out += this.cityName + " ";
        }
        
        if (this.zipCode != null) {
            out += this.zipCode;
        }
        
        return out;
    }
    
    /**
     * Create a String representation of this Location.
     * 
     * @return A String representing this Location.
     */
    public String toString() {
        String out = "";
        out += this.firstName + " ";
        out += this.lastName + " : ";
        out += this.getCompleteAddress();
        
        return out;
    }
    
    /**
     * Check if this location has an address that uniquely identifies it.
     * Currently, this doesn't actually check if the address exists,
     * only that it has a house address and street name.
     * 
     * @return Whether the address for this Location is valid.
     */
    public boolean hasValidAddress() {
        return !(this.houseNumber.matches("[^\\w\\d]+")) && !(this.streetName.matches("[^\\w\\d]+"));
    }
}
