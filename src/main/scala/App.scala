object App {
  def main(args: Array[String]): Unit = {
    // scala type system is bad
    val exit = () => {
      System.exit(1)
      throw new RuntimeException()
    }

    // parse command line arguments
    val (detector, rates, seconds) = (() => {
      if (args.length != 3) {
        println("Invalid command line arguments")
        println("Expecting: {detector name} {rates} {seconds}")
        exit()
      }
      // detector name
      val detector = args(0)
      // rates to produce to Kafka auth stream in auths per second
      val rates = args(1).split(",").map(_.toInt)
      // number of seconds to test each rate
      val seconds = args(2).toInt

      (detector, rates, seconds)
    }) ()

    // get the auth generator for the chosen detector
    val getAuth =
      (detector match {
        case "transactions" => TransactionGenerator
        case _ =>
          println("An invalid detector name was given")
          exit()
      }).authGenerator

    // repeat test for each rate
    rates.foreach(rate => {
      Thread.sleep(3000)
      Producer.test(getAuth)(seconds)(rate)
    })
  }
}