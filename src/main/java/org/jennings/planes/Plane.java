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

import java.io.FileWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import org.jennings.geotools.DistanceBearing;
import org.jennings.geotools.GeographicCoordinate;
import org.jennings.geotools.GreatCircle;
import org.json.JSONArray;
import org.json.JSONObject;

public class Plane {

    // Thing properties
    GeographicCoordinate gc;
    int id;
    double speed;
    double bearing;
    double dist;
    String origin;
    String destination;
    long timestamp;
    long secsToDep;

    DecimalFormat DF2 = new DecimalFormat();
    DecimalFormat DF5 = new DecimalFormat();

    String d = ",";

    Route rt;  // Route for this thing
    long rtOffsetSec; // Time offset into the route

    public Plane() {
        DF2.setMaximumFractionDigits(2);
        DF2.setGroupingUsed(false);

        DF5.setMaximumFractionDigits(5);
        DF5.setGroupingUsed(false);
    }

    /**
     *
     *
     * @param id Assigned
     * @param rt Route for the thing
     * @param rtOffsetSec Number of seconds into the route
     */
    public Plane(int id, Route rt, long rtOffsetSec) {
        this();
        this.id = id;
        this.rt = rt;
        this.rtOffsetSec = rtOffsetSec;
    }

    /**
     * Constructor creates a random offset for the thing.
     *
     * @param id
     * @param rt
     */
    public Plane(int id, Route rt) {
        this();
        this.id = id;
        this.rt = rt;
        // Create Random offset 
        Random rnd = new Random();
        this.rtOffsetSec = rnd.nextLong() % rt.getLastSec();
    }

    public long getSecondsToDeparture() {
        return secsToDep;
    }

    public void setSecondsToDept(long secondsToDept) {
        this.secsToDep = secondsToDept;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    public GeographicCoordinate getGc() {
        return gc;
    }

    public void setGc(GeographicCoordinate gc) {
        this.gc = gc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Plane{" + "gc=" + gc + ", id=" + id + ", speed=" + speed + ", bearing=" + bearing + ", dist=" + dist + ", origin=" + origin + ", destination=" + destination + ", timestamp=" + timestamp + ", secsToDep=" + secsToDep + ", rtOffsetSec=" + rtOffsetSec + '}';
    }

    /**
     * Return route as JSON
     *
     * @return
     */
    public JSONObject getJSON() {
        JSONObject json = new JSONObject();

        json.put("id", this.id);
        json.put("rt", this.rt.getJSON());
        json.put("rtOffsetSec", this.rtOffsetSec);

        return json;
    }

    public String getPlaneCSV() {
        String line;
        line = this.id + d
                + this.timestamp + d
                + DF2.format(this.speed * 1000.0) + d
                + DF2.format(this.dist) + d
                + DF2.format(this.bearing) + d
                + this.rt.id + d
                + "\"" + this.origin + "\"" + d
                + "\"" + this.destination + "\"" + d
                + this.secsToDep + d
                + DF5.format(this.gc.getLon()) + d
                + DF5.format(this.gc.getLat());
        return (line);
    }

    public JSONObject getPlaneJSON() {
        JSONObject js = new JSONObject();
        js.put("id", this.id);
        js.put("timestamp", this.timestamp);
        js.put("speed", DF2.format(this.speed * 1000.0));
        js.put("dist", DF2.format(this.dist));
        js.put("bearing", DF2.format(this.bearing));
        js.put("routeid", this.rt.id);
        js.put("origin", this.origin);
        js.put("destination", this.destination);
        js.put("secsToDep", this.secsToDep);
        js.put("lon", DF5.format(this.gc.getLon()));
        js.put("lat", DF5.format(this.gc.getLat()));
        return js;
    }

    public void setPosition(Long timestamp) {

        this.timestamp = System.currentTimeMillis();
        long t = Math.round(this.timestamp / 1000.0);

        if (timestamp != null) {
            this.timestamp = timestamp;
            t = Math.round(timestamp / 1000.0);
        }

        long t2 = (t + this.rtOffsetSec) % rt.getLastSec();   // some time between 0 and lastSec

//        System.out.println(t2);
//        System.out.println(lastSec);
        int i = 0;

        GreatCircle grc = new GreatCircle();
        ArrayList<Waypoint> wpts = rt.getWpts();
        Waypoint wpt = wpts.get(0);
        GeographicCoordinate gc = new GeographicCoordinate();
//        Thing thing = new Thing();

        while (i <= wpts.size()) {
            if (i == wpts.size()) {
                // Wrap to first
                wpt = wpts.get(0);
            } else {
                wpt = wpts.get(i);
            }

            if (t2 < wpt.st) {
                // on the ground at this point
                gc.setLon(wpt.getLon());
                gc.setLat(wpt.getLat());
                this.gc = gc;
                this.speed = 0.0;
                this.bearing = wpt.bearing;

                long td = wpt.st - t2;
                if (td < 0) {
                    td = 0;
                }

                this.secsToDep = td;

                this.origin = wpt.name;

                i++;
                if (i == wpts.size()) {
                    // Wrap to first
                    wpt = wpts.get(0);
                } else {
                    wpt = wpts.get(i);
                }

                this.destination = wpt.name;
                this.dist = wpt.distance;

                break;

            } else if (t2 < wpt.et) {
                // in route between this wpt and next
                GeographicCoordinate pt1 = new GeographicCoordinate(wpt.lon, wpt.lat);

                double dist = wpt.speed * (t2 - wpt.st);

                DistanceBearing distB = new DistanceBearing(dist, wpt.bearing);
                gc = grc.getNewCoordPair(pt1, distB);

                this.gc = gc;
                this.speed = wpt.speed;
                this.bearing = wpt.bearing;

                this.origin = wpt.name;
                double remainingDist = wpt.distance - dist;
                if (remainingDist < 0) {
                    remainingDist = 0.0;
                }

                this.dist = remainingDist;
                this.secsToDep = -1;

                i++;
                if (i == wpts.size()) {
                    // Wrap to first
                    wpt = wpts.get(0);
                } else {
                    wpt = wpts.get(i);
                }

                this.destination = wpt.name;

                break;

            }
            i++;

        }

    }

    public void createGeoJsonTest() {

        try {

            Routes routes = new Routes();
            //t.createRandomRouteFile("routesThreeDay10.json", 10, 86400*3);
            //routes.load("routesOneDay100.json");
            routes.load("routesOneDayUS10.json");

            JSONArray results = new JSONArray();

//            Route rt = routes.get(0);            
            for (Route rt : routes.rts) {

                // Create Thing
                Plane t = new Plane(1, rt, 0);

                long n = System.currentTimeMillis();

                for (int i = 0; i <= rt.lastSec; i = i + 60) {

                    JSONObject result = new JSONObject();

                    t.setPosition(n + i * 1000);

                    result.put("type", "Feature");

                    JSONObject properties = new JSONObject();
                    properties.put("id", t.id);
                    properties.put("rtid", t.rt.id);
                    properties.put("timestamp", t.timestamp);
                    Date d = new Date(t.timestamp);
                    DateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm'Z'");
                    properties.put("dtg", f.format(d));
                    properties.put("origin", t.origin);
                    properties.put("destination", t.destination);
                    properties.put("lon", t.gc.getLon());
                    properties.put("lat", t.gc.getLat());
                    properties.put("dist", t.dist);
                    properties.put("bearing", t.bearing);
                    properties.put("speed", t.speed);
                    properties.put("secsToDep", t.secsToDep);
                    result.put("properties", properties);

                    JSONObject geom = new JSONObject();
                    geom.put("type", "Point");
                    JSONArray coord = new JSONArray("[" + t.gc.getLon() + ", " + t.gc.getLat() + "]");
                    geom.put("coordinates", coord);
                    result.put("geometry", geom);

                    results.put(result);

                }
            }

            FileWriter fw = new FileWriter("temp.json");

            JSONObject featureCollection = new JSONObject();
            featureCollection.put("type", "FeatureCollection");

            featureCollection.put("features", results);

            //out.println(featureCollection);
            featureCollection.write(fw);

            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void test() {

        try {

            Routes routes = new Routes();
            //t.createRandomRouteFile("routesThreeDay10.json", 10, 86400*3);
            //routes.load("routesOneDay100.json");
            //routes.load("routesOneDayUS10.json");
            routes.createRandomRoutes(10, 86400);

            // Pick Route
            // Should turn routes class into an iterable class
            Route rt = routes.get(4);

            RouteBuilder rb = new RouteBuilder();
            rt = rb.createRoute(86400);

            // Create Thing
            Plane t = new Plane(1, rt, 0);
            long n = System.currentTimeMillis();

//                    while (true) {
//            
//                        t.setPosition(System.currentTimeMillis() + 0 * 1000);
//                        System.out.println(t.secsToDep + " " + t.dist);
//                        Thread.sleep(1000);
//                    }
            FileWriter fw = new FileWriter("temp.txt");

            for (int i = 0; i <= rt.lastSec * 2; i = i + 60) {

                t.setPosition(n + i * 1000);
                fw.write(t + "\n");
                System.out.println(t);

            }
            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {

        Plane t = new Plane();
        t.test();
    }

}
