import org.scalatest.funsuite.AnyFunSuite
import org.scalactic.Tolerance.convertNumericToPlusOrMinusWrapper

class ExamTestsForStudents extends AnyFunSuite:
  val tolerance = 0.001

  val item1 = StockItem("I-1", 4.0, 100.0)
  val item2 = StockItem("I-2", 6.0, 450.0, fragile = true)
  val item3 = StockItem("I-3", 5.0, 200.0)
  val item4 = StockItem("I-4", 7.5, 320.0, fragile = true)
  val item5 = StockItem("I-5", 12.5, 50.0)

  val nestedPallet = Pallet("P-NEST", 10.0, List(item3, item4))
  val topPallet1 = Pallet("P-1", 30.0, List(item2, nestedPallet))
  val emptyPallet = Pallet("P-EMPTY", 15.0, Nil)
  val topPallet2 = Pallet("P-2", 10.0, List(item5))

  val depot = Warehouse(
    "Dublin-North",
    List(item1, topPallet1, emptyPallet, topPallet2)
  )

  // EXAM TESTS PART 1

  test("Part1: totalWeight computes single StockItem and nested Pallet weights") {
    assert(WarehouseOps.totalWeight(item1) === 4.0 +- tolerance)
    assert(WarehouseOps.totalWeight(nestedPallet) === 12.5 +- tolerance)
    assert(WarehouseOps.totalWeight(topPallet1) === 18.5 +- tolerance)
    assert(WarehouseOps.totalWeight(emptyPallet) === 0.0 +- tolerance)
  }

  test("Part1: topLevelPalletWeights includes only top-level pallets with nested item weights") {
    val weights = WarehouseOps.topLevelPalletWeights(depot)
    assert(weights.map(_(0)) === List("P-1", "P-EMPTY", "P-2"))
    assert(weights(0)(1) === 18.5 +- tolerance)
    assert(weights(1)(1) === 0.0 +- tolerance)
    assert(weights(2)(1) === 12.5 +- tolerance)
  }

  test("Part1: flattenItems returns all StockItems in depth-first order") {
    assert(WarehouseOps.flattenItems(depot) === List(item1, item2, item3, item4, item5))
  }

  // EXAM TESTS PART 2

  test("Part2: packIntoBatches greedily packs items in order within maxBatchWeight") {
    val batches = WarehouseOps.packIntoBatches(List(item1, item2, item3, item4), 10.0)
    assert(batches === List(List(item1, item2), List(item3), List(item4)))
  }

  test("Part2: packIntoBatches isolates an oversized item in its own batch") {
    val batches = WarehouseOps.packIntoBatches(List(item1, item5, item3, item1), 10.0)
    assert(batches === List(List(item1), List(item5), List(item3, item1)))
  }

  test("Part2: filterItems partitions warehouse items by predicate") {
    val (fragile, standard) = WarehouseOps.filterItems(depot, _.fragile)
    assert(fragile === List(item2, item4))
    assert(standard === List(item1, item3, item5))
  }

  // EXAM TESTS PART 3

  test("Part3: bestTruckAllocation pairs highest-value items with lowest-rate trucks") {
    val trucks = List(
      InsuredTruck("T-HIGH", 0.20),
      StandardTruck("T-STD"),
      InsuredTruck("T-LOW", 0.05)
    )
    val expected = Some(
      Map(
        "T-STD" -> item2,
        "T-LOW" -> item4,
        "T-HIGH" -> item3
      )
    )
    assert(WarehouseOps.bestTruckAllocation(depot, trucks) === expected)
  }

  test("Part3: bestTruckAllocation returns None when warehouse has fewer items than trucks") {
    val smallWarehouse = Warehouse("Small", List(item1))
    val trucks = List(StandardTruck("T-1"), InsuredTruck("T-2", 0.10))
    assert(WarehouseOps.bestTruckAllocation(smallWarehouse, trucks) === None)
  }

  // EXAM TESTS PART 4

  test("Part4: auditWarehouse finds top-level and nested issues sorted by severity then unitCode") {
    val expected = Vector(
      OverweightPallet("P-2", 12.5, 10.0),
      OverweightPallet("P-NEST", 12.5, 10.0),
      EmptyPallet("P-EMPTY")
    )
    assert(WarehouseOps.auditWarehouse(depot) === expected)
  }

  test("Part4: MemoryAuditSink records issues delivered by reportIssues in sorted order") {
    val sink = MemoryAuditSink()
    WarehouseOps.reportIssues(depot, sink)
    val expected = Vector(
      OverweightPallet("P-2", 12.5, 10.0),
      OverweightPallet("P-NEST", 12.5, 10.0),
      EmptyPallet("P-EMPTY")
    )
    assert(sink.recorded === expected)
  }

  test("Part4: reportIssues works polymorphically with an external AuditSink subclass") {
    class SpySink extends AuditSink:
      private var codes = Vector.empty[String]
      def record(issue: AuditIssue): Unit =
        codes = codes :+ issue.unitCode
      def recordedCodes: Vector[String] = codes

    val spy = SpySink()
    WarehouseOps.reportIssues(depot, spy)
    assert(spy.recordedCodes === Vector("P-2", "P-NEST", "P-EMPTY"))
  }
