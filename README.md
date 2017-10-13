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

The transaction generator reads a file `data.json` and pulls all accounts and merchants into two pools, then randomly generates a transaction based on the two pools. The same account and merchant information are used (account ID, email address, etc.), but a transaction ID, timestamp, and transaction amount are newly created. The transaction amount is based on transaction amounts for the merchant from `data.json`, within $0.50.