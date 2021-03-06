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

import com.calebjkeller.pathify.wizard.pages.WizardPageInterface;
import java.awt.CardLayout;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;

/**
 * A JPanel for handling switching between WizardPage objects.
 * @author Caleb Keller
 */
public class WizardPanel extends javax.swing.JPanel {

    private WizardPanelController controller;
    
    private WizardPageInterface curPage;
    
    /**
     * Creates new form WizardPanel
     */
    public WizardPanel(WizardPanelController controller) {
        this.controller = controller;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pageArea = new javax.swing.JPanel();
        finishButton = new javax.swing.JButton();
        quitButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(400, 300));

        pageArea.setPreferredSize(new java.awt.Dimension(400, 300));
        pageArea.setLayout(new java.awt.CardLayout());

        finishButton.setText("Finish");
        finishButton.setDefaultCapable(false);
        finishButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finishButtonActionPerformed(evt);
            }
        });

        quitButton.setText("Quit");
        quitButton.setDefaultCapable(false);
        quitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitButtonActionPerformed(evt);
            }
        });

        nextButton.setText("Next");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });
        nextButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nextButtonKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(nextButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(finishButton)
                        .addGap(4, 4, 4)
                        .addComponent(quitButton))
                    .addComponent(pageArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pageArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nextButton)
                    .addComponent(finishButton)
                    .addComponent(quitButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void quitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitButtonActionPerformed
        controller.quit();
    }//GEN-LAST:event_quitButtonActionPerformed

    private void finishButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finishButtonActionPerformed
        controller.finish();
    }//GEN-LAST:event_finishButtonActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        controller.pullNextPage();
    }//GEN-LAST:event_nextButtonActionPerformed

    private void nextButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nextButtonKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            controller.pullNextPage();
        }
    }//GEN-LAST:event_nextButtonKeyPressed
    
    /**
     * Remove the current page from the CardLayout, add the new page, and display it.
     * @param page The new page to display
     */
    public void displayPage(WizardPageInterface page) {
        CardLayout layout = (CardLayout)(this.pageArea.getLayout());
        
        if (curPage != null) {
            this.pageArea.remove( (JPanel) curPage);
        }
        
        this.pageArea.remove( (JPanel) page);
        
        this.pageArea.add( (JPanel) page, page.getID());
        layout.show(this.pageArea, page.getID());
        this.pageArea.updateUI();
        
        curPage = page;
        
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton finishButton;
    public javax.swing.JButton nextButton;
    private javax.swing.JPanel pageArea;
    public javax.swing.JButton quitButton;
    // End of variables declaration//GEN-END:variables
}
