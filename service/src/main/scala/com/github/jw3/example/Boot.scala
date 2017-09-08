package com.github.jw3.example

import akka.NotUsed
import akka.actor.{Actor, ActorIdentity, ActorSystem, Identify, Props}
import akka.pattern.{AskTimeoutException, ask}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}


object Boot extends App with LazyLogging {
  implicit val system = ActorSystem("example")
  implicit val materializer = ActorMaterializer()
  implicit val timeout = Timeout(10.seconds)
  import system.dispatcher

  val localhost = sys.env("HOSTNAME")
  val remotehost = sys.env("TARGET")
  val fqsystem = s"akka.tcp://example@$remotehost:2551"
  val fqactor = s"$fqsystem/user/$remotehost"

  val actor = system.actorOf(Props[Foo], localhost)
  logger.info("started actor {}", actor.path)

  Thread.sleep(15000)

  val f = system.actorSelection(fqactor) ? Identify(NotUsed)
  f.onComplete {
    case Success(id: ActorIdentity) ⇒
      id.ref match {
        case Some(ref) ⇒
          logger.info("received response from {}", ref.path)
        case None ⇒
          logger.warn("did not receive actor identity from {}", fqactor)
      }


    case Failure(_: AskTimeoutException) ⇒
      logger.warn("query timed out. is {} running at {}", fqactor, fqsystem)

    case Failure(ex) ⇒
      logger.warn("failure {} resolving {}", ex.getMessage, fqactor)

    case m ⇒
      logger.warn("hmmmmm, {}", m)
  }
}

class Foo extends Actor {
  def receive = Actor.emptyBehavior
}
