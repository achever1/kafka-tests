package kafka

import java.util.UUID

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.stream.ActorMaterializer
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

object KafkaProducerSettings {

  val bootstrapServers = "kafka:9092"
  implicit val system = ActorSystem("kafka-tests-system")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher
  val topic = "test7"

  val producerSettings = ProducerSettings(
    system,
    new ByteArraySerializer,
    new StringSerializer
  ).withProperties(KafkaConfigs.defaultConf)
    .withBootstrapServers(bootstrapServers)

}

object KafkaConfigs {

  val defaultConf = Map.empty[String, String]

  val kafkaConfigMap = Map(
    "request.timeout.ms" -> "5000"
//    "reconnect.backoff.max.ms" -> "10000",
//    "reconnect.backoff.ms" -> "14000",
//    "client.id" -> s"${UUID.randomUUID()}"
  )
}
