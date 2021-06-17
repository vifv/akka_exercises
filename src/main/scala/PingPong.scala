import PingPong.system
import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}
import akka.pattern.AskSupport
import akka.util.Timeout

import language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt

case class Ping()

case class Pong()

case class MissedBall()

class ActorA extends Actor with AskSupport {
  implicit val timeout: Timeout = Timeout(5 seconds)
  val ex: ExecutionContextExecutor = context.dispatcher
  val actorB: ActorSelection = system.actorSelection("akka://pingpong/user/ponger")

  override def preStart: Unit = {
    self ! Ping
  }

  override def receive: Receive = {
    case Ping =>
      println("Ping")
      (actorB ? Pong).mapTo[Pong].map { x=>
        self ! Ping
      }

    case MissedBall =>
      println("Ping")
      self ! Ping
  }
}

class ActorB extends Actor {

  override def receive: Receive = {
    case Pong =>
      if (scala.util.Random.nextInt(100) > 10) {
        println("Pong")
        sender() ! Ping
      } else {
        println("Мяч упущен")
        sender() ! MissedBall
      }
  }
}

object PingPong extends App {

  val system = ActorSystem("pingpong")

  val pinger: ActorRef = system.actorOf(Props[ActorB](), "ponger")
  val ponger: ActorRef = system.actorOf(Props[ActorA](), "pinger")
}