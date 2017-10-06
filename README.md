# Transaction Generator

To build:
```
mvn package
```

To run:
```
mvn exec:java -Dexec.mainClass=App -Dexec.args="{detector} {rates} {seconds}"
```
Where:

* detector: the name of the detector
* rates: comma-delimited list of rates in auths per second
* seconds: number of seconds to run each rate

Example:
```
mvn exec:java -Dexec.mainClass=App -Dexec.args="example 100 20"
```

This example will generate transactions for the `example` detector at 50 TPS and then 500 TPS for 20 seconds each

This will post events to the Kafka topic as defined in `client.properties`.