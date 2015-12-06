package models

import java.util.UUID

/**
  * A person that can perform an activity
  */
class Person(
    val id: IdType = UUID.randomUUID(),
    /**weight in kilograms*/
    val weight: Double,
    /**size in meters*/
    val size: Double,
    val name: String
) {
  lazy val bodyMassIndex: Double = size * size / weight
}
