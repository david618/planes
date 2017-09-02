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

import java.util.ArrayList;
import java.util.Random;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.jennings.geotools.DistanceBearing;
import org.jennings.geotools.GreatCircle;

/**
 * Used to create routes from Airports
 * 
 * @author david
 */
public class RouteBuilder {

    Airports arpts = new Airports();
    GreatCircle gc = new GreatCircle();

    RandomGenerator rg = new JDKRandomGenerator();
    GaussianRandomGenerator grg = new GaussianRandomGenerator(rg);

    
    public RouteBuilder() {
        arpts = new Airports();
    }

    public RouteBuilder(double lllon, double lllat, double urlon, double urlat) {
        arpts = new Airports(lllon, lllat, urlon, urlat);
    }

    /**
     * This creates a route
     *   Random speed (200 to 300 m/s); Gaussian distributed around 250
     *   Random delay (300 to 900 seconds); Normal distributed
     *
     * @param durationSec
     * @return
     */
    public Route createRoute(long durationSec) {

        Route rt = null;

        try {

            ArrayList<Waypoint> wpts;

            int i = 0;

            wpts = new ArrayList<>();

            Random rnd = new Random();

            Airport firstArpt = arpts.getRndAirport("");

            Airport arpt1 = firstArpt;
            Airport arpt2;

            long t = 0;

            while (t < durationSec) {
                arpt2 = arpts.getRndAirport(arpt1.getName());

                DistanceBearing distB = gc.getDistanceBearing(arpt1.getLon(), arpt1.getLat(), arpt2.getLon(), arpt2.getLat());

                long st = t + rnd.nextInt(600) + 300;  // Delay from 300 to 900 seconds

                //double speed = (Math.random() * 100 + 200) / 1000;  // speed from 100 to 300 m/s converted to km/s
                // Speeds in Gaussian Distro around 250 +- 50; Reflect under 100 to over 100.  Mean will be 250 with stddev of about 50
                double speed = 250 + 50 * grg.nextNormalizedDouble();  
                if (speed < 100) speed += 100 + (100 - speed);
                speed = speed / 1000;

                long et = st + Math.round(distB.getDistance() / speed);

                Waypoint wpt = new Waypoint(st, arpt1.getName(), arpt1.getId(), arpt1.getLon(), arpt1.getLat(), distB.getDistance(), distB.getBearing(), speed, et);
                wpts.add(wpt);

                arpt1 = arpt2;

                t = et;
                i++;
            }

            arpt2 = firstArpt;
            if (!arpt2.getName().equalsIgnoreCase(arpt1.getName())) {
                // Only add if the last airport not equal first
                DistanceBearing distB = gc.getDistanceBearing(arpt1.getLon(), arpt1.getLat(), arpt2.getLon(), arpt2.getLat());

                long st = t + rnd.nextInt(600) + 300;  // Delay from 300 to 900 seconds

                double speed = (Math.random() * 100 + 200) / 1000;  // speed from 100 to 300 m/s converted to km/s

                long et = st + Math.round(distB.getDistance() / speed);

                //System.out.println(st + "," + arpt1.getName() + "," + arpt1.getId() + "," + arpt1.getLon() + "," + arpt1.getLat() + "," + distB.getDistance() + "," + distB.getBearing() + "," + et);
                Waypoint wpt = new Waypoint(st, arpt1.getName(), arpt1.getId(), arpt1.getLon(), arpt1.getLat(), distB.getDistance(), distB.getBearing(), speed, et);
                wpts.add(wpt);

            }

            rt = new Route(wpts);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rt;

    }
    
    public static void main(String args[]) {
        RouteBuilder rb = new RouteBuilder();
        Route rt = rb.createRoute(36000);
        for (Waypoint wp: rt.wpts) {
            System.out.println(wp.getName());
        }
    }

}
