package models

/**
  * an object to mix in to activities that have tracks
  */
trait TrackAware {
  val track: Track
}
