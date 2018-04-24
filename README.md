# planes
Planes Simulator

## Compile

The planes project uses a Great Circle calculation provide by my [geotools](https://github.com/david618/geotools) project.

### Clone and build geotools
```
git clone https://github.com/david618/geotools
cd geotools
mvn install
```

### Clone and build planes
```
git clone https://github.com/david618/planes
cd planes
mvn install
```

## Airports Class

Loads the airport data from [openflights.org](https://openflights.org/data.html).

The data was enhanced by doing a spatial join with [TM World Borders](http://thematicmapping.org/downloads/world_borders.php) using QGIS.  The Nearest Neighbor Join ([NNJoin](https://plugins.qgis.org/plugins/NNJoin/)) plugin was used to match each airport to the nearest country and join the ISO 2 character code and country name with the airports data.  **Note:** The airports dat has a Country field; however, some of the names identified were not the country the Airport was in.

You can get a list of Airports from command line.  For example to get a list of Aiports for United State, Canada, and Mexico. `java -cp target/planes.jar org.jennings.planes.Airports US,CA,MX`

The last line of the output is the number of airports found. 


## Routes Class

Can be used to create a random routes file. The Routes file (json) contains an array of Route. Each Route is defined by a array of waypoints.  

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

## CreatePlaneEventsFiles Class

Creates events give a routes file.

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


## Schema
Fields
- id : Plane ID : integer
- ts : Epoch Time (ms) : long
- speed : Speed (km/s) : double
- dist : Distance to next dest (km) : double
- bearing : Bearing (degrees) measured from North : double
- rtid : Route ID (Multiple Planes can be on same route : integer
- orig : Origin airport name for this segment of the route : text
- dest : Destination airport name for this segment of the route : text
- secsToDep : Number of seconds before the plane with leave orig : integer
- lon : Current WGS84 longitude of plane
- lat : Current WGS84 latitude of plane

Sample CSV Output 
<pre>
0,1506955709148,186.53,4152.59,73.47,1,"Lavrentiya Airport","Wright-Patterson Air Force Base",-1,-132.46616,65.2584
1,1506955709148,199.14,3342.2,69.42,2,"Cascavel Airport","N'Djamena International Airport",-1,-10.78228,-3.51841
2,1506955709148,225.7,100.11,-77.1,3,"Tari Airport","Bougouni Airport",-1,-6.60775,11.57939
3,1506955709148,221.87,9914.93,16.74,4,"Salalah Airport","Mountain Village Airport",-1,56.0495,23.03242
4,1506955709148,264.42,2316.09,66.37,5,"Nancy-Essey Airport","Christchurch International Airport",-1,152.50794,-30.0481
5,1506955709148,319.47,2534.36,86.81,6,"Mountain Village Airport","Yariguíes Airport",-1,-85.60453,26.86844
6,1506955709148,254.39,1609.1,158.26,7,"Chièvres Air Base","Port St Johns Airport",-1,25.36625,-17.63205
7,1506955709148,152.99,5371.1,165.33,8,"Northeast Florida Regional Airport","Santa Rosa Airport",-1,-76.25148,10.38219
8,1506955709148,133.65,1660.18,-4.49,9,"Dane County Regional Truax Field","Kodinsk Airport",-1,104.70218,73.24852
9,1506955709148,236.1,7123.63,80.97,10,"Lake Havasu City Airport","Malamala Airport",-1,-23.43474,10.30753
</pre>




