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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;
import org.json.JSONObject;

/**
 *
 * @author david
 */
public class GenerateRandomData {
    
    /*
    Don't use float for decimal numbers in json; they are converted to double and you get some weird numbers.
    The number of decimal places presented in output is whatever it needs to be.
    
    Json also returns E (Expontential Notation) for large numbers
    */
    
    /**
     * When outputting numbers you may not want to send the full double precision number with 15 decimal places
     * Valid decimalPlaces values are 0 to 15; otherwise the number is returned as sent in
     * 
     * @param num
     * @param decimalPlaces
     * @return num rounded to decimalPlaces
     */
    public double round(double num, int decimalPlaces) {
        // For large numbers the decimal is dropped and rounding take off digits on exponential notation
        if (decimalPlaces < 15 && decimalPlaces >= -10) {
            double a = Math.pow(10, decimalPlaces);
            return Math.round(num*a)/a;
        } else {
            return num;
        }
    }
    
    /**
     * Return a double between the min and max values with numDecimalPlaces precision
     * @param min
     * @param max
     * @param numDecimalPlaces
     * @return 
     */
    public double generateDouble(double min, double max, int numDecimalPlaces) {
        double num;
        
        Random rnd = new Random();
        
        num = rnd.nextDouble()*(max - min) + min;
        num = round(num, numDecimalPlaces);
        
        return num;
    }
    
    /**
     * Generate Integer from min to max
     * @param min
     * @param max
     * @return 
     */
    public int generateInt(int min, int max) {
        int num;
        
        Random rnd = new Random();
        
        num = rnd.nextInt(max - min + 1) + min;        
       
        return num;
    }    

    /**
     * Generate Long from min to max
     * @param min
     * @param max
     * @return 
     */
    public long generateLong(long min, long max) {
        
        Random rnd = new Random();        
        
        long range = max - min;
        
        int rng = 0;
        if (range > Integer.MAX_VALUE) {
            rng = Integer.MAX_VALUE;
        } else {
            rng = (int)(max - min);
        }
                              
        long num = min + rnd.nextInt(rng + 1);
       
        return num;
    }    
    
    /**
     * Generated long starting from min within range (rng)
     * @param min
     * @param rng
     * @return 
     */
    public long generateLong(long min, int rng) {
        
        Random rnd = new Random();        
        
        long num = min + rnd.nextInt(rng + 1);
       
        return num;
    }        


    /**
     * Return formated date string given a startDate and number of seconds 
     * @param startDate
     * @param ms
     * @param format
     * @return 
     */
    public String generateDate(Date startDate, long ms, String format) {

        // http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html
        SimpleDateFormat fm = new SimpleDateFormat(format);
        Date dt = new Date();
        
        dt.setTime(startDate.getTime() + ms);
        
        return fm.format(dt);
    }
    
    /**
     * Return formated date string given a startDate and number of seconds 
     * @param startDate
     * @param seconds
     * @param format
     * @return 
     */
    public String generateDate(Date startDate, int seconds, String format) {

        // http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html
        SimpleDateFormat fm = new SimpleDateFormat(format);
        Date dt = new Date();
        dt.setTime(startDate.getTime() + seconds*1000);
        
        return fm.format(dt);
    }
    
    /**
     * Tries to make something that is somewhat readable.
     * @param numchars
     * @param addNumsSpecial
     * @return 
     */
    public String generateWord2(int numchars, boolean addNumsSpecial) {
        Random random = new Random();
        char[] uconsonants = {'B','D','F','G','H','K','L','M','N','P','R','S','R','V','W','Z'};
        char[] consonants = {'b','d','f','g','h','k','l','m','n','p','r','s','t','v','w','x','z'};        
        char[] vowels = {'a','e','i','o','u'};
        char[] nums = {'0','1','2','3','4','5','6','7','8','9'};
        char[] special = {'!','@','#','$','%','^','&','*','_'};
        
        int numucon = uconsonants.length;
        int numcon = consonants.length;
        int numvow = vowels.length;
        int numnum = nums.length;
        int numspe = special.length;
        
        int typeChars = 3;
        if (addNumsSpecial) {
            typeChars = 5;
        }
        
        // consant-vowel-constant and repeat
        char[] word = new char[numchars];
        for (int j = 0; j < word.length; j++) {
            int a = j%typeChars;
            switch (a) {
                case 0:
                    word[j] = uconsonants[random.nextInt(numucon)];
                    break;
                case 1:
                    word[j] = vowels[random.nextInt(numvow)];
                    break;
                case 2:
                    word[j] = consonants[random.nextInt(numcon)];                    
                    break;
                case 3:
                    word[j] = nums[random.nextInt(numnum)];                    
                    break;
                case 4:
                    word[j] = special[random.nextInt(numspe)];                    
                    break;
                default:
                    System.err.println("Invalid Type");
            }
            
        }
        return new String(word);
        
    }
    
    /**
     * Just a list of random ascii Letters
     * @param numchars
     * @return 
     */
    public String generateWord(int numchars) {
        Random random = new Random();
        char[] word = new char[numchars];
        for (int j = 0; j < word.length; j++) {
            if (random.nextBoolean()) {
                word[j] = (char) ('a' + random.nextInt(26));
            } else {
                word[j] = (char) ('A' + random.nextInt(26));
            }
        }
        return new String(word);
    }
    
    
    
    public void testJson() {
        String url = "http://www.javacreed.com/simple-gson-example/";
        int i = 123456;
        double d = 1234567890123456789d;
        
        double dtemp = 1200000000/3.234;
        
        Double d2 = round(dtemp,1);
        
        
        JSONObject json1 = new JSONObject();
        
        json1.put("url", url);
        json1.put("i", i);
        json1.put("d", d);
        json1.put("d2", d2);
        
        

        System.out.println(dtemp);
        System.out.println(d2);
        
        
        System.out.println(json1.toString());
        System.out.println(json1.toString(2));

//        myUrl a = new myUrl(url);
//        
//        JSONObject json2 = new JSONObject(a);
//        
//        System.out.println(json2.toString());
//        
//        Gson gson = new GsonBuilder().create();
//        System.out.println(gson.toJson(a));
        
        
    }

    public void testGetInt() {
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i<1000; i++) {
            //System.out.println(generateDouble(0.0, 800.0, 2));
            int a = generateInt(-30000,30000);
            if (a > max) {
                max = a; 
            }            
            if (a < min) {
                min = a; 
            }            
        }
        System.out.println(min);
        System.out.println(max);
    }

    public void testGetLong() {
        long max = Long.MIN_VALUE;
        long min = Long.MAX_VALUE;
        for (int i = 0; i<1000; i++) {
            //System.out.println(generateDouble(0.0, 800.0, 2));
            long a = generateLong(123456780000L,123456790000L);
            if (a > max) {
                max = a; 
            }            
            if (a < min) {
                min = a; 
            }            
        }
        System.out.println(min);        
        System.out.println(max);
        
        max = Long.MIN_VALUE;
        min = Long.MAX_VALUE;
        for (int i = 0; i<1000; i++) {
            //System.out.println(generateDouble(0.0, 800.0, 2));
            long a = generateLong(123456780000L,800000);
            if (a > max) {
                max = a; 
            }            
            if (a < min) {
                min = a; 
            }            
        }
        System.out.println(min);
        System.out.println(max);
        
    }
    
    public void testDate() {
        
        GregorianCalendar gc = new GregorianCalendar(2016, Calendar.MARCH, 1, 1, 0, 0);
        Date dt = gc.getTime();
        
        String format = "MM/dd/yyyy hh:mm:ss.SSS";
      
        
        for (long i = 0; i<=86400000; i=i+390137) {
            System.out.println(generateDate(dt, i, format));
        }
        
    }
    
    
    
    
    public static void main(String args[]) {
        GenerateRandomData t = new GenerateRandomData();
        
        //Random rnd = new Random();
        
        Date dt = new Date();
        
        SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
        
        System.out.println(dt);
        System.out.println(ft.format(dt));
        System.out.println(dt.getTime());
        
        Calendar cal = new GregorianCalendar();
        cal.setTime(dt);
        
        System.out.println(cal.get(Calendar.MONTH));
                       
        System.out.println(cal.get(Calendar.DAY_OF_YEAR));
        
        System.out.println(cal.get(Calendar.SECOND));
        
        Calendar cal2 = new GregorianCalendar();
        cal2.set(2017, 0, 1, 0, 0, 0);
        System.out.println(ft.format(cal2.getTime()));
        
        // http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html
       
        String pattern = "GG MM/dd/yyyy";
        ft = new SimpleDateFormat(pattern);
        System.out.println(ft.format(dt));
        
        pattern = "MM/dd/yyyy kk:mm:ss.SSS ZZZ";
        ft = new SimpleDateFormat(pattern);      
        System.out.println(ft.format(dt));

        pattern = "MM/dd/yyyy kk:mm:ss.SSS";
        ft = new SimpleDateFormat(pattern);
        ft.setTimeZone(TimeZone.getTimeZone("GMT"));
        System.out.println(ft.format(dt));

        System.out.println(t.generateWord(15));
        System.out.println(t.generateWord2(15, true));
        
    }
}
