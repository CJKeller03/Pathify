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
package com.calebjkeller.pathify.wizard;

import com.calebjkeller.pathify.AddressData;
import com.calebjkeller.pathify.RouteSolver;
import com.calebjkeller.pathify.SolverDataModel;
import com.calebjkeller.pathify.Tools;
import com.calebjkeller.pathify.locationHandling.DeliveryList;
import com.calebjkeller.pathify.locationHandling.Location;
import com.calebjkeller.pathify.locationHandling.LocationTools;
import com.calebjkeller.pathify.locationHandling.Route;
import com.calebjkeller.pathify.wizard.pages.AddressSelectWizardPage;
import com.calebjkeller.pathify.wizard.pages.BaseWizardPage;
import com.calebjkeller.pathify.wizard.pages.LoadingWizardPage;
import com.calebjkeller.pathify.wizard.pages.RouteDisplayPage;
import com.calebjkeller.pathify.wizard.pages.TitleWizardPage;
import com.calebjkeller.pathify.wizard.pages.WizardPageInterface;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * A class for dynamically generating Wizard pages.
 * @author Caleb Keller
 */
public class PathifyWizardPageGenerator implements WizardPageGeneratorInterface {
    
    private WizardModel model;
    private ArrayList<BaseWizardPage> history;
    
    private DeliveryList list;
    
    private String state = "start";
    
    private boolean enabled = true;
    private int curLocation;
    
    private ArrayList<Location> skippedLocations;
    private ArrayList<String[]> skippedLocationOptions;
    private String[] currentLocationOptions;
    
    /**
     * Create a WizardPageGenerator instance
     */
    public PathifyWizardPageGenerator() {
        
    }
    
    /**
     * Get a new page linked to the provided controller from the page generator.
     * @param controller The controller to link the new page to
     * @return The next WizardPage to display
     */
    public WizardPageInterface nextPage(WizardPanelController controller) {
        
        while (enabled) {
            switch (state) {
                case "start":
                    
                    state = "started";
                    return new TitleWizardPage("title", controller);
                    
                case "started":
                    list = new DeliveryList((File) model.getObject("csvFile"));
                    if (model.hasObject("adfFile")) {
                        AddressData.loadADF((File) model.getObject("adfFile"));
                    } else {
                        AddressData.createNew(null);
                    }
                    
                    this.skippedLocations = new ArrayList<Location>();
                    this.skippedLocationOptions = new ArrayList<String[]>();
                    
                    state = "validateAddresses-Serve";
                    curLocation = 0;
                    
                    break;

                case "validateAddresses-Serve":
                    
                    if (curLocation < list.getSize()) {
                        Location loc = list.getLocation(curLocation);
                        currentLocationOptions = LocationTools.getArcGISCandidates(loc);
                        
                        boolean foundMatch = false;
                        
                        for (String option: currentLocationOptions) {
                            if (AddressData.addressExists(option)) {
                                loc.setSafeAddress(option);
                                curLocation++;                                 
                                foundMatch = true;
                                break;
                            }
                        }
                        
                        if (foundMatch) {
                           break;
                        }
                        
                        state = "validateAddresses-Store";
                        return new AddressSelectWizardPage("select", controller, loc, currentLocationOptions, true);
                        
                    } else if (skippedLocations.size() > 0) {
                        curLocation = 0;
                        state = "validateSkipped-Serve";
                    } else {
                        state = "generateRoutes";
                    }
                    
                    break;
                    
                case "validateAddresses-Store":
                    
                    String selectedAddress = (String) model.getObject("selectedAddress");
                    
                    if (selectedAddress.equals("SKIP")) {
                        skippedLocations.add(list.getLocation(curLocation));
                        skippedLocationOptions.add(currentLocationOptions);                        
                    } else {
                        list.getLocation(curLocation).setSafeAddress(selectedAddress);
                        AddressData.addAddress(selectedAddress);
                    }
                    
                    curLocation++;
                    state = "validateAddresses-Serve";
                    break;
                    
                case "validateSkipped-Serve":
                    
                    if (curLocation < skippedLocations.size()) {
                        Location loc = skippedLocations.get(curLocation);
                        currentLocationOptions = skippedLocationOptions.get(curLocation);
                        state = "validateSkipped-Store";
                        return new AddressSelectWizardPage("select", controller, loc, currentLocationOptions, false);
                    } else {
                        state = "generateRoutes";
                    }
                    
                    break;
                    
                case "validateSkipped-Store":
                    
                    selectedAddress = (String) model.getObject("selectedAddress");

                    list.getLocation(curLocation).setSafeAddress(selectedAddress);
                    AddressData.addAddress(selectedAddress);
                    
                    curLocation++;
                    state = "validateAddresses-Serve";
                    break;                    
                    
                case "generateRoutes":
                    controller.pushNextPage(new LoadingWizardPage("loading", controller));
                    
                    // Generates fake coordinates for the fake distance matrix
                    /*
                    int n = 0;
                    for (Location loc: list.getLocations()) {
                        loc.testPosition[0] = (n % 3) * 1609;
                        loc.testPosition[1] = (n / 3) * 1609;
                        n+=1;
                    }
                    */
                    
                    HashMap<String, long[]> costMatrix = Tools.generateDistanceMatrix(list.getLocations());
                    AddressData.addCosts(costMatrix);
                    
                    //HashMap costMatrix = Tools.generateFakeDistanceMatrix(list.getLocations());
                    
                    int[] tmpVehicles = {0, 1, 0, 2, 1};
                    SolverDataModel dataModel = new SolverDataModel(list.getLocations(), tmpVehicles);
                    ArrayList<Route> routes = RouteSolver.solve(dataModel);
                    
                    for (Route route: routes) {
                        System.out.println("Next route:");
                        System.out.println(route.toString());
                    }
                    
                    Book routePages = new Book();
                    
                    PrinterJob pj = PrinterJob.getPrinterJob();
                    pj.setJobName(" Print Component ");

                    PageFormat pf = pj.defaultPage();
                    pf.setOrientation(PageFormat.LANDSCAPE);
                    pj.pageDialog(pf);
                    
                    for (int i = 0; i < routes.size(); i++) {
                        if (routes.get(i).getLocations().size() > 1) {
                            RouteDisplayPage page = new RouteDisplayPage("display", controller, routes.get(i), i);
                            routePages.append(page, pf);
                        }
                    }                 
                    
                    pj.setPageable(routePages);
                    
                    if (pj.printDialog() == true) {
                        try {
                              pj.print();
                        } catch (PrinterException ex) {
                              // handle exception
                        }        

                    }
                    
                    JFileChooser chooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter(
                                            "Address Data File", "adf");
                    chooser.setFileFilter(filter);
                    if (chooser.showSaveDialog(controller.getPanel()) == JFileChooser.APPROVE_OPTION) {
                        AddressData.save(chooser.getSelectedFile());
                      // save to file
                    }
                    enabled = false;
                    return new RouteDisplayPage("display", controller, routes.get(0), 0);
                    //break;
            }
        }
        
        
        return new BaseWizardPage("none", controller);
        
    }
    
    /**
     * Set this generator's data model.
     * @param model The model to use
     */
    public void setModel(WizardModel model) {
        this.model = model;
    }
    
}
