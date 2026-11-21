case class TrailGuide(trails: Vector[Trail]):
  def trailCount: Int = trails.length

  def isEmpty: Boolean = trails.isEmpty

  def trailById(id: String): Option[Trail] =
    trails.find(_.id == id)

  def addTrail(trail: Trail): TrailGuide =
    copy(trails = trails :+ trail)

  // Part 2
  def formattedSummary: String = ???

  def countByDifficulty: Map[Difficulty, Int] = ???

  // Part 3
  def mostCommonWaypoint: Option[(String, Int)] = ???

  def steepestSegment: Option[(String, Double)] = ???

  // Part 4
  def nearestWaypoint(from: GeoPoint, requiredDifficulty: Difficulty): Option[(String, Double)] = ???

  def classifyByLength(minKm: Double): (Vector[Trail], Vector[Trail]) = ???

object TrailGuide:
  val empty: TrailGuide = TrailGuide(Vector.empty)
