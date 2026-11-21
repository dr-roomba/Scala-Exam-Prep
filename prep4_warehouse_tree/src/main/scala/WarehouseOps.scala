/*
Note to students: The baseline functions below illustrate the idiomatic pure-FP
style expected in this codebase: nested recursive helpers, foldLeft to avoid
unnecessary intermediate lists, and exhaustive pattern matching over sealed ADTs.
*/

object WarehouseOps:
  def countItems(w: Warehouse): Int =
    def countIn(units: List[StorageUnit]): Int =
      units.foldLeft(0): (acc, unit) =>
        acc + (unit match
          case _: StockItem => 1
          case Pallet(_, _, contents) => countIn(contents)
        )
    countIn(w.units)

  def allCodes(w: Warehouse): List[String] =
    def collect(units: List[StorageUnit]): List[String] =
      units.flatMap:
        case item: StockItem => List(item.code)
        case Pallet(code, _, contents) => code :: collect(contents)
    collect(w.units)

  def countFragileItems(w: Warehouse): Int =
    def countIn(units: List[StorageUnit]): Int =
      units.foldLeft(0): (acc, unit) =>
        acc + (unit match
          case s: StockItem if s.fragile => 1
          case _: StockItem => 0
          case Pallet(_, _, contents) => countIn(contents)
        )
    countIn(w.units)

  // Part 1
  def totalWeight(unit: StorageUnit): Double = ???

  def topLevelPalletWeights(w: Warehouse): List[(String, Double)] = ???

  def flattenItems(w: Warehouse): List[StockItem] = ???

  // Part 2
  def packIntoBatches(
    items: List[StockItem],
    maxBatchWeight: Double
  ): List[List[StockItem]] = ???

  def filterItems(
    w: Warehouse,
    pred: StockItem => Boolean
  ): (List[StockItem], List[StockItem]) = ???

  // Part 3
  def bestTruckAllocation(
    w: Warehouse,
    trucks: List[DeliveryTruck]
  ): Option[Map[String, StockItem]] = ???

  // Part 4
  def auditWarehouse(w: Warehouse): Vector[AuditIssue] = ???

  def reportIssues(w: Warehouse, sink: AuditSink): Unit = ???
