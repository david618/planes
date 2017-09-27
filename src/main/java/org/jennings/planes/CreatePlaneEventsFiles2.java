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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import org.json.JSONObject;

/**
 *
 * @author david
 */
public class CreatePlaneEventsFiles2 {
    
    final private int TXT = 0;
    final private int JSON = 1;

    ArrayList<Plane> planes = new ArrayList<>();    
    
    /**
     * 
     */
    public void createEventsFile(String routeFile, Integer numPlanes, String outputFolder, String prefix, Long startTime, Integer stepSec, Integer durSec, Integer samplesPerFile, Integer format, Double maxAbsLat) {
        try {
            
            if (maxAbsLat == null) {
                maxAbsLat = 90.0;
            }
            
            // Create the Routes
            Routes rts;
            rts = new Routes();
            rts.load(routeFile);

            // Create Planes
            Random rnd = new Random();
            int i = 0;
            while (i < numPlanes) {
                int rndoffset = Math.abs(rnd.nextInt());
                Plane t = new Plane(i, rts.get(i), rndoffset);
                planes.add(t);
                i++;
            }

            String fs = System.getProperty("file.separator");
            String d = ",";

            int fileNum = 0;

            FileWriter fw = null;
            BufferedWriter bw = null;

            // CurrentTime
            Long t = startTime;  // millisecons from epoch

            Long numWritten = 0L;
            
            
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            df.setGroupingUsed(false);
            
            DecimalFormat df5 = new DecimalFormat();
            df5.setMaximumFractionDigits(5);
            df5.setGroupingUsed(false);
            
            GenerateRandomData genRndData = new GenerateRandomData();
            
            while (t < startTime + durSec * 1000) {

                for (Plane plane : planes) {
                    plane.setPosition(t);

                    String line = "";

                    switch (format) {
                        case TXT:
                            line = plane.id + d 
                                    + plane.timestamp + d 
                                    + df.format(plane.speed * 1000.0) + d
                                    + df.format(plane.dist) + d 
                                    + df.format(plane.bearing) + d
                                    + plane.rt.id + d
                                    + "\"" + plane.origin + "\"" + d
                                    + "\"" + plane.destination + "\"" + d
                                    + plane.secsToDep + d
                                    + df5.format(plane.gc.getLon()) + d 
                                    + df5.format(plane.gc.getLat());                                    
                            // Add loop to append 140 additional fields 
                            int cnt = 0;
                            while (cnt < 140) {
                                line += d + genRndData.generateWord(8);
                                cnt++;
                            }
                            break;
                        case JSON:
                            JSONObject js = new JSONObject();
                            js.put("id", plane.id);
                            js.put("timestamp", plane.timestamp);
                            js.put("speed", df.format(plane.speed * 1000.0));
                            js.put("dist", df.format(plane.dist));
                            js.put("bearing", df.format(plane.bearing));
                            js.put("routeid", plane.rt.id);
                            js.put("origin", plane.origin);
                            js.put("destination", plane.destination);
                            js.put("secsToDep", plane.secsToDep);
                            js.put("lon", df5.format(plane.gc.getLon()));
                            js.put("lat", df5.format(plane.gc.getLat()));
                            line = js.toString();

                    }
                    
                    if (Math.abs(plane.gc.getLat()) > maxAbsLat) {
                        // Skip lats over maxAbsLat 
                        
                    } else {
                        // If no file is open open the next file 
                        if (bw == null) {
                            fileNum += 1;
                            fw = new FileWriter(outputFolder + fs + prefix + String.format("%05d", fileNum));
                            bw = new BufferedWriter(fw);
                        }
                        
                        numWritten += 1;
                        bw.write(line);
                        bw.newLine();
                    }
                                                                                
                    if (numWritten % samplesPerFile == 0) {
                        bw.close();
                        fw.close();
                        bw = null;
                        fw = null;
                        
                    }                                        
                }

                t += stepSec * 1000;
            }

            try {
                fw.close();
            } catch (Exception e) {
                // ok to ignore
            }

            try {
                bw.close();
            } catch (Exception e) {
                // ok to ignore
            }
            

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public static void main(String[] args) {

//        String routeFile = "routes10000_4day.json";
//        Integer numThg = 100000;
//        String outputFolder = "/home/david/testfolder";
//        String prefix = "data";
//        Long startTime = System.currentTimeMillis();
//        Integer stepSec = 60;
//        Integer durSec = 3600;
//        Integer samplesPerFile = 1000000;
//        Integer format = t.TXT;
//
//        t.run(routeFile, numThg, outputFolder, prefix, startTime, stepSec, durSec, samplesPerFile, format, null);
        
        
        int numArgs = args.length;
        CreatePlaneEventsFiles2 t = new CreatePlaneEventsFiles2();
        
        if (numArgs < 9 || numArgs > 10) {
            System.err.println("Usage: CreatePlaneEventsFiles2 routeFile numThings outputFolder prefix startTime step durationSec samplesPerFile format <latLimit>");
            System.err.println();
            System.err.println("Example: CreatePlaneEventsFiles2 routes10000_4day.json 100000 /home/david/testfolder data now 60 3600 1000000 txt");            
        } else {
            
            String routeFile = args[0];
            Integer numThg = Integer.parseInt(args[1]);
            String outputFolder = args[2];
            String prefix = args[3];
            String startTimeStr = args[4];
            
            Long startTime = System.currentTimeMillis();
            if (startTimeStr.equalsIgnoreCase("now")) {
                // ok
            } else {
                startTime = Long.parseLong(startTimeStr);
            }
            
            Integer stepSec = Integer.parseInt(args[5]);
            Integer durSec = Integer.parseInt(args[6]);
            Integer samplesPerFile = Integer.parseInt(args[7]);
                                    
            Integer format = t.TXT;
            if (args[8].equalsIgnoreCase("json")) {
                format = t.JSON;
            } else if (args[8].equalsIgnoreCase("txt")) {
                format = t.TXT;
            } else {
                System.out.println("Unrecognized Format. Defaulting to txt");
            }
            
            Double absMaxLat = null;
            if (numArgs == 10) {
                absMaxLat = Double.parseDouble(args[9]);
            }

            t.createEventsFile(routeFile, numThg, outputFolder, prefix, startTime, stepSec, durSec, samplesPerFile, format, absMaxLat);
            
            
        }
        
        

    }    
    
}
