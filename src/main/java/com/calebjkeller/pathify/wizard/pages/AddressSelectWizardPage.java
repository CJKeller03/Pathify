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
package com.calebjkeller.pathify.wizard.pages;

import com.calebjkeller.pathify.locationHandling.Location;
import com.calebjkeller.pathify.locationHandling.LocationTools;
import com.calebjkeller.pathify.wizard.WizardModel;
import com.calebjkeller.pathify.wizard.WizardPanelController;
import java.util.ArrayList;

/**
 *
 * @author Caleb Keller
 */
public class AddressSelectWizardPage extends javax.swing.JPanel implements WizardPageInterface {
    
    private String ID;
    private WizardPanelController controller;
    
    private String descriptionText = "<html>The address for %s : %s couldn't be verified. "
            + "Please select the correct address from the list below. "
            + "If none of the below addresses are correct, skip it to verify later, "
            + "or enter a different address to search for.</html>";
    
    private String[] addressOptions;
    private Location loc;
    
    private boolean skipEnabled;
    private boolean isSkip = false;
    private boolean customAddressMode = false;
    
    /**
     * Creates new form WizardPage
     */
    public AddressSelectWizardPage(String ID, WizardPanelController controller,
                                   Location loc, String[] options, boolean skipEnabled) {
        this.ID = ID;
        this.controller = controller;
        this.loc = loc;
        this.skipEnabled = skipEnabled;
        
        addressOptions = options;
        
        initComponents();
    }
    
    public String getID() {
        return this.ID;
    }
    
    public void enable() {
        controller.setFinish(false);
        controller.setNext(false);
        controller.setQuit(true);
        System.out.println("select page started!");
        
        // SKIPS PAGE AUTOMATICALLY - REMOVE FROM HERE
        
        addressSelectBox.setSelectedIndex(0);
        controller.pullNextPage();
        
        // TO HERE
    }
    
    public void disable(WizardModel model) {
        if (!isSkip) {
            model.setObject("selectedAddress", addressSelectBox.getSelectedValue());
        } else {
            ArrayList skippedAddresses;
            
            if (!model.hasObject("skippedAddresses")) {
                skippedAddresses = new ArrayList<Location>();
                model.setObject("skippedAddresses", skippedAddresses);
            } else {
                skippedAddresses = (ArrayList) model.getObject("skippedAddresses");
            }
            
            skippedAddresses.add(loc);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        addressSelectBox = new javax.swing.JList(this.addressOptions);
        description = new javax.swing.JLabel();
        skipButton = new javax.swing.JButton();
        enterAddressButton = new javax.swing.JButton();
        userAddressField = new javax.swing.JTextField();

        addressSelectBox.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        addressSelectBox.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                addressSelectBoxValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(addressSelectBox);

        description.setText(String.format(this.descriptionText, loc.firstName + " " + loc.lastName, loc.getUniqueAddress()));

        skipButton.setText("Skip");
        skipButton.setEnabled(this.skipEnabled);
        skipButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                skipButtonActionPerformed(evt);
            }
        });

        enterAddressButton.setText("Enter Different Address");
        enterAddressButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enterAddressButtonActionPerformed(evt);
            }
        });

        userAddressField.setBackground(new java.awt.Color(153, 153, 153));
        userAddressField.setForeground(new java.awt.Color(255, 255, 255));
        userAddressField.setText("Enter address (including zip code and city, if known)");
        userAddressField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                userAddressFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                userAddressFieldFocusLost(evt);
            }
        });
        userAddressField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userAddressFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(description, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(enterAddressButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(skipButton))
                    .addComponent(userAddressField, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(description, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(userAddressField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(skipButton)
                    .addComponent(enterAddressButton))
                .addContainerGap())
        );

        userAddressField.setVisible(false);
    }// </editor-fold>//GEN-END:initComponents

    private void addressSelectBoxValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_addressSelectBoxValueChanged
        if (addressSelectBox.getSelectedValue() != null) {
            controller.setNext(true);
        } else {
            controller.setNext(false);
        }
    }//GEN-LAST:event_addressSelectBoxValueChanged
    
    private void skipButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_skipButtonActionPerformed
        isSkip = true;
        controller.pullNextPage();
    }//GEN-LAST:event_skipButtonActionPerformed

    private void enterAddressButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterAddressButtonActionPerformed
        if (!customAddressMode) {
            controller.setNext(false);
            customAddressMode = true;
            
            enterAddressButton.setText("Validate");
            enterAddressButton.setEnabled(false);
            
            userAddressField.setVisible(true);
            jScrollPane1.setVisible(false);

            this.updateUI();
        } else {
            
            customAddressMode = false;   
            
            addressOptions = LocationTools.getArcGISCandidates(userAddressField.getText());
            addressSelectBox.setListData(addressOptions);
            
            description.setText(String.format(descriptionText, loc.firstName + " " + loc.lastName, 
                                                               userAddressField.getText()));
            enterAddressButton.setText("Enter Different Address");
            enterAddressButton.setEnabled(true);
            
            userAddressField.setVisible(false);
            jScrollPane1.setVisible(true);
            
        }
    }//GEN-LAST:event_enterAddressButtonActionPerformed

    private void userAddressFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_userAddressFieldFocusGained
        if (userAddressField.getText().equals("Enter address (including zip code and city, if known)")) {
            userAddressField.setText("");
        }
    }//GEN-LAST:event_userAddressFieldFocusGained

    private void userAddressFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_userAddressFieldFocusLost
        if (customAddressMode) {
            String userText = userAddressField.getText();
            if (userText == null || userText.equals("")) {
                userAddressField.setText("Enter address (including zip code and city, if known)");
                enterAddressButton.setEnabled(false);
            } else {
                enterAddressButton.setEnabled(true);
            }
        }
    }//GEN-LAST:event_userAddressFieldFocusLost

    private void userAddressFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userAddressFieldActionPerformed
        if (customAddressMode) {
            String userText = userAddressField.getText();
            if (!(userText == null || userText.equals(""))) {
                enterAddressButton.setEnabled(true);
            } else {
                enterAddressButton.setEnabled(false);
            }
        }
    }//GEN-LAST:event_userAddressFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> addressSelectBox;
    private javax.swing.JLabel description;
    private javax.swing.JButton enterAddressButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton skipButton;
    private javax.swing.JTextField userAddressField;
    // End of variables declaration//GEN-END:variables
}
