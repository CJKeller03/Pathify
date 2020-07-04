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

import com.calebjkeller.pathify.locationHandling.DeliveryList;
import com.calebjkeller.pathify.locationHandling.Location;
import com.calebjkeller.pathify.wizard.pages.AddressSelectWizardPage;
import com.calebjkeller.pathify.wizard.pages.BaseWizardPage;
import com.calebjkeller.pathify.wizard.pages.TitleWizardPage;
import com.calebjkeller.pathify.wizard.pages.WizardPageInterface;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author caleb
 */
public class PathifyWizardPageGenerator implements WizardPageGeneratorInterface {
    
    private WizardModel model;
    private ArrayList<BaseWizardPage> history;
    
    public PathifyWizardPageGenerator() {
        
    }
    
    public WizardPageInterface nextPage(WizardPanelController controller) {
        
        if (!model.hasObject("doGenerate")) {
            System.out.println("serving title...");
            return new TitleWizardPage("title", controller);
        }
        
        String[] arr = {"a", "b"};
        String[] locArr = {"Joe", "Schmo", "100", "somewhere st", "1", "nowhere",
                           "00000", "1234567890", "0", "0", "0", "1", "exists?"};
        
        
        Location tmpLoc = new Location(locArr);
        
        System.out.println("serving select page...");
        return new AddressSelectWizardPage("test", controller, arr, tmpLoc);
        
        DeliveryList list = new DeliveryList((File) model.getObject("csvFile"));
        
        if (! (Boolean) model.getObject("doGenerate")) {
            
        }
        
        return new BaseWizardPage("none", controller);
        
    }
    
    public void setModel(WizardModel model) {
        this.model = model;
    }
    
}
