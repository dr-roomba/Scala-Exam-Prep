import org.scalatest.funsuite.AnyFunSuite
import org.scalactic.Tolerance.convertNumericToPlusOrMinusWrapper
import LockerSize.*

class InitialTests extends AnyFunSuite:
  val tolerance = 0.001

  test("Initial: LockerSize capacities match specification") {
    assert(Small.maxWeightKg === 2.0 +- tolerance)
    assert(Small.maxVolumeCm3 === 5000.0 +- tolerance)
    assert(Medium.maxWeightKg === 10.0 +- tolerance)
    assert(Medium.maxVolumeCm3 === 25000.0 +- tolerance)
    assert(Large.maxWeightKg === 25.0 +- tolerance)
    assert(Large.maxVolumeCm3 === 80000.0 +- tolerance)
  }

  test("Initial: LockerSize.canHold checks weight and volume boundaries") {
    assert(Small.canHold(2.0, 5000.0))
    assert(!Small.canHold(2.1, 1000.0))
    assert(!Small.canHold(1.0, 5000.1))
    assert(Medium.canHold(5.0, 12000.0))
    assert(Large.canHold(25.0, 80000.0))
  }

  test("Initial: Compartment starts free with occupant None") {
    val comp = Compartment(1, Small)
    assert(comp.occupant === None)
    assert(comp.isFree)
  }

  test("Initial: LockerStation.empty has zero compartments") {
    val station = LockerStation.empty("DUB-01")
    assert(station.stationId === "DUB-01")
    assert(station.totalCompartments === 0)
    assert(station.freeCount === 0)
    assert(station.compartmentWithId(1) === None)
  }

  test("Initial: LockerStation.withSizes numbers compartments from 1") {
    val station = LockerStation.withSizes("DUB-02", Vector(Small, Medium, Large))
    assert(station.totalCompartments === 3)
    assert(station.freeCount === 3)
    assert(station.compartmentWithId(1) === Some(Compartment(1, Small, None)))
    assert(station.compartmentWithId(2) === Some(Compartment(2, Medium, None)))
    assert(station.compartmentWithId(3) === Some(Compartment(3, Large, None)))
    assert(station.compartmentWithId(4) === None)
  }

  test("Initial: LockerNetworkManager tracks initial station count") {
    val emptyManager = LockerNetworkManager()
    assert(emptyManager.stationCount === 0)

    val s1 = LockerStation.withSizes("DUB-01", Vector(Small))
    val s2 = LockerStation.withSizes("COR-01", Vector(Medium, Large))
    val populated = LockerNetworkManager(Vector(s1, s2))
    assert(populated.stationCount === 2)
  }
