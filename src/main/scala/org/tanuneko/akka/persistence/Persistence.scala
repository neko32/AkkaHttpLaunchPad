package org.tanuneko.akka.persistence

import com.redis.RedisClientPool

import scala.concurrent.{ExecutionContext, Future}

trait Persistence {

  def creditCardFieldName = "CreditCard"

  def creditCardKey = "app:cc"

  def add[A](key: String, field: String, elem: A): Either[Exception, Boolean]
  def get(key: String, field: String): Future[Option[String]]
  def del(key: String): Long

}

class RedisPersistence(host: String, port: Int)(implicit ec: ExecutionContext) extends Persistence {

  lazy val redisPool = new RedisClientPool(host, port)

  override def add[A](key: String, field: String, elem: A): Either[Exception, Boolean] = {
    redisPool.withClient { cl =>
      cl.hset(key, field, elem)
    } match {
      case true => Right(true)
      case _ => Left(new Exception("Failed to add to Redis"))
    }
  }

  override def get(key: String, field: String): Future[Option[String]] = {
    Future {
      redisPool.withClient { cl =>
        cl.hget(key, field)
      }
    }
  }

  override def del(key: String): Long = {
    redisPool.withClient { cl =>
      cl.del(key)
    }.getOrElse(0)
  }
}
