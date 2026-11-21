import org.scalatest.funsuite.AnyFunSuite
import org.scalactic.Tolerance.convertNumericToPlusOrMinusWrapper

class ExamTestsForStudents extends AnyFunSuite:
  private val tolerance = 0.001

  private val visitorCentre = Waypoint("Visitor Centre", GeoPoint(0.0, 0.0, 120.0))
  private val bridge = Waypoint("Derrybawn Bridge", GeoPoint(3.0, 0.0, 180.0))
  private val lookout = Waypoint("Spinc Lookout", GeoPoint(3.0, 4.0, 480.0))
  private val saddle = Waypoint("Lugduff Saddle", GeoPoint(6.0, 8.0, 400.0))
  private val summit = Waypoint("Lugnaquilla Summit", GeoPoint(6.0, 10.0, 920.0))
  private val meadow = Waypoint("Amber Meadow", GeoPoint(4.5, 6.0, 515.5))

  private val glendaloughLoop = Trail(
    id = "T1",
    name = "Glendalough Loop",
    difficulty = Difficulty.Easy,
    waypoints = Vector(visitorCentre, bridge, lookout, visitorCentre)
  )

  private val spincRidge = Trail(
    id = "T2",
    name = "Spinc Ridge",
    difficulty = Difficulty.Hard,
    waypoints = Vector(lookout, saddle, summit)
  )

  private val valleyStroll = Trail(
    id = "T3",
    name = "Amber Valley Stroll",
    difficulty = Difficulty.Easy,
    waypoints = Vector(lookout, meadow)
  )

  private val emptyTrail = Trail("T0", "Empty Trail", Difficulty.Moderate, Vector.empty)

  private val guide = TrailGuide(Vector(glendaloughLoop, spincRidge, valleyStroll))

  // EXAM TESTS PART 1

  test("Part1: totalDistance sums horizontal distances between consecutive waypoints") {
    assert(glendaloughLoop.totalDistance === 12.0 +- tolerance)
    assert(valleyStroll.totalDistance === 2.5 +- tolerance)
    assert(emptyTrail.totalDistance === 0.0 +- tolerance)
  }

  test("Part1: totalElevationGain sums only positive elevation climbs") {
    assert(glendaloughLoop.totalElevationGain === 360.0 +- tolerance)
    assert(spincRidge.totalElevationGain === 520.0 +- tolerance)
    assert(valleyStroll.totalElevationGain === 35.5 +- tolerance)
  }

  // EXAM TESTS PART 2

  test("Part2: formattedSummary sorts trails alphabetically by name and formats metrics") {
    val expected =
      Vector(
        "Amber Valley Stroll (Easy): 2.50km, +35.5m",
        "Glendalough Loop (Easy): 12.00km, +360.0m",
        "Spinc Ridge (Hard): 7.00km, +520.0m"
      ).mkString("\n")
    assert(guide.formattedSummary === expected)
  }

  test("Part2: countByDifficulty includes all three Difficulty keys even when count is zero") {
    val expected = Map(
      Difficulty.Easy -> 2,
      Difficulty.Moderate -> 0,
      Difficulty.Hard -> 1
    )
    assert(guide.countByDifficulty === expected)
  }

  // EXAM TESTS PART 3

  test("Part3: mostCommonWaypoint finds the waypoint appearing on the most trails") {
    assert(guide.mostCommonWaypoint === Some(("Spinc Lookout", 3)))
  }

  test("Part3: mostCommonWaypoint counts a repeated waypoint only once per trail") {
    val loopTrail = Trail(
      id = "L1",
      name = "Triple Loop",
      difficulty = Difficulty.Easy,
      waypoints = Vector(visitorCentre, bridge, visitorCentre, visitorCentre)
    )
    val connectorTrail = Trail(
      id = "L2",
      name = "Bridge Connector",
      difficulty = Difficulty.Moderate,
      waypoints = Vector(bridge, lookout)
    )
    val smallGuide = TrailGuide(Vector(loopTrail, connectorTrail))
    assert(smallGuide.mostCommonWaypoint === Some(("Derrybawn Bridge", 2)))
  }

  test("Part3: steepestSegment finds the climbing segment with maximum gradient across all trails") {
    guide.steepestSegment match
      case Some((trailName, gradient)) =>
        assert(trailName === "Spinc Ridge")
        assert(gradient === 260.0 +- tolerance)
      case None =>
        fail("Expected Some((trailName, gradient))")
  }

  // EXAM TESTS PART 4

  test("Part4: nearestWaypoint returns the closest waypoint on trails of the required difficulty") {
    guide.nearestWaypoint(GeoPoint(6.0, 7.0, 999.0), Difficulty.Hard) match
      case Some((waypointName, distance)) =>
        assert(waypointName === "Lugduff Saddle")
        assert(distance === 1.0 +- tolerance)
      case None =>
        fail("Expected Some((waypointName, distance))")
  }

  test("Part4: nearestWaypoint returns None when no trails match the required difficulty") {
    assert(guide.nearestWaypoint(GeoPoint(0.0, 0.0, 0.0), Difficulty.Moderate) === None)
  }

  test("Part4: classifyByLength partitions trails at the inclusive minKm threshold preserving order") {
    val (longTrails, shortTrails) = guide.classifyByLength(7.0)
    assert(longTrails === Vector(glendaloughLoop, spincRidge))
    assert(shortTrails === Vector(valleyStroll))
  }
