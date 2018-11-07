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

  override val supervisorStrategy =
    OneForOneStrategy(1000, Duration("1 day").asInstanceOf[FiniteDuration]) {
      case e: Exception ⇒
        logger.warn("KafkaSupervisorActor restarting a failed child actor", e)
        Restart
    }

  override def receive: Receive = {
    case m =>
      logger.info(s"in supervisor forwarding to producer")
      kafkaProducer ! m
  }
}
