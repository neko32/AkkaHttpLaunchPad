package org.tanuneko.akka

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.Future
import scala.util.{Failure, Success}

object App extends RequestTimeout {

  def main(args: Array[String]):Unit = {

    implicit val sys = ActorSystem("my")
    implicit val ec = sys.dispatcher
    implicit val mat = ActorMaterializer()

    val api = new RestApi(sys, requestTimeout).routes
    val bindingFuture: Future[ServerBinding] =
      Http().bindAndHandle(api, "localhost", 9500)
    val log = Logging(sys.eventStream, "my")
    bindingFuture.map { serverBinding =>
      log.info(s"REST API is bound to ${serverBinding.localAddress}")
    }.onComplete {
      case Success(_) => log.info(s"Success to bind")
      case Failure(ex) =>
        log.error(ex, "fail!")
        sys.terminate()
    }

  }

}

trait RequestTimeout {
  import scala.concurrent.duration._
  def requestTimeout: Timeout = {
    val d = Duration("10s")
    FiniteDuration(d.length, d.unit)
  }
}
