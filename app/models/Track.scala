package models

import java.time.{ZonedDateTime, Duration}

import scala.language.{postfixOps, implicitConversions}
import scala.xml.{NodeSeq, Elem}

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

case class GpxLocation(
  override val lat:           Double,
  override val lon:           Double,
  override val elevation:     Option[Double] = None,
  override val dateTime:      ZonedDateTime,
  override val heartbeatRate: Option[Double] = None
) extends Location

object Track {
  private implicit class RichNode(val node: NodeSeq) {
    def textOpt: Option[String] = Option(node.text) match {
      case None | Some("") => None
      case Some(text)      => Some(text)
    }
  }

  implicit def seqLocationToTrack(seq: Seq[Location]): Track = new Track(seq)
  def fromGpx(gpx: Elem): Seq[Track] = {
      def parseLocation(loc: Elem): Location = {
        GpxLocation(
          lat = (loc \@ "lat").toDouble,
          lon = (loc \@ "lon").toDouble,
          dateTime = ZonedDateTime.parse(loc \ "time" text), elevation = (loc \ "ele" textOpt).map(_.toDouble),
          heartbeatRate = (loc \ "ele" textOpt).map(_.toDouble)
        )
      }
      def parseTrack(trk: Elem): Track = {
        (trk \ "trkseg" \ "trkpt").collect{ case loc: Elem => parseLocation(loc) }
      }

    (gpx \ "trk").collect{
      case trk: Elem => parseTrack(trk)
    }
  }
}
