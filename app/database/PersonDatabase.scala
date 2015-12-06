package database

import models.{Person, Activity, IdType}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * persons database used to store activities
  */
trait PersonDatabase {
  def getAll: Future[Seq[Person]]
  def getBy(id: IdType): Future[Option[Person]] =
    for { elements <- getAll } yield elements.find(_.id == id)
  def add(activity: Person): Future[Person]
  def upsert(activity: Person): Future[Person]
  def deleteBy(id: IdType): Future[Unit]
}

object InMemoryPersonDatabase extends PersonDatabase {
  var persons: Seq[Person] = Seq.empty

  override def getAll: Future[Seq[Person]] = Future.successful(persons)

  override def upsert(person: Person): Future[Person] = {
    persons = persons.filter(_.id == person.id) :+ person
    Future.successful(person)
  }

  override def add(person: Person): Future[Person] = {
    persons = persons :+ person
    Future.successful(person)
  }

  override def deleteBy(id: IdType): Future[Unit] = {
    persons = persons.filter(_.id == id)
    Future.successful(Unit)
  }
}

trait PersonDatabaseAware {
  protected val personDatabase = InMemoryPersonDatabase
}
