package org.tanuneko.akka

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.Future

class RestApi(sys: ActorSystem, timeout: Timeout) extends RestRouter {

  implicit val requestTimeout = timeout
  implicit val ec = sys.dispatcher

}

trait RestRouter extends EventMarshalling {

  import akka.http.scaladsl.model.StatusCodes._

  def routes: Route = healthCheckRoute

  def healthCheckRoute = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val f = Future {
      Health("tanuapp", "Healthy")
    }
    pathPrefix("health") {
      pathEndOrSingleSlash {
        get {
          // GET /health
          onSuccess(f) { h =>
            complete(OK, h)
          }
        }
      }
    }
  }

}
