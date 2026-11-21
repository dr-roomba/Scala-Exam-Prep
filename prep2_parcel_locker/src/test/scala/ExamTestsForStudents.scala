import org.scalatest.funsuite.AnyFunSuite
import org.scalactic.Tolerance.convertNumericToPlusOrMinusWrapper
import LockerSize.*

class ExamTestsForStudents extends AnyFunSuite:
  val tolerance = 0.001

  private def makeCode(raw: String): ParcelCode =
    ParcelCode(raw).getOrElse(fail(s"Expected valid ParcelCode for '$raw'"))

  private def makeParcel(rawCode: String, weightKg: Double, volumeCm3: Double): Parcel =
    Parcel(makeCode(rawCode), weightKg, volumeCm3)
      .getOrElse(fail(s"Expected valid Parcel for ($rawCode, $weightKg, $volumeCm3)"))

  // EXAM TESTS PART 1

  test("Part1: ParcelCode trims, uppercases, and validates length and characters") {
    assert(ParcelCode("  ab12cd  ").map(_.value) === Some("AB12CD"))
    assert(ParcelCode("1234").map(_.value) === Some("1234"))
    assert(ParcelCode("AB1") === None)
    assert(ParcelCode("AB-1234") === None)
    assert(ParcelCode("   ") === None)
  }

  test("Part1: Parcel validates positive finite weight and volume") {
    val code = makeCode("PKG100")
    val valid = Parcel(code, 1.5, 4000.0)
    assert(valid.isDefined)
    valid.foreach { p =>
      assert(p.code === code)
      assert(p.weightKg === 1.5 +- tolerance)
      assert(p.volumeCm3 === 4000.0 +- tolerance)
    }
    assert(Parcel(code, 0.0, 4000.0) === None)
    assert(Parcel(code, 1.5, -10.0) === None)
    assert(Parcel(code, Double.NaN, 100.0) === None)
  }

  test("Part1: Parcel.parse parses valid CSV lines and rejects malformed input") {
    val parsed = Parcel.parse("  ab1234 , 2.5 , 6000.0 ")
    assert(parsed.isDefined)
    parsed.foreach { p =>
      assert(p.code.value === "AB1234")
      assert(p.weightKg === 2.5 +- tolerance)
      assert(p.volumeCm3 === 6000.0 +- tolerance)
    }
    assert(Parcel.parse("AB1234,notANumber,6000.0") === None)
    assert(Parcel.parse("AB1234,2.5") === None)
  }

  // EXAM TESTS PART 2

  test("Part2: deposit places parcel in smallest fitting free compartment") {
    val station = LockerStation(
      "DUB-01",
      Vector(Compartment(1, Large), Compartment(2, Small), Compartment(3, Medium))
    )
    val smallParcel = makeParcel("SML1", 1.0, 2000.0)
    val afterFirst = station.deposit(smallParcel).getOrElse(fail("Expected deposit to succeed"))

    assert(station.freeCount === 3)
    assert(afterFirst.freeCount === 2)
    assert(afterFirst.compartmentWithId(2).flatMap(_.occupant) === Some(smallParcel))

    val bulkyParcel = makeParcel("BLK1", 1.0, 10000.0)
    val afterSecond = afterFirst.deposit(bulkyParcel).getOrElse(fail("Expected second deposit to succeed"))
    assert(afterSecond.compartmentWithId(3).flatMap(_.occupant) === Some(bulkyParcel))
    assert(afterSecond.compartmentWithId(1).exists(_.isFree))
  }

  test("Part2: deposit breaks equal-size ties by lowest compartment id and rejects duplicates") {
    val station = LockerStation(
      "DUB-02",
      Vector(Compartment(10, Medium), Compartment(4, Medium))
    )
    val p1 = makeParcel("DUP1", 5.0, 10000.0)
    val updated = station.deposit(p1).getOrElse(fail("Expected deposit to succeed"))

    assert(updated.compartmentWithId(4).flatMap(_.occupant) === Some(p1))
    assert(updated.compartmentWithId(10).exists(_.isFree))

    val duplicate = makeParcel("dup1", 3.0, 4000.0)
    assert(updated.deposit(duplicate) === None)
  }

  test("Part2: collect removes matching parcel and frees its compartment") {
    val station = LockerStation.withSizes("DUB-03", Vector(Small, Medium))
    val p1 = makeParcel("COL1", 1.5, 3000.0)
    val deposited = station.deposit(p1).getOrElse(fail("Expected deposit to succeed"))

    val collectedResult = deposited.collect(p1.code)
    assert(collectedResult.isDefined)
    collectedResult.foreach { (parcel, afterCollect) =>
      assert(parcel === p1)
      assert(afterCollect.freeCount === 2)
      assert(afterCollect.collect(p1.code) === None)
    }
  }

  // EXAM TESTS PART 3

  test("Part3: stats computes counts, total weight, and heaviestCode with tie-breaking") {
    val empty = LockerStation.empty("EMPTY-01")
    assert(empty.stats === StationStats(0, 0, 0.0, None))

    val base = LockerStation(
      "DUB-04",
      Vector(Compartment(5, Medium), Compartment(2, Medium), Compartment(8, Small))
    )
    val p1 = makeParcel("P111", 4.5, 8000.0)
    val p2 = makeParcel("P222", 4.5, 8000.0)
    val p3 = makeParcel("P333", 1.0, 1000.0)

    val loaded = base
      .deposit(p1)
      .flatMap(_.deposit(p2))
      .flatMap(_.deposit(p3))
      .getOrElse(fail("Expected all three deposits to succeed"))

    val s = loaded.stats
    assert(s.totalCompartments === 3)
    assert(s.occupiedCount === 3)
    assert(s.totalWeightKg === 10.0 +- tolerance)
    assert(s.heaviestCode === Some("P111"))
  }

  test("Part3: depositBatch deposits valid lines in order and skips invalid or unfitting ones") {
    val station = LockerStation.withSizes("DUB-05", Vector(Small, Medium))
    val lines = List(
      "PKG1, 1.5, 3000",
      "bad,line",
      "pkg1, 1.0, 1000",
      "PKG2, 30.0, 1000",
      "PKG3, 8.0, 20000"
    )
    val (finalStation, depositedCount) = station.depositBatch(lines)
    assert(depositedCount === 2)
    assert(finalStation.freeCount === 0)
    assert(finalStation.compartmentWithId(1).flatMap(_.occupant).map(_.code.value) === Some("PKG1"))
    assert(finalStation.compartmentWithId(2).flatMap(_.occupant).map(_.code.value) === Some("PKG3"))
  }

  // EXAM TESTS PART 4

  test("Part4: addStation and instance apply manage stations without duplicates") {
    val manager = LockerNetworkManager()
    val s1 = LockerStation.withSizes("DUB-01", Vector(Small, Medium))

    assert(manager.addStation(s1))
    assert(!manager.addStation(s1))
    assert(manager.stationCount === 1)
    assert(manager("DUB-01") === Some(s1))
    assert(manager("UNKNOWN") === None)
  }

  test("Part4: depositAt and collectFrom delegate updates to the target station") {
    val s1 = LockerStation.withSizes("DUB-01", Vector(Small, Medium))
    val manager = LockerNetworkManager(Vector(s1))
    val p1 = makeParcel("NET1", 1.2, 2500.0)

    assert(manager.depositAt("DUB-01", p1))
    assert(manager("DUB-01").map(_.freeCount) === Some(1))
    assert(!manager.depositAt("UNKNOWN", p1))

    assert(manager.collectFrom("DUB-01", p1.code) === Some(p1))
    assert(manager("DUB-01").map(_.freeCount) === Some(2))
    assert(manager.collectFrom("DUB-01", p1.code) === None)
  }

  test("Part4: locateParcel finds the station holding a parcel or returns None") {
    val s1 = LockerStation.withSizes("DUB-01", Vector(Small))
    val s2 = LockerStation.withSizes("COR-01", Vector(Medium))
    val manager = LockerNetworkManager(Vector(s1, s2))
    val p1 = makeParcel("LOC1", 4.0, 9000.0)

    assert(manager.locateParcel(p1.code) === None)
    assert(manager.depositAt("COR-01", p1))
    assert(manager.locateParcel(p1.code) === Some("COR-01"))
    assert(manager.collectFrom("COR-01", p1.code) === Some(p1))
    assert(manager.locateParcel(p1.code) === None)
  }
