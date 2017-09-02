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
 *
 *
 *   Route consists of a series of Waypoints
 *   Each route is closed. The last Waypoint give dist/bearing to the first.
 * Example:
 * 
 * 816,Alice Springs Airport,3319,133.90199279785156,-23.806699752807617,13361.853782406464,168.8274126698336,61068
 * 61648,La Plata Airport,2447,-57.8947,-34.9722,12787.247710042635,60.14191134805573,123564
 * 124405,Adıyaman Airport,5800,38.4688987732,37.7313995361,2911.4261109444706,94.73005499828885,136367
 * 136733,Zhob Airport,2233,69.4636001586914,31.358400344848633,5246.3508694127495,-72.72477455791804,155912
 * 156589,Tripoli International Airport,1157,13.1590003967,32.6635017395,9852.188296138784,-2.4847151672753514,197301
 * 198200,Cape Newenham LRRS Airport,3427,-162.06300354,58.646400451699996,10878.523714379604,-123.86942642142148,247956
 *
 * From 0 to 816 seconds on ground at Alice Springs Airport  
 * From 816 to 61068 in route from Alice Springs Airport to La Plata Airport 
 * From 61068 to 61648 on ground at La Plata Airport
 * ...
 * From 19301 to 198200 on the group at Cape Newenham LRRS Airport
 * From 198200 to 247956 in route from Cape Newenham LRRS Airport to Alice Springs Airport
 *  
 * System.currentTimeMS/1000 % 247956 = number from 0 to 247955    
 */

package org.jennings.planes;

import java.util.ArrayList;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author david
 */
public class Route {

    ArrayList<Waypoint> wpts;
    long lastSec;
    int numWpts;
//    int offset;
    int id;

    public Route() {

    }

    /**
     * Construct route from a json object
     * 
     * @param json 
     */
    public Route(JSONObject json) {
        id = json.getInt("id");
        numWpts = json.getInt("numWpts");
        lastSec = json.getLong("lastSec");

        JSONArray jsonWpts = json.getJSONArray("wpts");

        int i = 0;

        wpts = new ArrayList<>();

        while (i < jsonWpts.length()) {

            JSONObject wpt = jsonWpts.getJSONObject(i);

            String name = wpt.getString("name");
            double lon = wpt.getDouble("lon");
            double lat = wpt.getDouble("lat");
            long st = wpt.getLong("st");
            double distance = wpt.getDouble("distance");
            double bearing = wpt.getDouble("bearing");
            double speed = wpt.getDouble("speed");
            long et = wpt.getLong("et");

            Waypoint wp = new Waypoint(st, name, id, lon, lat, distance, bearing, speed, et);
            wpts.add(wp);

            i++;
        }

    }

    /**
     * Construct Route from ArrayList of Waypoint
     * @param wpts 
     */
    public Route(ArrayList<Waypoint> wpts) {

        this.wpts = wpts;
        numWpts = wpts.size();
        lastSec = wpts.get(numWpts - 1).getEt();
        int maxOffset = 0;
        if (lastSec > Integer.MAX_VALUE) {
            maxOffset = Integer.MAX_VALUE;
        } else {
            maxOffset = (int) lastSec;
        }
        Random rnd = new Random();
    }

    /** 
     * Return ArrayList of WayPoints
     * 
     * @return 
     */
    public ArrayList<Waypoint> getWpts() {
        return wpts;
    }

    /**
     * Set Waypoints from ArrayList
     * 
     * @param wpts 
     */
    public void setWpts(ArrayList<Waypoint> wpts) {
        this.wpts = wpts;
    }

    public long getLastSec() {
        return lastSec;
    }

    public void setLastSec(long lastSec) {
        this.lastSec = lastSec;
    }

    public int getNumWpts() {
        return numWpts;
    }

    public void setNumWpts(int numWpts) {
        this.numWpts = numWpts;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Return route as JSON
     * @return 
     */
    public JSONObject getJSON() {
        JSONObject json = new JSONObject();

        json.put("id", this.id);
        json.put("numWpts", this.numWpts);
        json.put("lastSec", this.lastSec);

        JSONArray waypts = new JSONArray();

        for (Waypoint wp : this.wpts) {
            JSONObject wpt = new JSONObject();
            wpt.put("name", wp.name);
            wpt.put("id", wp.id);
            wpt.put("lon", wp.lon);
            wpt.put("lat", wp.lat);
            wpt.put("st", wp.st);
            wpt.put("distance", wp.distance);
            wpt.put("bearing", wp.bearing);
            wpt.put("speed", wp.speed);
            wpt.put("et", wp.et);
            waypts.put(wpt);
        }

        json.put("wpts", waypts);

        return json;
    }

    @Override
    public String toString() {
        return "Route{" + "wpts=" + wpts + ", lastSec=" + lastSec + ", numWpts=" + numWpts  + ", id=" + id + '}';
    }

    public static void main(String[] args) throws Exception {
        
        RouteBuilder rb = new RouteBuilder();
        Route rt = rb.createRoute(3600);

        JSONObject json = rt.getJSON();
        
        System.out.println(json.toString(1));
        

    }

}
