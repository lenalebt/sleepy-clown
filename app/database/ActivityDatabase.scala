package database

import models.{Activity, IdType}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * activities database used to store activities
  */
trait ActivityDatabase {
  def getBy(personId: IdType): Future[Seq[Activity]]
  def getBy(personId: IdType, id: IdType): Future[Option[Activity]] =
    for { elements <- getBy(personId) } yield elements.find(_.id == id)
  def add(personId: IdType, activity: Activity): Future[Activity]
  def upsert(personId: IdType, activity: Activity): Future[Activity]
  def deleteBy(personId: IdType, id: IdType): Future[Unit]
}

object InMemoryActivityDatabase extends ActivityDatabase {
  var activities: Map[IdType, Seq[Activity]] = Map.empty.withDefault(key => Seq.empty)

  override def getBy(personId: IdType): Future[Seq[Activity]] = Future.successful(activities(personId))

  override def upsert(personId: IdType, activity: Activity): Future[Activity] = {
    activities = activities.updated(personId, activities(personId).filter(_.id == activity.id) :+ activity)
    Future.successful(activity)
  }

  override def add(personId: IdType, activity: Activity): Future[Activity] = {
    activities = activities.updated(personId, activities(personId) :+ activity)
    Future.successful(activity)
  }

  override def deleteBy(personId: IdType, id: IdType): Future[Unit] = {
    activities = activities.updated(personId, activities(personId).filter(_.id == id))
    Future.successful(Unit)
  }
}

trait ActivityDatabaseAware {
  protected val activityDatabase = InMemoryActivityDatabase
}
