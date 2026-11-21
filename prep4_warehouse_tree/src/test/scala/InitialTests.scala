import org.scalatest.funsuite.AnyFunSuite

class InitialTests extends AnyFunSuite:
  val sampleWarehouse = Warehouse(
    "Baseline-Depot",
    List(
      StockItem("S-101", 4.5, 120.0),
      Pallet(
        "P-A",
        50.0,
        List(
          StockItem("S-102", 8.0, 250.0, fragile = true),
          Pallet(
            "P-A1",
            20.0,
            List(
              StockItem("S-103", 3.5, 80.0),
              StockItem("S-104", 6.0, 310.0, fragile = true)
            )
          )
        )
      ),
      Pallet("P-B", 15.0, Nil)
    )
  )

  test("Initial: countItems counts loose and nested stock items") {
    assert(WarehouseOps.countItems(sampleWarehouse) === 4)
  }

  test("Initial: countItems returns 0 for an empty warehouse") {
    assert(WarehouseOps.countItems(Warehouse("Empty", Nil)) === 0)
  }

  test("Initial: allCodes returns all unit codes in depth-first pre-order") {
    assert(
      WarehouseOps.allCodes(sampleWarehouse) ===
        List("S-101", "P-A", "S-102", "P-A1", "S-103", "S-104", "P-B")
    )
  }

  test("Initial: countFragileItems counts only fragile stock items across tree") {
    assert(WarehouseOps.countFragileItems(sampleWarehouse) === 2)
  }

  test("Initial: AuditIssue messages format OverweightPallet and EmptyPallet") {
    assert(
      OverweightPallet("P-9", 42.5, 30.0).message ===
        "Pallet P-9 overweight: 42.50 kg exceeds 30.00 kg limit"
    )
    assert(EmptyPallet("P-0").message === "Pallet P-0 is empty")
  }

  test("Initial: StockItem defaults fragile to false and supports structural equality") {
    val defaultItem = StockItem("S-1", 2.0, 50.0)
    val explicitItem = StockItem("S-1", 2.0, 50.0, fragile = false)
    assert(defaultItem === explicitItem)
    assert(!defaultItem.fragile)
  }
