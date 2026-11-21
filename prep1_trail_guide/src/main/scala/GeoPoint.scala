import scala.math.*

case class GeoPoint(x: Double, y: Double, elevation: Double):
  def horizontalDistanceTo(other: GeoPoint): Double =
    val dx = other.x - x
    val dy = other.y - y
    sqrt(dx * dx + dy * dy)

  def elevationChangeTo(other: GeoPoint): Double =
    other.elevation - elevation
