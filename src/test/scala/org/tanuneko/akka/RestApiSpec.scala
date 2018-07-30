package org.tanuneko.akka

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{HttpMethod, HttpMethods, HttpRequest}
import akka.stream.ActorMaterializer
import akka.util.{ByteString, Timeout}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, WordSpec}
import org.tanuneko.akka.persistence.{Persistence, RedisPersistence}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.util.{Failure, Success, Try}

class RestApiSpec extends WordSpec with BeforeAndAfterAll {

  class InMemPersistence extends Persistence {

    private var db = scala.collection.mutable.Map.empty[String, String]

    override def add[A](key: String, field: String, elem: A): Either[Exception, Boolean] = {
      Try {
        db += (key + field) -> elem.asInstanceOf[String]
      } match {
        case Success(_) => Right(true)
        case _ => Left(new Exception("In Mem ops failed"))
      }
    }

    override def get(key: String, field: String): Option[String] = {
      println(s"@InMemPersistence::get(${key},${field})")
      db.get(key + field)
    }

    override def del(key: String): Long = {
      val ret = if(db.exists { case (k,v) => k == key}) 1 else 0
      db -= key
      ret
    }

    def reset = db.clear()
  }

  class MockRestAPI extends RestRouter {
    override def persistence: Persistence = new InMemPersistence
  }

  implicit val sys = ActorSystem("unittest")
  implicit val mat = ActorMaterializer()
  implicit val ec: ExecutionContext = sys.dispatcher

  override def beforeAll = {

    val api = new MockRestAPI()
    val bindingFuture: Future[ServerBinding] =
      Http().bindAndHandle(api.routes, "localhost", 19500)
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

  override def afterAll = {
    println("#### SHUTTING DOWN ALL!!! ####")
    mat.shutdown()
    sys.terminate.onComplete { _ => println("Actor System shutted down.")}
  }


  "RestApi" should {

    "Get a credit card but Persistence is empty" in {
      val rez = Http().singleRequest(HttpRequest(uri = "http://localhost:19500/credit?userId=32", method = HttpMethods.GET))
      rez
      .onComplete {
          case Success(res) => {
            val body = res.entity.dataBytes.runFold(ByteString(""))(_ ++ _)
          }
        }
      val r = Await.result(rez, Duration("10s"))
      println(r)
      assert(r.status.intValue == 404)
    }

    "Register card and get it" in {

    }
  }

}
