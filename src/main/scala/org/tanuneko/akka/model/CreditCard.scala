package org.tanuneko.akka.model

case class User(id: Int, name: String)

case class CreditCard(name: String,
                      expiryMonth: Int,
                      expiryYear: Int,
                      last4Digit: Int)

case class AddCreditCardReq(userId: Int,
                            creditCard: CreditCard)
