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

import com.calebjkeller.pathify.wizard.pages.WizardPageInterface;
import javax.swing.JFrame;

/**
 *
 * @author caleb
 */
public class WizardPanelController {
    
    private WizardModel model;
    private WizardPanel panel;
    private WizardPageGeneratorInterface generator;
    private WizardPageInterface curPage;
    
    private JFrame frame;
    
    public WizardPanelController(WizardPageGeneratorInterface generator, JFrame frame) {
        panel = new WizardPanel(this);
        model = new WizardModel();
        
        this.frame = frame;
        this.generator = generator;
        this.generator.setModel(model);
        
        this.next();
    }
    
    public void quit() {
        frame.setVisible(false);
        frame.dispose();
    }
    
    public void next() {
        if (curPage != null) {
            curPage.disable(model);
        }
        curPage = generator.nextPage(this);
        curPage.enable();
        
        panel.displayPage(curPage);
    }
    
    public void finish() {
        
    }
    
    
    // Methods that affect the WizardPanel
    public WizardPanel getPanel() {
        return this.panel;
    }
    
    public void setNext(boolean isEnabled) {
        this.panel.nextButton.setEnabled(isEnabled);
    }
    
    public void setFinish(boolean isEnabled) {
        this.panel.finishButton.setEnabled(isEnabled);
    } 
    
    public void setQuit(boolean isEnabled) {
        this.panel.quitButton.setEnabled(isEnabled);
    }    
}
