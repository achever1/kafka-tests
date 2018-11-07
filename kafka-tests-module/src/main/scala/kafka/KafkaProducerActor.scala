package kafka

import akka.NotUsed
import akka.actor.{Actor, Props}
import akka.pattern.pipe
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.{
  Keep,
  RestartSink,
  Sink,
  Source,
  SourceQueueWithComplete
}
import akka.stream.{Materializer, OverflowStrategy}
import org.apache.kafka.clients.producer.ProducerRecord

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object KafkaProducerActor {
  def props()(implicit
              ec: ExecutionContext,
              materializer: Materializer): Props =
    Props(new KafkaProducerActor())

}

class KafkaProducerActor extends Actor {

  import KafkaProducerSettings._

  val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  override def receive: Receive = {
    case "post"  => post("hello")
    case "close" => queue.complete()
    case akka.actor.Status.Failure(throwable) =>
      logger.error(
        s"Error received in KafkaProducer actor $throwable. Propagating to supervisor"
      )
      throw throwable
  }

  def supervisedProducerPlainSink[K, V](
      producerSettings: ProducerSettings[K, V]
  ): Sink[ProducerRecord[K, V], NotUsed] =
    RestartSink.withBackoff(10 second, 10 seconds, 0.01)(
      () => {
        logger.info("Starting or restarting Kafka sink in KafkaProducer")
        Producer.plainSink(
          producerSettings,
          producerSettings.createKafkaProducer()
        )
      }
    )

  val queue: SourceQueueWithComplete[String] = Source
    .queue[String](0, OverflowStrategy.backpressure)
    .map { qor =>
      logger.info(s"Out of the queue : $qor")
      qor
    }
    .map(s => new ProducerRecord[Array[Byte], String](topic, s))
    .toMat(supervisedProducerPlainSink(producerSettings))(Keep.left)
    .run()

  queue.watchCompletion().onComplete {
    case m =>
      logger.info(s"Stream competed $m")
      //KafkaProducerSettings.system.terminate()
  }

  def post(message: String): Unit =
    queue
      .offer(message)
      .map { qor =>
        logger.info(s"Enqueued message in Kafka producer stream $qor")
        qor
      } andThen {
      case Success(r) ⇒ logger.info(r.toString)
      case Failure(f) ⇒ logger.error(f.getMessage, f)
    } pipeTo self

}
