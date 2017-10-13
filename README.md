# Transaction Generator

To build:
```
mvn clean package
```

To run:
```
mvn exec:java -Dexec.mainClass=App -Dexec.args="{detector} {rates} {seconds}"
```
Where:

* detector: the name of the detector
* rates: comma-delimited list of rates (TPS)
* seconds: number of seconds to run each rate

Example:
```
mvn exec:java -Dexec.mainClass=App -Dexec.args="transactions 100 20"
```

This example will generate transactions for the `transactions` detector at 100 TPS for 20 seconds.

This will post events to the Kafka topic as defined in `client.properties`.