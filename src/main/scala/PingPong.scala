import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import language.postfixOps
import scala.concurrent.duration._

case object Ping
case object Pong
case object MissedBall

class actorB extends Actor {
  var countDown = 0

  def receive = {
    case Pong =>
      if (countDown < 4) {
        countDown += 1
        println("Pong")
        sender() ! Ping
      } else {
        println("Мяч упущен")
        countDown = 0
        sender() ! MissedBall
      }
  }
}

class actorA(actorB: ActorRef) extends Actor {
  def receive = {
    case Ping =>
      println("Ping")
      actorB ! Pong

    case MissedBall =>
      println("Ping")
      actorB ! Pong
  }
}


object PingPong extends App {
  val system = ActorSystem("pingpong")

  val pinger = system.actorOf(Props[actorB](), "pinger")

  val ponger = system.actorOf(Props(classOf[actorA], pinger), "ponger")

  import system.dispatcher

  system.scheduler.scheduleOnce(500 millis) {
    ponger ! Ping
  }
}

