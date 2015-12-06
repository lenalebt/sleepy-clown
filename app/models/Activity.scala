package models

import java.time.ZonedDateTime
import java.util.UUID

/**
  * A basic activity
  */
trait Activity extends PersonAware {
  val id: IdType = UUID.randomUUID()
  val startTime: ZonedDateTime
  val stopTime: ZonedDateTime
  val title: String
  val comment: String
}
