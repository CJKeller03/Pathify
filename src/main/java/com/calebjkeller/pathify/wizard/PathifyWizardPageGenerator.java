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
package com.calebjkeller.pathify.wizard;

import com.calebjkeller.pathify.RouteSolver;
import com.calebjkeller.pathify.SolverDataModel;
import com.calebjkeller.pathify.Tools;
import com.calebjkeller.pathify.locationHandling.DeliveryList;
import com.calebjkeller.pathify.locationHandling.Location;
import com.calebjkeller.pathify.locationHandling.Route;
import com.calebjkeller.pathify.wizard.pages.AddressSelectWizardPage;
import com.calebjkeller.pathify.wizard.pages.BaseWizardPage;
import com.calebjkeller.pathify.wizard.pages.LoadingWizardPage;
import com.calebjkeller.pathify.wizard.pages.RouteDisplayPage;
import com.calebjkeller.pathify.wizard.pages.TitleWizardPage;
import com.calebjkeller.pathify.wizard.pages.WizardPageInterface;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author caleb
 */
public class PathifyWizardPageGenerator implements WizardPageGeneratorInterface {
    
    private WizardModel model;
    private ArrayList<BaseWizardPage> history;
    
    private DeliveryList list;
    
    private String state = "start";
    
    private boolean enabled = true;
    private int curLocation;
    
    public PathifyWizardPageGenerator() {
        
    }
    
    public WizardPageInterface nextPage(WizardPanelController controller) {
        
        while (enabled) {
            switch (state) {
                case "start":
                    
                    state = "started";
                    return new TitleWizardPage("title", controller);
                    
                case "started":
                    list = new DeliveryList((File) model.getObject("csvFile"));
                    
                    if ((Boolean) model.getObject("doGenerate")) {
                        state = "generateAddresses";
                    }
                    
                    break;

                case "generateAddresses":

                    if (!model.hasObject("selectedAddress")) {
                        curLocation = 0;
                    } else {
                        String selectedAddress = (String) model.getObject("selectedAddress");
                        if (!selectedAddress.equals("SKIP")) {
                            list.getLocation(curLocation).setSafeAddress(selectedAddress);
                        }
                        curLocation++;
                    }
                    
                    if (curLocation == list.getSize()) {
                        
                        if (model.hasObject("skippedAddresses")) {
                            state = "generateSkipped";
                        } else {
                            state = "generateRoutes";
                        }
                        
                    } else {
                        return new AddressSelectWizardPage("select", controller, list.getLocation(curLocation)); 
                    }
                    
                    break;
                    
                case "generateSkipped":
                    enabled = false;
                    
                    break;
                    
                case "generateRoutes":
                    controller.pushNextPage(new LoadingWizardPage("loading", controller));
                    
                    
                    int n = 0;
                    for (Location loc: (ArrayList<Location>) list.getLocations()) {
                        loc.testPosition[0] = n % 3;
                        loc.testPosition[1] = n / 3;
                        n+=1;
                    }
                    
                    
                    //HashMap<String, long[]> costMatrix = Tools.generateDistanceMatrix(list.getLocations());
                    HashMap costMatrix = Tools.generateFakeDistanceMatrix(list.getLocations());
                    
                    int[] tmpVehicles = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10};
                    SolverDataModel dataModel = new SolverDataModel(costMatrix, list.getLocations(), tmpVehicles);
                    ArrayList<Route> routes = RouteSolver.solve(dataModel);
                    
                    for (Route route: routes) {
                        System.out.println("Next route:");
                        System.out.println(route.toString());
                    }
                    
                    enabled = false;
                    return new RouteDisplayPage("display", controller, routes.get(0));
            }
        }
        
        
        return new BaseWizardPage("none", controller);
        
    }
    
    public void setModel(WizardModel model) {
        this.model = model;
    }
    
}
