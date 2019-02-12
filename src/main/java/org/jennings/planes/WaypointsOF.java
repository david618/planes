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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * Waypoints created from Openflight routes.dat
 * https://openflights.org/data.html
 *
 * @author david
 */
public class WaypointsOF {

    public HashMap<String, ArrayList<Waypoint>> waypoints = new HashMap<>();

    public WaypointsOF() {

        InputStreamReader isr = null;
        BufferedReader br = null;
        
        try {
            Airports arpts = new Airports();

            isr = new InputStreamReader(new FileInputStream("routes.dat"), StandardCharsets.UTF_8); 
            br = new BufferedReader(isr);

            int i = 0;
            String line;
            while ((line = br.readLine()) != null) {

                String fields[] = line.split(",", -1);
                String airline = fields[0];
                Integer airlineID = null;
                try {
                    airlineID = Integer.parseInt(fields[1]);
                } catch (NumberFormatException e) {
                    airlineID = null;
                }
                String sourceAirport = fields[2];
                Integer sourceAirportID = null;
                try {
                    sourceAirportID = Integer.parseInt(fields[3]);
                } catch (NumberFormatException e) {
                    sourceAirportID = null;
                }
                String destinationAirport = fields[4];
                Integer destinationAirportID = null;
                try {
                    destinationAirportID = Integer.parseInt(fields[5]);
                } catch (NumberFormatException e) {
                    destinationAirportID = null;
                }
                String codeShare = fields[6];
                String stops = fields[7];
                String equipment = fields[8];

                int wptCnt = 0;

                if (sourceAirportID != null && destinationAirportID != null) {
                    // Both source and destination airport ID's are specified
                    Airport origin = null;
                    Airport destination = null;
                    for (Airport arpt : arpts.airports) {
                        int id = arpt.getId();
                        if (sourceAirportID == id) {
                            origin = arpt;
                        }
                        if (destinationAirportID == id) {
                            destination = arpt;
                        }
                        if (origin != null && destination != null) {
                            break;
                        }
                    }

                    if (origin != null && destination != null) {
                        wptCnt += 1;

                        // The Source is specified and is in airports
                        //System.out.println(origin.getName() + ":" + destination.getName());
                        Waypoint wpt = new Waypoint();
                        wpt.setOrigin(origin.getName());
                        wpt.setLon(origin.getLon());
                        wpt.setLat(origin.getLat());
                        wpt.setDestination(destination.getName());
                        wpt.setId(wptCnt);

                        // Started Mapping with Name; might be better to Map with Airport Trigraphs (Easier to match)
                        if (waypoints.containsKey(origin.getName())) {
                            waypoints.get(origin.getName()).add(wpt);
                        } else {
                            ArrayList<Waypoint> waypts = new ArrayList<>();
                            waypts.add(wpt);
                            waypoints.put(wpt.origin, waypts);

                        }

                    }

                }

            }

            //for (String name : waypoints.keySet()) {
            for (Map.Entry entry : waypoints.entrySet()) {
                String name = (String) entry.getKey();
                System.out.println(name + " : " + waypoints.get(name).size());
            }
            System.out.println(waypoints.size());  // 3150 origins; each origin has 1 to 100's of destinations

        } catch (IOException e) {

        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException e) {
                // ok to ignore.
            }
            try {
                if (isr != null) isr.close();
            } catch (IOException e) {
                // ok to ignore
            }
        }
    }

    public static void main(String[] args) {
        //WaypointsOF t = new WaypointsOF();
    }

}
