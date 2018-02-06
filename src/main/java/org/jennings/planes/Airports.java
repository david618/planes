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
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author david
 */
public class Airports {

    private static Logger log = LogManager.getLogger(Airports.class);

    // Group 3 is unquoted String or null, Groupt 2 is Number or null 
    //public static final Pattern COMMADELIMQUOTEDSTRINGS = Pattern.compile("(([^\"][^,]*)|\"([^\"]*)\"),?");
    // Group 2 is number or null; Group 1 is String without quotes; Group 0 is the Quoted String
    public static final Pattern COMMADELIMQUOTEDSTRINGS = Pattern.compile("\"([^\"]+?)\",?|([^,]+),?|,");

    // Group 2 is number or null; Group 1 is String without quotes; Group 0 is the Quoted String
    public static final Pattern PIPEDELIMQUOTEDSTRINGS = Pattern.compile("\"([^\"]+?)\"\\|?|([^\\|]+)\\|?\\|");

    ArrayList<Airport> airports;
    int numAirports;

    /**
     * Load Airports from all over the world
     */
    public Airports() {
        this(-180, -90, 180, 90, null);
    }

    /**
     * Load Airports Matching a Country Name
     *
     * @param countryName
     */
    public Airports(String countryName) {
        this(-180, -90, 180, 90, countryName);
    }

    public Airports(double lllon, double lllat, double urlon, double urlat) {
        this(lllon, lllat, urlon, urlat, null);
    }

    /**
     * Loads airports from a file
     *
     * The airport.dat file was downloaded from
     * https://openflights.org/data.html
     *
     * @param lllon Lower Left Longitude
     * @param lllat Lower Right Longitude
     * @param urlon Upper Right Longitude
     * @param urlat Upper Right Latitude
     * @param cntryName
     */
    public Airports(double lllon, double lllat, double urlon, double urlat, String ccs) {

        // Load Airports
        FileReader fr;
        BufferedReader br;

        try {
            // Load in Aiports; Assumes file is located in Project Root
            fr = new FileReader("airports_countries.dat");
            br = new BufferedReader(fr);

            airports = new ArrayList<>();

            ArrayList<String> ccsList = new ArrayList<>();

            if (ccs != null) {
                ccsList = new ArrayList<>();
                String ccsArray[] = ccs.split(",");
                for (String cc : ccsArray) {
                    ccsList.add(cc);
                }
            }

            String line;
            while ((line = br.readLine()) != null) {

                line = line.replace("\\\"", "'");  // Replace \" with single quote
                Matcher matcher = COMMADELIMQUOTEDSTRINGS.matcher(line);
                //System.out.println(line);

                ArrayList<String> vals = new ArrayList<>();

                int i = 0;
                while (matcher.find()) {
                    if (matcher.group(2) != null) {
                        String val = matcher.group(2);
                        //System.out.println(val);
                        if (val.equalsIgnoreCase("\\N")) {
                            vals.add(i, null);
                            i += 1;
                        } else {
                            // This is a number
                            vals.add(i, matcher.group(2));
                            i += 1;
                        }
                    } else if (matcher.group(1) != null) {
                        // This is a String
                        vals.add(i, matcher.group(1));
                        i += 1;
                    }

                }

                int id = Integer.parseInt(vals.get(0));
                String name = vals.get(1);
                String cntry = vals.get(3);
                String cc = vals.get(14);
                String lat = vals.get(6);
                String lon = vals.get(7);

                double dlon = Double.parseDouble(lon);
                double dlat = Double.parseDouble(lat);

                Airport arpt = new Airport(id, name, cntry, cc, dlat, dlon);

                if (ccs != null) {
                    // If name is not null match name (ignore any coordinate entries provided)
                    if (ccsList.contains(cc)) {
                        airports.add(arpt);
                    }
                } else if (dlon > lllon && dlon < urlon) {
                    if (dlat > lllat && dlat < urlat) {
                        airports.add(arpt);
                    }
                }

            }

            numAirports = airports.size();
            //System.out.println(numAirports);

        } catch (IOException | NumberFormatException e) {
            log.error("Ops!", e);
        }

    }

    /**
     * Get a Random Airport that is not have the name provided
     *
     * @param name
     * @return
     * @throws Exception
     */
    public Airport getRndAirport(String name) throws Exception {
        Random rnd = new Random();

        int i = rnd.nextInt(numAirports);
        Airport arpt = airports.get(i);

        int numTries = 1;
        while (arpt.getName().equalsIgnoreCase(name)) {

            numTries += 1;
            i = rnd.nextInt(numAirports);
            arpt = airports.get(i);
            if (numTries > 10) {
                throw new Exception("Having trouble finding a Random that doesn't match the name provided");
            }
        }

        return arpt;
    }

    public void printAll() {
        airports.forEach((arpt) -> {
            System.out.println(arpt.getName());
        });

    }

    public static void main(String[] args) {

//        Airports t = new Airports("DE,US");
//
//        t.printAll();
//        System.out.println(t.airports.size());
        int numArgs = args.length;

        if (numArgs != 0 && numArgs != 1 && numArgs != 4) {
            System.err.println("Usage: Airports ");
            System.err.println("       Airports lllon lllat urlon urlat");
            System.err.println("       Airports csvCountryCodes");
            System.err.println();
            System.err.println("Example: Airports DE,US");
        } else {
            Airports t = new Airports();

            switch (numArgs) {
                case 1:
                    String csvCountryCodes = args[0];
                    t = new Airports(csvCountryCodes);
                    break;
                case 4:
                    Integer lllon = Integer.parseInt(args[0]);
                    Integer lllat = Integer.parseInt(args[1]);
                    Integer urlon = Integer.parseInt(args[2]);
                    Integer urlat = Integer.parseInt(args[3]);
                    t = new Airports(lllon, lllat, urlon, urlat, null);
                    break;
                default:
                    break;
            }

            t.printAll();
            System.out.println(t.airports.size());

        }

    }

}
