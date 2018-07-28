package org.tanuneko.akka

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import org.tanuneko.akka.model.{AddCreditCardReq}
import org.tanuneko.akka.persistence.Persistence
import spray.json._

trait RestRouter extends EventMarshalling {

  def persistence: Persistence
  import akka.http.scaladsl.model.StatusCodes._

  def routes: Route = healthCheckHandler ~ creditHandler


  def creditHandler =
    pathPrefix("credit") {
      // add credit card to the user
      post {
        entity(as[AddCreditCardReq]) { req =>
          persistence.add(s"${persistence.creditCardKey}:${req.userId}", persistence.creditCardFieldName, req.creditCard.toJson) match {
            case Right(_) => complete(
              HttpResponse(
                OK,
                entity = HttpEntity(ContentTypes.`application/json`,
                s"${req.userId}'s cc[${req.creditCard.last4Digit}] was added successfully"
                )
              )
            )
            case _ => complete(BadRequest, s"Failed to add ${req.userId}, cc[${req.creditCard.last4Digit}]")
          }
        }
      } ~
      // get credit card info for the specified user
      get {
        parameters('userId.as[Int]) { userId =>
          persistence.get(s"${persistence.creditCardKey}:${userId}", persistence.creditCardFieldName) match {
            case Some(cc) => complete(
              HttpResponse(
                OK,
                entity = HttpEntity(ContentTypes.`application/json`,
                  cc.toJson.prettyPrint)
              )
            )
            case _ => complete(NotFound)
          }
        }
      }
    }


  def healthCheckHandler= {
    pathPrefix("health") {
      pathEndOrSingleSlash {
        get {
          // GET /health
            complete(OK)
          }
        }
      }
    }

}
