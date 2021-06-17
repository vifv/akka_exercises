
import PingPong.system

import scala.language.postfixOps

object PingPongActorSelection extends App{

  val actorA = system.actorSelection("akka://pingpong/user/pinger")
  val actorB = system.actorSelection("akka://pingpong/user/ponger")
}



