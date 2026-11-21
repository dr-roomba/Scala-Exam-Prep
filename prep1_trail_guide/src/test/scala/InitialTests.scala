import org.scalatest.funsuite.AnyFunSuite
import org.scalactic.Tolerance.convertNumericToPlusOrMinusWrapper

class InitialTests extends AnyFunSuite:
  private val tolerance = 0.001

  private val pStart = GeoPoint(0.0, 0.0, 120.0)
  private val pMid = GeoPoint(3.0, 4.0, 480.0)
  private val pEnd = GeoPoint(3.0, 4.0, 300.0)

  private val wStart = Waypoint("Start", pStart)
  private val wMid = Waypoint("Mid", pMid)
  private val wEnd = Waypoint("End", pEnd)

  test("Initial: GeoPoint horizontalDistanceTo ignores elevation and computes 2D Euclidean distance") {
    assert(pStart.horizontalDistanceTo(pMid) === 5.0 +- tolerance)
    assert(pMid.horizontalDistanceTo(pEnd) === 0.0 +- tolerance)
    assert(wStart.distanceTo(wMid) === 5.0 +- tolerance)
  }

  test("Initial: GeoPoint elevationChangeTo computes signed elevation change") {
    assert(pStart.elevationChangeTo(pMid) === 360.0 +- tolerance)
    assert(pMid.elevationChangeTo(pEnd) === -180.0 +- tolerance)
  }

  test("Initial: Trail waypointCount, isEmpty, startWaypoint, and endWaypoint") {
    val emptyTrail = Trail("T0", "Empty", Difficulty.Easy, Vector.empty)
    val threePointTrail = Trail("T1", "Ridge", Difficulty.Moderate, Vector(wStart, wMid, wEnd))

    assert(emptyTrail.waypointCount === 0)
    assert(emptyTrail.isEmpty === true)
    assert(emptyTrail.startWaypoint === None)
    assert(emptyTrail.endWaypoint === None)

    assert(threePointTrail.waypointCount === 3)
    assert(threePointTrail.isEmpty === false)
    assert(threePointTrail.startWaypoint === Some(wStart))
    assert(threePointTrail.endWaypoint === Some(wEnd))
  }

  test("Initial: TrailGuide trailCount, isEmpty, and trailById") {
    val t1 = Trail("T1", "Ridge", Difficulty.Moderate, Vector(wStart, wMid))
    val t2 = Trail("T2", "Peak", Difficulty.Hard, Vector(wMid, wEnd))
    val guide = TrailGuide(Vector(t1, t2))

    assert(TrailGuide.empty.trailCount === 0)
    assert(TrailGuide.empty.isEmpty === true)
    assert(TrailGuide.empty.trailById("T1") === None)

    assert(guide.trailCount === 2)
    assert(guide.isEmpty === false)
    assert(guide.trailById("T2") === Some(t2))
    assert(guide.trailById("UNKNOWN") === None)
  }

  test("Initial: TrailGuide addTrail returns an updated copy without mutating the original") {
    val t1 = Trail("T1", "Ridge", Difficulty.Easy, Vector(wStart, wMid))
    val original = TrailGuide.empty
    val updated = original.addTrail(t1)

    assert(original.trailCount === 0)
    assert(updated.trailCount === 1)
    assert(updated.trailById("T1") === Some(t1))
  }
