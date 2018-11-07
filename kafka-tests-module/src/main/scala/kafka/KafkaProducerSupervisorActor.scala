package kafka

import akka.actor.SupervisorStrategy._
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props}
import akka.stream.Materializer

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object KafkaProducerSupervisorActor {
  def props()(
    implicit
    ec:           ExecutionContext,
    materializer: Materializer
  ): Props =
    Props(new KafkaProducerSupervisorActor())
}

class KafkaProducerSupervisorActor()(
  implicit
  ec:           ExecutionContext,
  materializer: Materializer
) extends Actor
  with ActorLogging {

  val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  val config = context.system.settings.config
  val kafkaProducer: ActorRef = context.actorOf(KafkaProducerActor.props())

  val maxNrOfRetries: Int = config.getInt("rating.actor.supervision.maxNrOfRetries")
  val withinTimeRange: FiniteDuration =
    Duration
      .apply(config.getString("rating.actor.supervision.withinTimeRange"))
      .asInstanceOf[FiniteDuration]

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries, withinTimeRange) {
      case e: Exception ⇒
        logger.warn("KafkaSupervisorActor restarting a failed child actor", e)
        Restart
    }

  override def receive: Receive = {
    case m => kafkaProducer ! m
  }
}
