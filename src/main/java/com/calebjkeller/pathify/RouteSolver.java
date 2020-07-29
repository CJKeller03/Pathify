// Copyright 2010-2018 Google LLC
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


package com.calebjkeller.pathify;
// [START program]
// [START import]
import com.calebjkeller.pathify.locationHandling.Route;
import com.google.ortools.constraintsolver.Assignment;
import com.google.ortools.constraintsolver.FirstSolutionStrategy;
import com.google.ortools.constraintsolver.RoutingIndexManager;
import com.google.ortools.constraintsolver.RoutingModel;
import com.google.ortools.constraintsolver.RoutingSearchParameters;
import com.google.ortools.constraintsolver.main;
import java.util.ArrayList;
// [END import]

/** Minimal VRP.*/
public class RouteSolver {
  static {
    System.loadLibrary("jniortools");
  }
  
  public static ArrayList<Route> solve(SolverDataModel data) {

    // Create Routing Index Manager
    // [START index_manager]
    RoutingIndexManager manager =
        new RoutingIndexManager(data.costMatrix.length, data.vehicleNumber, data.depot);
    // [END index_manager]

    // Create Routing Model.
    // [START routing_model]
    RoutingModel routing = new RoutingModel(manager);
    // [END routing_model]

    // Create and register a transit callback.
    // [START transit_callback]
    final int transitCallbackIndex =
        routing.registerTransitCallback((long fromIndex, long toIndex) -> {
          // Convert from routing variable Index to user NodeIndex.
          int fromNode = manager.indexToNode(fromIndex);
          int toNode = manager.indexToNode(toIndex);
          return data.costMatrix[fromNode][toNode][0];
        });
    // [END transit_callback]

    // Define cost of each arc.
    // [START arc_cost]
    routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);
    // [END arc_cost]

    // Add Capacity constraint.
    // [START capacity_constraint]
    final int demandCallbackIndex = routing.registerUnaryTransitCallback((long fromIndex) -> {
      // Convert from routing variable Index to user NodeIndex.
      int fromNode = manager.indexToNode(fromIndex);
      return data.demands[fromNode];
    });
    
    routing.addDimensionWithVehicleCapacity(demandCallbackIndex, 0, // null capacity slack
        data.vehicleCapacities, // vehicle maximum capacities
        true, // start cumul to zero
        "Capacity");
    // [END capacity_constraint]

    // Setting first solution heuristic.
    // [START parameters]
    RoutingSearchParameters searchParameters =
        main.defaultRoutingSearchParameters()
            .toBuilder()
            .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
            .build();
    // [END parameters]

    // Solve the problem.
    // [START solve]
    Assignment solution = routing.solveWithParameters(searchParameters);
    // [END solve]

    // Print solution on console.
    // [START print_solution]
    
    ArrayList<Route> routes = new ArrayList<Route>();
    
    // Inspect solution.
    long totalDistance = 0;
    long totalLoad = 0;
    
    for (int i = 0; i < data.vehicleNumber; ++i) {
      long index = routing.start(i);
      Route route = new Route();
      
      long routeTime = 0;
      long routeDistance = 0;
      long routeLoad = 0;
      //String route = "";
      
      while (!routing.isEnd(index)) {
        long nodeIndex = manager.indexToNode(index);
        routeLoad += data.demands[(int) nodeIndex];
        
        route.addLocations(data.getLocations((int) nodeIndex));
        //route += nodeIndex + " Load(" + routeLoad + ") -> ";
        
        long previousIndex = index;
        index = solution.value(routing.nextVar(index));
        routeDistance += routing.getArcCostForVehicle(previousIndex, index, i);
        routeTime += data.costMatrix[manager.indexToNode(previousIndex)][manager.indexToNode(index)][1];
      }
      
      route.setDistanceMeters(routeDistance);
      route.setTime(routeTime);
      route.setNumBoxes(routeLoad);
      
      //route += manager.indexToNode(routing.end(i));
      //logger.info(route);
      //logger.info("Distance of the route: " + routeDistance + "m");
      //totalDistance += routeDistance;
      //totalLoad += routeLoad;
      
      routes.add(route);
    }
    
    //logger.info("Total distance of all routes: " + totalDistance + "m");
    //logger.info("Total load of all routes: " + totalLoad);
    
    // [END print_solution]
    
    return routes;
  }
}
// [END program]
