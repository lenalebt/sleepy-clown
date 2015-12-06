package models

import java.time.Duration

import scala.language.implicitConversions

/**
  * A track that consists of multiple locations.
  */
class Track(seq: Seq[Location]) {
  import Track.seqLocationToTrack

  //time must not go backwards!
  require(seq.foldLeft((true, seq.head.dateTime))((tuple, location) =>
    (tuple._1 && !tuple._2.isBefore(location.dateTime), location.dateTime))._1)

  lazy val distance: Double = seq match {
    case head :: tail => tail.foldLeft(head, 0.0)((accum, elem) => (elem, accum._2 + accum._1.distance(elem)))._2
    case Nil          => 0.0
  }

  lazy val averagePace = Duration.between(seq.head.dateTime, seq.last.dateTime).getSeconds / (distance * 1000.0)

  /**return the fastest kilometer, start index together with duration*/
  lazy val fastestKilometer: Option[(Int, Duration)] = fastestDistance(1000.0)

  /**returns the fastest distance (in meters), start index together with duration*/
  def fastestDistance(distance: Double): Option[(Int, Duration)] = {
    val newSeq = (0 to seq.size).flatMap(slidingWindowOfLength(_, distance)).zipWithIndex
    newSeq.toSeq match {
      case Nil => None //can happen when the given distance is longer than the distance of this track
      case list =>
        val min = list.minBy(_._1.distance)
        val (track, index) = min
        Some((index, Duration.between(track.seq.head.dateTime, track.seq.last.dateTime)))
    }
  }

  /**creates a new sub-sequence that has all points to form a given distance, if any*/
  private def slidingWindowOfLength(startIndex: Int, length: Double): Option[Seq[Location]] = {
    val subSeq = seq.drop(startIndex)
    (0 to subSeq.size).map(subSeq.take).find(_.distance > length)
  }
}

object Track {
  implicit def seqLocationToTrack(seq: Seq[Location]): Track = new Track(seq)
}
