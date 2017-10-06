import java.io.FileInputStream
import java.util.Properties

import org.apache.kafka.clients.producer._


object Config {
  val fileProperties = new Properties()
  fileProperties.load(new FileInputStream("client.properties"))

  val properties = new Properties()
  properties.put("zookeeper.connect", fileProperties.get("producer.bootstrap.servers"))
  properties.put("metadata.broker.list", fileProperties.get("producer.bootstrap.servers"))
  properties.put("metadata.broker.list", fileProperties.get("producer.bootstrap.servers"))
  properties.put("zookeeper.session.timeout.ms", "400")
  properties.put("zookeeper.sync.time.ms", "200")
  properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, fileProperties.get("producer.bootstrap.servers"))
  properties.put("producer.type", "async")
  properties.put("request.required.acks", "1")
  properties.put("acks", "all")
}

object Producer {
  // output auths to stream at the rate given
  val test = (getAuth: Function[Any, String]) => (seconds: Int) => (rate: Int) => {
    val (send, close) = (() => {
      // request resource pool
      val producers = Range(1, 32)
        .map(_ => new KafkaProducer[String, String](Config.properties))

      // distribute work and send
      val partitions = Config.fileProperties.get("partitions").toString.toInt
      var counter = 0
      val send = (str: String) => {
        producers(counter % producers.length)
          .send(new ProducerRecord(Config.fileProperties.getProperty("topic"), counter % partitions, "key", str))
        counter = counter + 1
      }

      // close resources and ensure flush
      val close = () => producers.foreach(_.close())

      (send, close)
    }) ()

    println(s"Producing to Kafka at a rate of $rate auths per second for $seconds second(s)")

    val totalAuths = seconds * rate
    val totalTime = seconds * 1000
    val startTime = System.currentTimeMillis().toInt

    var sentAuths = 0
    // polled sending of auths
    while (sentAuths < totalAuths) {
      val elapsedTime = (System.currentTimeMillis() - startTime).toInt
      val currentAuths = Math.min(elapsedTime, totalTime) * totalAuths / totalTime

      for (a <- sentAuths until currentAuths) {
        send(getAuth())
      }

      sentAuths = currentAuths

      Thread.sleep(1)
    }

    close()

    println(s"Finished producing to Kafka at a rate of $rate auths per second for $seconds second(s)")
  }
}