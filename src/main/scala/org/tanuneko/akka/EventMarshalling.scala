package org.tanuneko.akka

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.tanuneko.akka.model.{AddCreditCardReq, CreditCard, User}
import spray.json._

case class Health(name: String, status: String)

case class Error(msg: String)

class EventMarshalling extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val heathFormat = jsonFormat2(Health)
  implicit val creditCardFormat = jsonFormat4(CreditCard)
  implicit val userFormat = jsonFormat2(User)
  implicit val addCCReqFormat = jsonFormat2(AddCreditCardReq)

}

object EventMarshalling extends EventMarshalling

