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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This version of CreatePlanesEventsFiles adds additional 140 random string fields.
 * 
 * @author david
 */
public class CreatePlaneEventsFiles2 {

    private static final Logger log = LogManager.getLogger(CreatePlaneEventsFiles2.class);

    final private int TXT = 0;
    final private int JSON = 1;

    ArrayList<Plane> planes = new ArrayList<>();

    /**
     *
     * @param routeFile
     * @param numPlanes
     * @param outputFolder
     * @param prefix
     * @param startTime
     * @param stepSec
     * @param durSec
     * @param samplesPerFile
     * @param format
     * @param maxAbsLat
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
                int rndoffset = rnd.nextInt(Integer.MAX_VALUE);
                Plane t = new Plane(i, rts.get(i), rndoffset);
                planes.add(t);
                i++;
            }

            String fs = System.getProperty("file.separator");
            String d = ",";

            int fileNum = 0;

            OutputStreamWriter osw = null;
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
                    StringBuffer buf = new StringBuffer();

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
                                buf.append(d + genRndData.generateWord(8));
                                cnt++;
                            }
                            line = buf.toString();
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
                            break;
                        default:
                            System.err.println("Invalid Format");

                    }

                    if (Math.abs(plane.gc.getLat()) > maxAbsLat) {
                        // Pin maxLat
                        if (plane.gc.getLat() > 0) {
                            plane.gc.setLat(maxAbsLat);
                        } else {
                            plane.gc.setLat(-1.0*maxAbsLat);
                        }
                    }
                    
                    // If no file is open open the next file 
                    if (bw == null) {
                        fileNum += 1;
                        osw = new OutputStreamWriter(new FileOutputStream(outputFolder + fs + prefix + String.format("%05d", fileNum)), StandardCharsets.UTF_8); 
                        bw = new BufferedWriter(osw);
                    }

                    numWritten += 1;
                    bw.write(line);
                    bw.newLine();

                    if (numWritten % samplesPerFile == 0) {
                        if (bw != null) {
                            bw.close();
                        }
                        if (osw != null) {
                            osw.close();
                        }
                        bw = null;
                        osw = null;

                    }
                }

                t += stepSec * 1000;
            }

            try {
                if (osw != null) {
                    osw.close();
                }
            } catch (IOException e) {
                // ok to ignore
            }

            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                // ok to ignore
            }

        } catch (IOException | JSONException e) {
            log.error(e);
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
                System.out.println("Unrecognized Format. Using txt");
            }

            Double absMaxLat = null;
            if (numArgs == 10) {
                absMaxLat = Double.parseDouble(args[9]);
            }

            t.createEventsFile(routeFile, numThg, outputFolder, prefix, startTime, stepSec, durSec, samplesPerFile, format, absMaxLat);

        }

    }

}
