# planes
Planes Simulator

## Compile

- Clone the repo
- Compile `mvn install`

## Airports

This class loads the airport data from [openflights.org](https://openflights.org/data.html).

The data was enhanced by doing a spatial join with [TM World Borders](http://thematicmapping.org/downloads/world_borders.php) using QGIS.  The Nearest Neighbor Join ([NNJoin](https://plugins.qgis.org/plugins/NNJoin/)) plugin was used to match each airport to the nearest country and join the ISO 2 character code and country name with the airports data.  **Note:** The airports dat has a Country field; however, some of the names identified were not the country the Airport was in.

You can get a list of Airports from command line.  For example to get a list of Aiports for United State, Canada, and Mexico. `java -cp target/planes.jar org.jennings.planes.Airports US,CA,MX`

The last line of the output is the number of airports found. 


## Routes 

This Class can be used to create a random routes file. The Routes file (json) contains an array of Route. Each Route is defined by a array of waypoints.  

<pre>
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
</pre>

Example command:
<pre>
java -cp target/planes.jar org.jennings.planes.Routes GermanyRoutes_2days.json 100 172800 DE
</pre>

- Creates a Route File named: GermanyRoutes_2days.json
- The file will have 100 routes 
- Minimum duration of a route will be 172,800 seconds (2 days)
- The routes will only contain airports in DE (Germany)

## CreatePlaneEventsFiles

This class creates events give a routes file.

The command line takes several parameters
<pre>
Usage: CreatePlaneEventsFiles routeFile numThings outputFolder prefix startTime step durationSec samplesPerFile format <latLimit>
</pre>

For example:

<pre>
java -cp target/planes.jar org.jennings.planes.CreatePlaneEventsFiles GermanyRoutes_2days.json 200 /home/david/testfolder DEplanes now 60 3600000 1000000 txt
</pre>

- Creates 200 planes on the routes defined in GermanyRoutes_2days.json
- Output files are sent to /home/david/testfolder
- Output files are prefixed with "DEplanes" and contain at most 1,000,000 events
- The startTime "now" uses the current system time as startTime.  Samples created every 60 seconds for 36,000,000 
- The output format will be txt (comma separated values)

This created 12 files DEplanes00001 to DEplanes00012. Each with 1,000,000 events.

The files contain UTF-8 characters.  

You can convert to ASCII using a Linux command `iconv -f UTF8 -t ASCII//TRANSLIT DEplanes00006 -o DEplanes00006.ascii.csv`







