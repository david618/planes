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

The data was enhanced by doing a spatial join with [TM World Borders](http://thematicmapping.org/downloads/world_borders.php) using QGIS.  The Nearest Neighbor Join ([NNJoin](https://plugins.qgis.org/plugins/NNJoin/)) plugin was used to match each airport to the nearest country and join the ISO 2 character code and country name with the airports data.   The resulting file [airports_countries.dat](./airports_countries.dat)

**Note:** The Open Flights `airports.dat` has a country field; however, some of the names identified were not the country the Airport was in.

### List all Airports

`java -cp target/planes.jar org.jennings.planes.Airports`

The dataset contains just over 7,000 airports.

### Get List of Aiports by Country Code
You can get a list of Airports from command line.  For example to get a list of Aiports for United State, Canada, and Mexico. `java -cp target/planes.jar org.jennings.planes.Airports US,CA,MX`  The last line of the output is the number of airports found. 

### Get List of Airports by Bounding Box

```
java -cp target/planes.jar org.jennings.planes.Airports 0 0 10 10

Cadjehoun Airport
Nnamdi Azikiwe International Airport
Akure Airport
Benin Airport
...
São Tomé International Airport
Sam Mbakwe International Airport
Asaba International Airport
Akwa Ibom International Airport
25
```

## Routes Class

Creates a random Routes File. Provides a quick way to create a set of random routes.

Parameters
- Desired Route File Name
- Number of Routes
- Duration in Seconds

### Algotihm Overview

The following Algorithm results in a deterministic position for simulated planes on routes given a time.

Each Route consists of a set of Waypoints. The set of Wayoints provide a predicatable time series that can be used to provide a predicatable position on the route.  For example; given these waypoints.

```
    "wpts": [
      {
        "st": 394,
        "distance": 958.1792577481264,
        "bearing": -82.84259622084379,
        "origin": "Page Municipal Airport",
        "destination": "San Carlos Airport",
        "lon": -111.447998,
        "id": 4124,
        "lat": 36.92610168,
        "speed": 0.22956721907413002,
        "et": 4568
      },
      {
        "st": 5256,
        "distance": 4089.5575505642364,
        "bearing": 69.58146536033003,
        "origin": "San Carlos Airport",
        "destination": "Morristown Municipal Airport",
        "lon": -122.25,
        "id": 7683,
        "lat": 37.511901855469,
        "speed": 0.19272472916988517,
        "et": 26476
      },
      {
        "st": 26876,
        "distance": 3211.106595432875,
        "bearing": -85.50935562258216,
        "origin": "Morristown Municipal Airport",
        "destination": "Page Municipal Airport",
        "lon": -74.4149017333984,
        "id": 7657,
        "lat": 40.7994003295898,
        "speed": 0.2692611203446172,
        "et": 38802
      }
```

The position is found for an Aircraft by taking the current Epoch time and doing a Modulo on et of last waypoint (38,802) resulting in a Time in Route (t) from 0 to 38,801.  

```
0     <= t < 394   : On Ground at Page Municipal Airport
394   <= t < 4568  : Enroute from Page Municipal Airport to  San Carlos Airport
4568  <= t < 5256  : On Ground at San Carlos Airport
5256  <= t < 26476 : Enroute from San Carlos Airport to Morristown Municipal Airport
26476 <= t < 26876 : On Ground at Morristown Municipal Airport
26876 <= t < 38802 : Enroute from Morristown Municipal Airport to Page Municipal Airport
```

Each plane is randomly assigned an offset into the route so mulitple planes can be on the same route. 

The Enroute positions are calculated using Great Circle path between origin and destination assuming a fixed speed.  



### Create 10,000 Random Routes 

`java -cp target/planes.jar org.jennings.planes.Routes routes10000_2day.json 10000 172800`


### Create Random Routes File in Germany
```
java -cp target/planes.jar org.jennings.planes.Routes GermanyRoutes_2days.json 100 172800 DE
```
- Creates a Route File named: GermanyRoutes_2days.json
- The file will have 100 routes 
- Minimum duration of a route will be 172,800 seconds (2 days)
- The routes will only contain airports in DE (Germany)


## CreatePlaneEventsFiles Class

Creates events give a Routes File.

The command line takes several parameters
```
Usage: CreatePlaneEventsFiles routeFile numPlanes outputFolder prefix startTime step durationSec samplesPerFile format (latLimit)
```
- routeFile: Name of the route file to use
- numPlanes: Number of planes to assign to the routes. If more planes than routes then routes will be used more than once.
- outputFolder: Where the Events Files will be created
- prefix: Output files are number from 1 to 99999 and zero padded (e.g. with prefix `planes` the first file is planes00001)
- step: Number of seconds between samples
- durationSec: Number of seconds to run simulation for
- samplesPerFile: Limit the number of samples in a file to this number 
- format: txt or json 
- (latlimit): If specified any events with a latitude magnitude greater than latlimit will not be created. You can use this to prevent creating latitude's that are out of range for a specific SRID (e.g. SRID 102100 cannot support latitudes outside range -89 to 89.

For example:

<pre>
java -cp target/planes.jar org.jennings.planes.CreatePlaneEventsFiles GermanyRoutes_2days.json 200 /home/david/testfolder DEplanes now 60 3600000 1000000 txt
</pre>

- Creates 200 planes on the routes defined in GermanyRoutes_2days.json
- Output files are sent to /home/david/testfolder
- Output files are prefixed with "DEplanes" and contain at most 1,000,000 events
- The startTime "now" uses the current system time as startTime.  Samples created every 60 seconds for 3,600,000 
- The output format will be txt (comma separated values)

This created 12 files DEplanes00001 to DEplanes00012. Each with 1,000,000 events.

Number of Events = durationSec / step * numPlanes




## Converting To ASCII

The names in the Data contain UTF-8 Characters.  

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




