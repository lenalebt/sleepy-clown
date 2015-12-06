package models

import java.time.ZonedDateTime

/**
  * A location (with elevation, date and time)
  */
trait Location {
  val lat: Double
  val lon: Double
  val elevation: Double
  val dateTime: ZonedDateTime
  val heartbeatRate: Option[Double]

  def distance(other: Location): Double = haversineDistance((lat, lon), (other.lat, other.lon))

  private def haversineDistance(pointA: (Double, Double), pointB: (Double, Double)): Double = {
    val deltaLat = math.toRadians(pointB._1 - pointA._1)
    val deltaLong = math.toRadians(pointB._2 - pointA._2)
    val a = math.pow(math.sin(deltaLat / 2), 2) +
      math.cos(math.toRadians(pointA._1)) * math.cos(math.toRadians(pointB._1)) * math.pow(math.sin(deltaLong / 2), 2)
    val greatCircleDistance = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    6371.000785 * greatCircleDistance
  }
}
