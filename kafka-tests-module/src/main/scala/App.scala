import kafka.{KafkaProducerActor, KafkaProducerSettings}

import scala.concurrent.ExecutionContext.Implicits.global
object App {

  val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    val producer = new KafkaProducerActor()
    producer.queue.watchCompletion().onComplete {
      case m =>
        logger.info(s"Gracefully closing the actor system $m")
        KafkaProducerSettings.system.terminate()
    }
    producer.post("Hi there")

    Thread.sleep(3000)
    logger.info("Closing the stream")
    producer.queue.complete()

  }

}
