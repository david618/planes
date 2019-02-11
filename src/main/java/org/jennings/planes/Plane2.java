/*
 * (C) Copyright 2017 David Jennings
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     David Jennings
 */
package org.jennings.planes;

import org.jennings.geotools.GeographicCoordinate;
import org.jennings.geotools.GreatCircle;

/**
 * This version of plane would work with WayPointsOF (openflight)
 * 
 * Plane would start at a (specified origin) and pick a WayPoint (to next city from those listed in OpenFlight routes.dat
 * Would only return current position (no forecasting) positions 
 * Benefit it could run indefinitely without repeating paths 
 * 
 * @author david
 */
public class Plane2 {
    
    Waypoint wpt;  // Current Waypoint for plane; st is starttime as epoch and et is arrival time as epoch
    
    //GreatCircle gcir = new GreatCircle();
    
    //GeographicCoordinate gc; // Current position of plan
    int id;
    double speed;
    double bearing;
    double dist;
    String origin;
    String destination;
    long timestamp;
    long secsToDep;    
    
    public Plane2(Waypoint wpt, int id, double speed) {
        this.wpt = wpt;
        this.id = id;
        this.speed = speed;
        
        this.origin = wpt.origin;
        this.destination = wpt.destination;
        this.timestamp = System.currentTimeMillis();
        this.secsToDep = (wpt.st - this.timestamp)/1000;
        
        this.dist = wpt.distance;
        this.bearing = wpt.bearing;        
        
    }    
    
    // Create a a WayPoints from availble routes.dat https://openflights.org/data.html
    
    // Each Plane starts from Random WayPoint; st and et set based on currentEpochMillis (Epoch Times)
    
    // Update Position current epoch time
        // while current time > et     
            // Select a new WayPoint that has (destination) as the Name
    
            // Set st, speed, and et
    
    
        // if less than st plane is on ground at origin
    
        // if > st then find point in route and report the position

    public Waypoint getWpt() {
        return wpt;
    }

    public int getId() {
        return id;
    }

    public double getSpeed() {
        return speed;
    }

    public double getBearing() {
        return bearing;
    }

    public double getDist() {
        return dist;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getSecsToDep() {
        return secsToDep;
    }


    
   
    
    
    
}
