package org.tanuneko.akka.service

import org.tanuneko.akka.model.CreditCard
import org.tanuneko.akka.persistence.Persistence
import org.tanuneko.akka.EventMarshalling
import spray.json._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class CreditCardStore {

  def store(userId: Int, cc: CreditCard)(implicit persistence: Persistence) = {
    import EventMarshalling._
    val ccs = Await.result(persistence.get(s"${persistence.creditCardKey}:${userId}", persistence.creditCardFieldName), Duration("10s"))
      .fold {
        // if not found
        println(s"${userId} has not registered credit card yet")
        List(cc)
      } { existingCCs =>
        // if already exists
        val jsAst = existingCCs.parseJson
        val obj = jsAst.convertTo[List[CreditCard]]
        println(s"Already found credit cards registered for user ${userId}")
        obj :+ cc
      }
    persistence.add(s"${persistence.creditCardKey}:${userId}", persistence.creditCardFieldName, ccs.toJson)
  }

}
