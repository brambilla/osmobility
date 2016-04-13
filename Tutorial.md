# Introduction #

OSMobility uses several open source softwares:

  * PostgreSQL
> > PostgreSQL, often simply "Postgres", is an open source object-relational database management system (ORDBMS) with an emphasis on extensibility and standards-compliance.

  * PostGIS
> > PostGIS is an open source software that adds support for geographic objects to the PostgreSQL object-relational database.

  * pgRouting
> > Adds routing and other network analysis functionalities. Advantages of the database routing approach are:
      * Data and attributes can be modified by many clients through JDBC, ODBC, or directly using Pl/pgSQL. The clients can either be PCs or mobile devices.
      * Data changes can be reflected instantaneously through the routing engine. There is no need for precalculation.
      * The “cost” parameter can be dynamically calculated through SQL and its value can come from multiple fields or tables.

  * OpenStreetMap
> > OpenStreetMap is a perfect data source to use for pgRouting, because it’s freely available and has no technical restrictions in terms of processing the data. Data availability still varies from country to country, but the worldwide coverage is improving day by day. OpenStreetMap uses a topological data structure:
      * Nodes are points with a geographic position.
      * Ways are lists of nodes, representing a polyline or polygon.
      * Relations are groups of nodes, ways and other relations which can be assigned certain properties.
      * Tags can be applied to nodes, ways or relations and consist of name=value pairs.

  * osm2pgrouting
> > osm2pgrouting is a command line tool that makes it easy to import OpenStreetMap data into a pgRouting database. It builds the routing network topology automatically and creates tables for feature types and road classes.

  * DEUS
> > DEUS is a general-purpose tool for creating simulations of complex systems. It provides a Java API which allows to implement:
      * nodes (i.e. the parts which interact in a complex system, leading to emergent behaviors such as humans, pets, cells, robots, intelligent agents, etc.).
      * events (e.g. node births/deaths, interactions among nodes, interactions with the environment, logs, etc. - briefly, whatever you want!).
      * processes (stochastic or deterministic, they regulate the timeliness of events).

# Installation and requirements #

Installation of pgRouting on Ubuntu can be done using packages from a Launchpad repository. All you need to do is to open a terminal window and run:
```
# Add pgRouting launchpad repository
sudo add-apt-repository ppa:georepublic/pgrouting-unstable
sudo apt-get update

# Install pgRouting package
sudo apt-get install postgresql-9.1-pgrouting

# Install osm2pgrouting package
sudo apt-get install osm2pgrouting
```
This will also install all required packages such as PostgreSQL and PostGIS if not installed yet.

**Note:**
  * Once pgRouting 2.0 has been released it will be available in the stable repository on Launchpad.
  * To be up-to-date with changes and improvements you might run sudo apt-get update & sudo apt-get upgrade from time to time.
  * To avoid permission denied errors for local users you can set connection method to trust in /etc/postgresql/9.1/main/pg\_hba.conf and restart PostgreSQL server with sudo service postgresql restart.

Open a terminal window and execute the following commands:
```
sudo -u postgres createdb osm

sudo -u postgres psql -d osm -f /usr/share/postgresql/9.1/contrib/postgis-1.5/postgis.sql

sudo -u postgres psql -d osm -f /usr/share/postgresql/9.1/contrib/postgis-1.5/spatial_ref_sys.sql

sudo -u postgres psql -d osm -f /usr/share/postgresql/9.1/contrib/pgrouting-2.0/pgrouting.sql
```
OSMobility will make use of OpenStreetMap data. More information how to get OSM data: http://wiki.openstreetmap.org/wiki/Downloading_data. An alternative for very large areas is the download services of Geofabrik (http://download.geofabrik.de/).

Now we have to import data into a PostgreSQL database. Open a terminal and execute the following commands:
```
osm2pgrouting -file "data.osm" -conf "/usr/share/osm2pgrouting/mapconfig.xml" -dbname osm -user postgres -clean

sudo -u postgres psql

\c osm

SELECT pgr_createTopology('ways', 0.00001, 'the_geom', 'gid');
```

**Note:**
  * If you want to enable remote connections to your database, edit /var/lib/pgsql/data/postgresql.conf and set listen\_addresses =  `'*'`, then edit /var/lib/pgsql/data/pg\_hba.conf and set host all all 0.0.0.0/0 trust


# Simulation #

First of all, read the DEUS [Tutorial](http://code.google.com/p/deus/wiki/Tutorial).

If you want to simulate MobileNode using OSMobility, all you need to do is to extend the abstract class MobileNode and implement onReceivedMessage(Message message, GeoNode, senderNode), onPathEnd() and onLocationChanged(Location location) methods.
Moreover, you have to extend MobileNodeBirthEvent and MobileNodeDeathEvent wich are the events associated to the birth and the death of the node. Obviously you can add functionalities to these events, overriding the run() method. Remember that, if you do this, you must call super.run() as first instruction.

OSMobility is based on DEUS simulator so inherits from it the same XML schema for the configuration of simulations. If you want to simulate mobile nodes with OSMobility, you have to include in your XML file the definitions of the MobileNode, of the MobileNodeBirthEvent, of MobileNodeDeathEvent and a Process.

Definition of  the MobileNodeBirthEvent:
```
<aut:event handler="it.unipr.ce.dsg.osmobility.example.CarNodeBirthEvent" id="CarNodeBirthEvent" oneShot="false">
		<aut:params>
		    <aut:param value="Parma.zip" name="paths" />
			<aut:param value="it.unipr.ce.dsg.osmobility.mobility.model.FluidTrafficModel"	name="mobilityModel" />
			<aut:param value="it.unipr.ce.dsg.osmobility.communication.model.DelayModel"	name="communicationModel" />
 			<aut:param value="200.0"	name="communicationModel#param1" /> <!-- uplink -->
			<aut:param value="2000.0"	name="communicationModel#param2" /> <!-- downlink -->
		</aut:params>
	</aut:event>
```
With these lines you define a DEUS event which creates a MobileNode that follows one of the paths inside the Parma.zip file, using the FluidTrafficModel as mobility model and the DelayModel as communication model. FluidTrafficModel and DelayModel are implementations of MobilityModel and CommunicationModel, respectively. You can define your own implementations of the Mobility and Communication model with custom parameters.

Definition of the  MobileNodeDeathEvent:
```
<aut:event handler="it.unipr.ce.dsg.osmobility.example.CarNodeDeathEvent" id="CarNodeDeathEvent">
	</aut:event>

Definition of the MobileNode:
<aut:node handler="it.unipr.ce.dsg.osmobility.example.CarNode" id="CarNode">
	</aut:node><aut:node handler="it.unipr.ce.dsg.osmobility.example.CarNode" id="CarNode">
	</aut:node>
```
Finally, you have to define a Process:
```
<aut:process handler="it.unipr.ce.dsg.deus.impl.process.RectangularPulsePeriodicProcess" id="CarNodeBirthPeriodicProcess">
		<aut:params>
			<aut:param value="1" name="period" />
			<aut:param value="0" name="startVtThreshold" />
			<aut:param value="4000" name="stopVtThreshold" />
		</aut:params>
		<aut:nodes>
			<aut:reference id="CarNode" />
		</aut:nodes>
		<aut:events>
			<aut:reference id="CarNodeBirthPeriodicEvent" />
		</aut:events>
	</aut:process>

```
**Note:**
  * You can obtain directly a .zip file containig the paths that your MobileNode will follow, using the PathGenerator of OSMobility. You have to specify some parameters:
    * the host where the database is located;
    * the name of the database;
    * the name of the user of the database;
    * the passoword of the user of the database;
    * the type of vehicle for which the paths are generated (MOTORCAR, MOTORCYCLE, HORSE, BICYCLE or FOOT);
    * the resolution in kilometers between the locations of the generated paths;
    * the name of the file containing the generated paths;
    * the way the paths are generated (RANDOM or SWITCHSTATION).