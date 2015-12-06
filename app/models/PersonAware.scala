package models

import database.PersonDatabaseAware

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * something that knows to what person it belongs
  */
trait PersonAware extends PersonDatabaseAware {
  val personId: IdType
  def person: Future[Person] = personDatabase.getBy(personId).map(_.get)
}
