package models

/**
  * A sports activity that has calorie
  */
trait SportsActivity extends Activity {
  val kilojoulesBurnt: Double
  lazy val caloriesBurnt: Double = kilojoulesBurnt / 4.1868
  val distance: Double
}

trait SportsActivityWithTrack extends SportsActivity with TrackAware {
  override val distance = track.distance
}
