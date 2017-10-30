# Transaction Generator

## Run

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

## Kafka Setup

To run Kafka on an EC2 instance:

1. SSH onto the instance
  ```
  ssh -i <pem> <ec2-host>
  ```
  
2. Elevate
  ```
  sudo su -
  ```

3. Download and extract Kafka 0.11.
  ```
  wget https://www.apache.org/dyn/closer.cgi?path=/kafka/0.11.0.1/kafka_2.11-0.11.0.1.tgz
  tar -xvzf kafka_2.11-0.11.0.1.tgz
  cd kafka_2.11-0.11.0.1
  ```

4. Set `advertised.listeners` in `config/server.properties`.
  ```
  advertised.listeners=PLAINTEXT://<ec2-host>.compute-1.amazonaws.com:9092
  ```

5. Start Kafka.
  ```
  bin/zookeeper-server-start.sh config/zookeeper.properties &
  bin/kafka-server-start.sh config/server.properties &
  ```
