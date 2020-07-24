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


import com.calebjkeller.pathify.locationHandling.Route;
import com.calebjkeller.pathify.wizard.WizardModel;
import com.calebjkeller.pathify.wizard.WizardPanelController;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 *
 * @author Caleb Keller
 */
public class RouteDisplayPage extends javax.swing.JPanel implements WizardPageInterface, Printable {
    
    private JFrame dummyFrame = new JFrame();
    private int routeNumber;
    private String ID;
    private WizardPanelController controller;
    
    private Route route;
    private TableColumnAdjuster tca;
    
    
    /**
     * Creates new form WizardPage
     */
    public RouteDisplayPage(String ID, WizardPanelController controller,
                            Route route, int routeNumber) {
        this.routeNumber = routeNumber;
        this.route = route;
        this.ID = ID;
        this.controller = controller;
        initComponents();
        
        this.dummyFrame.add(this);
        //resizeRows();
    }
    
    public String getID() {
        return this.ID;
    }
    
    public void enable() {
        controller.setFinish(false);
        controller.setNext(false);
        controller.setQuit(true);
    }
    
    public void disable(WizardModel model) {
        this.dummyFrame.dispose();
    }
    
    private void resizeRows() {
        for (int i = 0; i < addressTable.getRowCount(); i++) {
            Component comp = addressTable.prepareRenderer(addressTable.getCellRenderer(i, 3), i, 3);
            addressTable.setRowHeight(i, comp.getPreferredSize().height);
        }
    }
    
    public int print(Graphics g, PageFormat pf, int pageNumber) throws PrinterException {

        Component comp = this;

        // Get the preferred size ofthe component...
        Dimension compSize = comp.getPreferredSize();
        // Make sure we size to the preferred size
        comp.setSize(compSize);
        // Get the the print size
        Dimension printSize = new Dimension();
        printSize.setSize(pf.getImageableWidth(), pf.getImageableHeight());

        // Calculate the scale factor
        double scaleFactor = 1d;

        if (compSize != null && printSize != null) {
            double dScaleWidth = (double) printSize.width / (double) compSize.width;
            double dScaleHeight = (double) printSize.height / (double) compSize.height;

            scaleFactor = Math.min(dScaleHeight, dScaleWidth);
        }

        // Calcaulte the scaled size...
        double scaleWidth = compSize.width * scaleFactor;
        double scaleHeight = compSize.height * scaleFactor;

        // Create a clone of the graphics context.  This allows us to manipulate
        // the graphics context without begin worried about what effects
        // it might have once we're finished
        Graphics2D g2 = (Graphics2D) g.create();
        // Calculate the x/y position of the component, this will center
        // the result on the page if it can
        double x = ((pf.getImageableWidth() - scaleWidth) / 2d) + pf.getImageableX();
        double y = ((pf.getImageableHeight() - scaleHeight) / 2d) + pf.getImageableY();
        
        // Create a new AffineTransformation
        AffineTransform at = new AffineTransform();
        
        // Translate the offset to out "center" of page
        at.translate(x, y);
        
        // Set the scaling
        at.scale(scaleFactor, scaleFactor);
        
        // Apply the transformation
        g2.transform(at);
        
        // Print the component

        this.dummyFrame.pack();
        comp.print(g2);
        
        // Dispose of the graphics context, freeing up memory and discarding
        // our changes
        g2.dispose();
        this.dummyFrame.dispose();
        
        comp.revalidate();
        return Printable.PAGE_EXISTS;
    } 
       
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        jScrollPane1 = new javax.swing.JScrollPane();
        addressTable = new javax.swing.JTable();
        googleQRCode = new javax.swing.JLabel();
        infoBox = new javax.swing.JLabel();
        mapquestQRCode = new javax.swing.JLabel();
        googleMapsLabel = new javax.swing.JLabel();
        mapquestLabel = new javax.swing.JLabel();
        mapDisplay = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setForeground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(1320, 1020));
        setPreferredSize(new java.awt.Dimension(1320, 1020));

        jScrollPane1.setBorder(null);

        addressTable.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        addressTable.setModel(new javax.swing.table.DefaultTableModel(
            this.route.getAsTable(),
            new String [] {
                "Name", "Address", "Phone Number", " Notes"
            })
            {
                Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
                };
                boolean[] canEdit = new boolean [] {
                    false, false, false, false
                };

                public Class getColumnClass(int columnIndex) {
                    return types [columnIndex];
                }

                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit [columnIndex];
                }
            });
            addressTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
            addressTable.setFillsViewportHeight(true);
            addressTable.setRowHeight(35);
            addressTable.setRowSelectionAllowed(false);
            addressTable.setShowHorizontalLines(true);
            addressTable.setShowVerticalLines(true);
            addressTable.getTableHeader().setReorderingAllowed(false);
            this.tca = new TableColumnAdjuster(this.addressTable);
            this.tca.adjustColumns();
            jScrollPane1.setViewportView(addressTable);

            googleQRCode.setIcon(new ImageIcon(this.route.getGoogleMapsQRCode(200, 200))
            );
            googleQRCode.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            googleQRCode.setMaximumSize(new java.awt.Dimension(200, 200));
            googleQRCode.setMinimumSize(new java.awt.Dimension(200, 200));
            googleQRCode.setPreferredSize(new java.awt.Dimension(200, 200));

            infoBox.setBackground(new java.awt.Color(204, 204, 204));
            infoBox.setForeground(new java.awt.Color(0, 0, 0));
            infoBox.setText(String.format(
                "<html> <h1>Route %s</h1>"
                + "Total distance (miles): %s<br>"
                + "Approximate travel time: %s<br>"
                + "Total boxes to deliver: %s<br>"
                + "Scan upper QR code for google maps route (max 11 destinations)<br>"
                + "Scan lower QR code for mapquest route (displays all destinations)<br>"
                + "<h2>Please Report Errors</h2>"
                + "If there is an error with this page, please send a picture of this page and a description of the error to pathify@gmail.com. Thanks!"
                + "<h3>Pathify Pre-Alpha0.8 - July 2020</h3>"
                + "Designed by Caleb Keller"
                + "</html>",
                this.routeNumber, this.route.getDistance(), this.route.getTime(), this.route.getNumBoxes()
            ));
            infoBox.setVerticalAlignment(javax.swing.SwingConstants.TOP);
            infoBox.setMinimumSize(new java.awt.Dimension(225, 50));
            infoBox.setPreferredSize(new java.awt.Dimension(225, 16));

            mapquestQRCode.setIcon(new ImageIcon(this.route.getMapquestQRCode(200, 200))
            );
            mapquestQRCode.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            mapquestQRCode.setMaximumSize(new java.awt.Dimension(200, 200));
            mapquestQRCode.setMinimumSize(new java.awt.Dimension(200, 200));
            mapquestQRCode.setPreferredSize(new java.awt.Dimension(200, 200));

            googleMapsLabel.setBackground(new java.awt.Color(255, 255, 255));
            googleMapsLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
            googleMapsLabel.setForeground(new java.awt.Color(51, 51, 51));
            googleMapsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            googleMapsLabel.setLabelFor(googleQRCode);
            googleMapsLabel.setText("Open in Google Maps");
            googleMapsLabel.setToolTipText("");

            mapquestLabel.setBackground(new java.awt.Color(255, 255, 255));
            mapquestLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
            mapquestLabel.setForeground(new java.awt.Color(51, 51, 51));
            mapquestLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            mapquestLabel.setLabelFor(mapquestQRCode);
            mapquestLabel.setText("Open in Mapquest");

            //mapDisplay.setIcon(new ImageIcon(new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB)));
            mapDisplay.setMaximumSize(new java.awt.Dimension(640, 640));
            mapDisplay.setPreferredSize(new java.awt.Dimension(640, 450));

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
            this.setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1314, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(mapDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(mapquestLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(googleMapsLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(googleQRCode, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addComponent(mapquestQRCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(infoBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addContainerGap())))
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 544, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(googleMapsLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(googleQRCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(mapquestLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(mapquestQRCode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(infoBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(mapDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, Short.MAX_VALUE)))
                    .addGap(12, 12, 12))
            );

            /*
            this.dummyFrame.pack();
            int maxHeight = this.getHeight() - jScrollPane1.getHeight();
            int maxWidth = this.getWidth() - infoBox.getWidth() - googleQRCode.getWidth();
            int imgHeight;
            int imgWidth;
            double ratio = (double) maxHeight / maxWidth;
            if (maxWidth > maxHeight) {
                imgWidth = Math.min(maxWidth, 640);
                imgHeight = new Long(Math.round(ratio / maxWidth)).intValue();
            } else {
                imgHeight = Math.min(maxHeight, 640);
                imgWidth = new Long(Math.round(maxHeight / ratio)).intValue();
            }
            */
            mapDisplay.setIcon(new ImageIcon(this.route.getMap(640, 450).getScaledInstance(640, 450, Image.SCALE_SMOOTH)));
        }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable addressTable;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel googleMapsLabel;
    private javax.swing.JLabel googleQRCode;
    private javax.swing.JLabel infoBox;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel mapDisplay;
    private javax.swing.JLabel mapquestLabel;
    private javax.swing.JLabel mapquestQRCode;
    // End of variables declaration//GEN-END:variables
}
