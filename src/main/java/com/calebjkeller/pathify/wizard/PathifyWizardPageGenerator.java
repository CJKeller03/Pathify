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

import com.calebjkeller.pathify.wizard.pages.BaseWizardPage;
import com.calebjkeller.pathify.wizard.pages.TitleWizardPage;
import com.calebjkeller.pathify.wizard.pages.WizardPageInterface;
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
            return new TitleWizardPage("title", controller);
        }
        
        return new BaseWizardPage("none", controller);
        
    }
    
    public void setModel(WizardModel model) {
        this.model = model;
    }
    
}
