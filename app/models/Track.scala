package models

import java.time.Duration

/**
  * A track that consists of multiple locations.
  */
trait Track extends Seq[Location] {
  //time must not go backwards!
  require(seq.foldLeft((true, head.dateTime))((tuple, location) =>
    (tuple._1 && !tuple._2.isBefore(location.dateTime), location.dateTime))._1)

  val distance: Double = seq match {
    case head :: tail => tail.foldLeft(head, 0.0)((accum, elem) => (elem, accum._2 + accum._1.distance(elem)))._2
    case Nil          => 0.0
  }

  val averagePace = Duration.between(head.dateTime, last.dateTime).getSeconds / (distance * 1000.0)
}
