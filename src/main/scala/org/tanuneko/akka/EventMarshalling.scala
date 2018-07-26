package org.tanuneko.akka

import spray.json._

case class Health(name: String, status: String)

case class Error(msg: String)

class EventMarshalling extends DefaultJsonProtocol {

  implicit val heathFormat = jsonFormat2(Health)

}
