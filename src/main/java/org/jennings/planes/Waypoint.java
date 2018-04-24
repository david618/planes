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

/**
 * 
 * Java Bean
 *     Example: 
 *   st: 321 (Seconds)
 *   origin: Allice Springs Airport (String)
 *   dest: La Plata Airport (String)
 *   id: 123 (waypoint id)
 *   lon: 33.4  (wgs84)
 *   lat: 22.8  (wgs84) 
 *   dist: 327 (km)
 *   bearing: 128.3 (deg)
 *   speed: 221 (m/s)
 *   et: 1234 (Seconds)
 *
 * @author david
 */
public class Waypoint {
    
    /*
    
    
    */
    long st; // Time to start based 
    String origin;
    String destination;
    int id;
    double lon;
    double lat;
    double distance; // distance to next point  
    double bearing;  // bearing to next point
    double speed;  // constanct speed used when travelling between this point and next    
    long et; // Arrival time at next point  

    public Waypoint() {
        
    }
    
    
    public Waypoint(long st, String origin, String destination, int id, double lon, double lat, double distance, double bearing, double speed, long et) {
        this.st = st;       
        this.origin = origin;
        this.destination = destination;
        this.id = id;
        this.lon = lon;
        this.lat = lat;
        this.distance = distance;
        this.bearing = bearing;
        this.speed = speed;
        this.et = et;
    }

    public long getSt() {
        return st;
    }

    public void setSt(long st) {
        this.st = st;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public long getEt() {
        return et;
    }

    public void setEt(long et) {
        this.et = et;
    }

    @Override
    public String toString() {
        return "Waypoint{" + "st=" + st + ", origin=" + origin + ", destination=" + destination + ", id=" + id + ", lon=" + lon + ", lat=" + lat + ", distance=" + distance + ", bearing=" + bearing + ", speed=" + speed + ", et=" + et + '}';
    }
}
