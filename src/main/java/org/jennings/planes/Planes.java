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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * @author david
 */
public class Planes {

    ArrayList<Plane> planes = new ArrayList<>();
    // Specify Routes 
    Routes routes = new Routes();
    //or specify a route file name
    //String routeFileName;

    public Planes() {
    }

    public Planes(String routeFileName) {

        this.routes.load(routeFileName);
    }

    /**
     *
     * Generated plane on each route
     *
     * @param routes
     */
    public Planes(Routes routes) {
        this.routes = routes;

    }

    public void createPlanes() {
        Random rnd = new Random();
        int i = 0;
        while (i < routes.rts.size()) {
            int rndoffset = rnd.nextInt(Integer.MAX_VALUE);
            Plane t = new Plane(i, this.routes.get(i), rndoffset);
            planes.add(t);
            i++;
        }
    }

    /**
     * Save planes to json file This file would include route info for each
     * plane.
     *
     * @param filename
     */
    public void save(String filename) {

        OutputStreamWriter osw = null;

        try {

            JSONArray jsonPlanes = new JSONArray();

            for (Plane plane : planes) {
                jsonPlanes.put(plane.getJSON());
            }

            osw = new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8);

            jsonPlanes.write(osw);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (JSONException e) {
            System.err.println(e.getMessage());
        } finally {
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    // ok to ignore
                }
            }
        }

    }

    /**
     * Load planes from json file
     *
     * @param filename
     */
    public void load(String filename) {

    }

    /**
     *
     * Generated numPlanes on random routes
     *
     * @param routes
     * @param numPlanes
     */
    public Planes(Routes routes, Integer numPlanes) {

    }

    /**
     * Combine the planes returns into JSON
     */
    public void getPlanesJSON(long timeMillis) {
        for (Plane plane : planes) {
            plane.setPosition(timeMillis);
            System.out.println(plane.getPlaneJSON().toString());
        }
        System.out.println();
    }

    public void getPlanesJSON() {
        getPlanesJSON(System.currentTimeMillis());
    }

    public void getPlanesCSV(long timeMillis) {

        for (Plane plane : planes) {
            plane.setPosition(timeMillis);
            System.out.println(plane.getPlaneJSON().toString());
        }

    }

    public void getPlanesCSV() {
        getPlanesCSV(System.currentTimeMillis());
    }

    public static void main(String args[]) {

        try {

            Routes routes = new Routes();
            //t.createRandomRouteFile("routesThreeDay10.json", 10, 86400*3);
            //routes.load("routesOneDay100.json");
            //routes.load("routesOneDayUS10.json");
            routes.createRandomRoutes(10, 86400);
            Planes t = new Planes(routes);

            //Planes t = new Planes("routes10000_2day.json");
            t.createPlanes();
            long st = System.currentTimeMillis();
            long ct = st;
            while (ct < st + 1000 * 10) {
                t.getPlanesCSV();
                ct += 1000;
            }

            //t.save("planes.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
