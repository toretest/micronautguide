# Documentation: Importing OSM Data into a Graph Database

## Step 1: Choose the Right Tool to Extract OSM Data
Since the raw OSM planet file is too large and OSM’s main API is too limited for this purpose, use one of these:

Option A: Geofabrik Norway Extract (Full Country)
https://download.geofabrik.de/europe/norway.html

https://download.geofabrik.de/europe/norway-latest.osm.pbf


Download the .osm.pbf file (compressed binary OSM XML)

Example file: norway-latest.osm.pbf (~500MB–2GB)

## Step 2: Parse the Data
You will receive either:

.osm.pbf → Use a parser like:

osmium

pyosmium

osmconvert

Overpass JSON/XML → Use simple parsers (Jackson, Python, or Kotlin)

For each result, extract:


```json
{
  "type": "node",
  "id": 123456,
  "lat": 59.911,
  "lon": 10.752,
  "tags": {
    "addr:street": "Karl Johans gate",
    "addr:housenumber": "22",
    "addr:postcode": "0159",
    "building": "yes"
  }
}
```

## Step 3: Transform to Graph Model
Suggested vertex and edge types:

🎯 Nodes
:Address – with street, housenumber, lat, lon, postcode

:Postcode – uniquely identified by addr:postcode

:Street – addr:street

🔗 Edges
(:Address)-[:ON]->(:Street)

(:Address)-[:IN]->(:Postcode)

(:Address)-[:LOCATED_AT]->(lat/lon node) (if you want coordinates as nodes)

## Step 4: Import to GraphDB
Depending on your stack:

For Gremlin/JanusGraph:
Use your existing Kotlin-based import pipeline (e.g., via GraphCrudService) to generate:

kotlin
Copy
Edit
g.addV("Address")
.property("street", "Karl Johans gate")
.property("housenumber", "22")
.property("postcode", "0159")
.property("lat", 59.911)
.property("lon", 10.752)
.next()

g.addV("Street").property("name", "Karl Johans gate").next()
g.addV("Postcode").property("code", "0159").next()
And connect with:

kotlin
Copy
Edit
g.V(address).addE("ON").to(street).iterate()
g.V(address).addE("IN").to(postcode).iterate()
