package org.tanuneko.akka

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import org.tanuneko.akka.model.AddCreditCardReq
import org.tanuneko.akka.persistence.Persistence
import org.tanuneko.akka.service.CreditCardStore
import spray.json._

trait RestRouter extends EventMarshalling {

  implicit def persistence: Persistence
  import akka.http.scaladsl.model.StatusCodes._

  def routes: Route = healthCheckHandler ~ creditHandler
  def creditCardStoreService = new CreditCardStore()


  def creditHandler =
    pathPrefix("credit") {
      // add credit card to the user
      post {
        entity(as[AddCreditCardReq]) { req =>
          creditCardStoreService.store(req.userId, req.creditCard) match {
            case Right(_) => complete(
              HttpResponse(
                OK,
                entity = HttpEntity(ContentTypes.`application/json`,
                s"${req.userId}'s cc[${req.creditCard.last4Digit}] was added successfully"
                )
              )
            )
            case _ => complete(
              HttpResponse(
              OK,
              entity = HttpEntity(ContentTypes.`application/json`,
                s"${req.userId} has already registered cc[${req.creditCard.last4Digit}]"
                )
              )
            )
          }
        }
      } ~
      // get credit card info for the specified user
      get {
        parameters('userId.as[Int]) { userId =>
          onSuccess(persistence.get(s"${persistence.creditCardKey}:${userId}", persistence.creditCardFieldName)) {
            case Some(v) => complete(HttpResponse(OK, entity = HttpEntity(ContentTypes.`application/json`, v.toJson.prettyPrint)))
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
