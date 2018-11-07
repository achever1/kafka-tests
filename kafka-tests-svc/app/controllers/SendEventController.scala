package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.google.inject.Inject
import kafka.KafkaProducerActor
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext

class SendEventController @Inject()(cc: ControllerComponents)(
    implicit ec: ExecutionContext, system: ActorSystem, mat: Materializer)
    extends AbstractController(cc) {

  val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)
  val producer = system.actorOf(KafkaProducerActor.props())

  def send = Action {
    logger.info("Post a msg")
    producer ! "post"
    Ok("Msg posted! ")
  }

  def close = Action {
    logger.info("Complete and close the stream")
    producer ! "close"
    Ok("Producer closed ! ")
  }

}
