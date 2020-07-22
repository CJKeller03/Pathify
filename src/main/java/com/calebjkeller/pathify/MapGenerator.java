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
package com.calebjkeller.pathify;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javax.swing.ImageIcon;

/**
 *
 * @author caleb
 */
public class MapGenerator {
    
    private static MapView mapView;
    private static BorderPane borderPane;
    private static Scene scene;
    
    public static void initialize() {
        Platform.startup(new Runnable() {
            public void run() {
                initFX();
            }
        });
    }
    
    private static void initFX() {
        mapView = new MapView();
        borderPane = new BorderPane();
        borderPane.setCenter(mapView);
        scene = new Scene(borderPane, 500, 500);
    }
    
    public static ImageIcon createMap() {

        Basemap.Type basemapType = Basemap.Type.STREETS_VECTOR;
        double latitude = 34.02700;
        double longitude = -118.80543;
        int levelOfDetail = 12;
        
        ArcGISMap map = new ArcGISMap(basemapType, latitude, longitude, levelOfDetail);
        mapView.setMap(map);        

        WritableImage img = new WritableImage(500, 500);
        
        FutureTask snapshotTask = new FutureTask(new Callable() {
            @Override
            public Boolean call() throws Exception {
               scene.snapshot(img); 
               return true;
            }
        });
        
        Platform.runLater(snapshotTask);
        try {
            snapshotTask.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return new ImageIcon(SwingFXUtils.fromFXImage(img, null));
    }
}
