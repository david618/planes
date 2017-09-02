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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


import org.json.JSONArray;
import org.json.JSONObject;


/**
 *
 * @author david
 */
public class Routes {
    
    ArrayList<Route> rts = new ArrayList<>();
    RouteBuilder rb;

    public Routes() {
        rb = new RouteBuilder();
    }
    
    public Routes(double lllon, double lllat, double urlon, double urlat) {
        rb = new RouteBuilder(lllon, lllat, urlon, urlat);
    }
    
    public Route get(int index) {
        
        Route rt = null;
            
        int i = index % rts.size();
        
        try {
            rt = rts.get(i);
            
        } catch (Exception e) {
            
        }
        
        return rt;
        
    }
    
    public void save(String filename) {
        try {

            JSONArray jsonRts = new JSONArray();
            
            for (Route rt: rts) {
                jsonRts.put(rt);
            }

            FileWriter fw = new FileWriter(filename);

            jsonRts.write(fw);
            fw.close();
        } catch (Exception e) {

        }
        
        
    }
    
    public void load(String filename) {
        try {
                        
            String text = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
            
            JSONArray jsonRts = new JSONArray(text);
            
            int i = 0;
            
            while (i < jsonRts.length()) {
                JSONObject jsonRt = jsonRts.getJSONObject(i);
                Route rt = new Route(jsonRt);
                rts.add(rt);
                
                i++;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public void createRandomRoutes(int numRoutes, long durationSecs) {
        try {
            

            

            for (int i = 1; i <= numRoutes; i++) {
                Route rt = rb.createRoute(durationSecs);
                rt.setId(i);
                rts.add(rt);
            }

            //System.out.println(jsonRts.toString(2));
        } catch (Exception e) {

        }
    }    
    
    public void createRandomRouteFile(String filename, int numRoutes, long durationSecs) {
        try {

            JSONArray jsonRts = new JSONArray();

            for (int i = 1; i <= numRoutes; i++) {
                Route rt = rb.createRoute(durationSecs);
                rt.setId(i);
                rts.add(rt);
                jsonRts.put(rt.getJSON());
            }

            FileWriter fw = new FileWriter(filename);

            jsonRts.write(fw);

            fw.close();
            //System.out.println(jsonRts.toString(2));
        } catch (Exception e) {

        }
    }

    @Override
    public String toString() {
        return "Routes{" + "rts=" + rts + '}';
    }

    
    

    public static void main(String[] args) throws Exception {
        Routes t = new Routes();
        
        // Create random route file; 1,000 routes; duration 4 days (86400 seconds/day)
        t.createRandomRouteFile("routes10000_2day.json", 1000, 86400*2);
        
        // Load Route File 
        // t.load("routes10000_4day.json");
        

    }
    
    
}
