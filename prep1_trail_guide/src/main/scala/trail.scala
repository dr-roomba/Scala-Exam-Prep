case class Waypoint(name: String, location: GeoPoint):
  def distanceTo(other: Waypoint): Double =
    location.horizontalDistanceTo(other.location)

enum Difficulty:
  case Easy, Moderate, Hard

case class Trail(
  id: String,
  name: String,
  difficulty: Difficulty,
  waypoints: Vector[Waypoint]
):
  def waypointCount: Int = waypoints.length

  def isEmpty: Boolean = waypoints.isEmpty

  def startWaypoint: Option[Waypoint] = waypoints.headOption

  def endWaypoint: Option[Waypoint] = waypoints.lastOption

  // Part 1
  def totalDistance: Double = ???

  def totalElevationGain: Double = ???
