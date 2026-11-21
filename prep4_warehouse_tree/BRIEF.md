# COMP30950 Prep Assignment 4: Recursive Warehouse Logistics & Dispatch Planner

**Level:** 4 of 5 | **Target Time:** 135–150 minutes | **Total:** 100 marks (25 + 25 + 30 + 20)

---

Attempt all four parts. Parts 1–4 are designed to be solved in order: Part 2 (`filterItems`) and Part 3 (`bestTruckAllocation`) naturally reuse `flattenItems` from Part 1, and Part 4 (`auditWarehouse`) reuses `totalWeight` from Part 1.

The bulk of the marks (~80%) are awarded for working, correct code across both normal inputs and edge cases, so ensure your code compiles and passes tests at every stage. The remaining ~20% are awarded for software quality (applied only to the proportion of correct code).

Two test suites are provided:
- `src/test/scala/InitialTests.scala` verifies the working baseline and **already passes**. Ensure it continues to pass after your changes.
- `src/test/scala/ExamTestsForStudents.scala` provides minimal acceptance tests for Parts 1–4 and clarifies exact method names, parameter orders, and return types. **Do not modify either provided test file.**

Add thorough edge-case tests in a separate test file (e.g., `src/test/scala/MyEdgeCaseTests.scala`). This catches subtle bugs and improves your software quality grade.

No further input validation is necessary beyond what is explicitly stated below; you may assume inputs are well-formed (unique unit codes and truck IDs, positive item weights and values, positive pallet `maxLoadKg`, and `InsuredTruck.rate` in `[0.0, 1.0]`).

### Hard Module Rules
- **Scala 3.3.8 braceless 2-space indentation syntax**; flat default package (`src/main/scala/` only, no `package` declarations, no new directories, no changes to `build.sbt`).
- **Pure functional core** in `WarehouseOps`: **no `var`**, **no `while`**, **no `for`-comprehensions**, **no `return`**, **no `break`/`continue`**, **no `null`**, **no thrown exceptions**, **no `.get` on `Option`**, and **no `isInstanceOf`**.
- **Exhaustive pattern matching**: do **not** use a catch-all `case _` when matching over a `sealed trait` (`StorageUnit`, `DeliveryTruck`, `AuditIssue`).
- **Do not use out-of-scope Scala 3 features** (`given`/`using`, `extension`, `opaque type`, `export`, `Either`, `Try`).

---

## Starter Code Overview

- `src/main/scala/storage.scala`: `sealed trait StorageUnit` (`code: String`), leaf case classes `StockItem(code, weightKg, valueEuro, fragile = false)` and recursive `Pallet(code, maxLoadKg, contents: List[StorageUnit])`, plus `Warehouse(name, units: List[StorageUnit])`.
- `src/main/scala/dispatch.scala`: `sealed trait DeliveryTruck` (`id: String`), with `StandardTruck(id)` and `InsuredTruck(id, rate)`.
- `src/main/scala/audit.scala`: `sealed trait AuditIssue` (`unitCode: String`, `message: String`) with `OverweightPallet` and `EmptyPallet`, plus unsealed `abstract class AuditSink` and stubs for `ConsoleAuditSink` and `MemoryAuditSink`.
- `src/main/scala/WarehouseOps.scala`: Working baseline functions (`countItems`, `allCodes`, `countFragileItems`) demonstrating the expected pure-FP house style, plus `= ???` stubs for Parts 1–4.

---

## Exercises

### 1. Recursive ADT Tree Traversals & Top-Level Semantics (25 marks)

Implement the following three functions in `WarehouseOps`:

1. `totalWeight(unit: StorageUnit): Double`
   Returns the total weight in kilograms of `unit`: for a `StockItem`, its `weightKg`; for a `Pallet`, the recursive sum of the weights of all units in its `contents` (`0.0` if empty). Implement the pallet summation using `foldLeft` rather than `map(...).sum` to avoid creating intermediate lists.

2. `topLevelPalletWeights(w: Warehouse): List[(String, Double)]`
   Returns a list of `(pallet.code, totalWeight(pallet))` pairs for **only the top-level `Pallet`s** directly in `w.units`, preserving their order in `w.units`. A top-level pallet is a `Pallet` that is not contained inside any other `Pallet`.
   - Top-level `StockItem`s in `w.units` are ignored.
   - Nested `Pallet`s inside another `Pallet` must **not** appear as separate entries in the returned list, although all `StockItem`s inside them still contribute to their enclosing top-level pallet's total weight.
   - An empty top-level `Pallet` has total weight `0.0` and is included.

3. `flattenItems(w: Warehouse): List[StockItem]`
   Returns a flat `List[StockItem]` containing every `StockItem` in `w` (whether loose at top level or nested at any depth inside pallets) in depth-first, left-to-right order. Returns `Nil` if `w` contains no `StockItem`s.

---

### 2. Explicit Structural `List` Recursion & Predicate Partitioning (25 marks)

Implement the following two functions in `WarehouseOps`:

1. `packIntoBatches(items: List[StockItem], maxBatchWeight: Double): List[List[StockItem]]`
   **Mandatory constraint:** You **must** implement this function using **explicit structural `List` recursion** with pattern matching on `Nil` and `head :: tail` (no `var`, no loops, no mutable collections, no list indexing).

   Rules:
   - If `items` is empty or `maxBatchWeight <= 0.0`, return `Nil`.
   - Process `items` strictly in their given order, greedily adding consecutive items to the current batch as long as the batch's total weight remains `<= maxBatchWeight`.
   - When adding the next item would cause the current batch's weight to exceed `maxBatchWeight`, close the current batch and start a new batch.
   - **Oversized single item:** If a single item alone has `item.weightKg > maxBatchWeight`, close any open non-empty batch before it, place that item alone in its own 1-element batch `List(item)`, and start fresh for any remaining items.
   - Preserve the original item order both across batches and within each batch.

2. `filterItems(w: Warehouse, pred: StockItem => Boolean): (List[StockItem], List[StockItem])`
   Partitions all `StockItem`s in `w` (in `flattenItems(w)` order) according to `pred`, returning a pair `(matching, nonMatching)` where the first list contains all items for which `pred` returns `true` and the second contains all items for which `pred` returns `false`.

---

### 3. Greedy Dispatch Allocation (30 marks)

Implement the following function in `WarehouseOps`:

```scala
bestTruckAllocation(w: Warehouse, trucks: List[DeliveryTruck]): Option[Map[String, StockItem]]
```

Each truck in `trucks` must be assigned **exactly one distinct `StockItem`** from `w`. Treat all `StockItem`s inside pallets (at any depth) as independent items and ignore pallets themselves when allocating trucks.

- Return `Some(allocation)` mapping each truck's `id` (`String`) to its assigned `StockItem` such that total net dispatch value is maximised (see **Note 1** and **Note 2** below).
- Return `None` if `w` contains fewer `StockItem`s than `trucks.length`.
- If `trucks` is empty (`Nil`), return `Some(Map.empty)`.

---

### 4. Warehouse Audit, Multi-Key Stable Sorting & Open Polymorphic Sinks (20 marks)

1. Implement `auditWarehouse(w: Warehouse): Vector[AuditIssue]` in `WarehouseOps`:
   Recursively inspect **every `Pallet`** in `w` — **both top-level and nested at every depth**:
   - If a pallet has `contents.isEmpty`, record `EmptyPallet(pallet.code)`.
   - If a pallet has `totalWeight(pallet) > pallet.maxLoadKg` (strictly greater; a pallet loaded to exactly `maxLoadKg` is **not** overweight), record `OverweightPallet(pallet.code, totalWeight(pallet), pallet.maxLoadKg)`.

   Return all issues as a `Vector[AuditIssue]` sorted deterministically by `(severityRank, unitCode)` (see **Note 3** below), where:
   - `OverweightPallet` has rank `0`
   - `EmptyPallet` has rank `1`

   **Mandatory constraint:** Determine severity rank using exhaustive pattern matching on `AuditIssue` without `case _`.

2. Complete the concrete sink classes in `src/main/scala/audit.scala` (keep `AuditSink` unsealed so external code can define custom subclasses):
   - `ConsoleAuditSink.record(issue: AuditIssue): Unit` prints `issue.message` using `println`.
   - `MemoryAuditSink` stores each recorded `AuditIssue` in arrival order inside a `private` field and exposes the recorded issues as an immutable `Vector[AuditIssue]` via `recorded`.

3. Implement `reportIssues(w: Warehouse, sink: AuditSink): Unit` in `WarehouseOps`:
   Computes `auditWarehouse(w)` and delivers every resulting issue (in its sorted order) to `sink.record(issue)`.

---

## Notes

**(1) Greedy Optimality for Part 3:**
Each dispatched `(truck, item)` pair yields a net dispatch value of `item.valueEuro * (1.0 - effectiveRate)`, where a `StandardTruck` has an effective insurance rate of `0.0` and an `InsuredTruck` has an effective rate equal to its `rate` (in `[0.0, 1.0]`). Total net dispatch value is maximised by assigning the highest-value item to the truck with the lowest effective rate, the next highest-value item to the truck with the next lowest effective rate, and so on (using the highest-value items when there are more items than trucks). In the case of ties (equal item values or equal truck rates), any optimal allocation is acceptable.

**(2) Building a `Map` with `zip`:**
To construct a `Map` from two aligned lists of equal length (or where the second list is at least as long as the first):
```scala
val keys = List("T-1", "T-2")
val values = List(itemA, itemB, itemC)
val allocation = keys.zip(values).toMap
// Map("T-1" -> itemA, "T-2" -> itemB)
```

**(3) Multi-Key Stable Sorting & Matching vs Polymorphism:**
Tuple sort keys in `sortBy` compare lexicographically and preserve original order on ties:
```scala
issues.sortBy(issue => (severityRank(issue), issue.unitCode))
```
Notice why `StorageUnit`, `DeliveryTruck`, and `AuditIssue` are `sealed trait`s (closed data families with exhaustive pattern matching), whereas `AuditSink` is an unsealed `abstract class` (open for new consumer implementations in other files via subtype polymorphism).

---

## Post-Completion Self-Check (`SoftwareQuality.pdf`)

Before calling your work finished, run `sbt test` and review your code against the COMP30950 software quality rubric:
- [ ] **Zero compiler warnings** and both `InitialTests` and `ExamTestsForStudents` pass 100%.
- [ ] **SRP & Short Methods:** Complex traversals/helpers are decomposed cleanly (nested private recursive helpers inside `WarehouseOps`; `effectiveRate` and `severityRank` extracted as focused private helpers).
- [ ] **No Duplication (DRY):** `filterItems` and `bestTruckAllocation` reuse `flattenItems`; `topLevelPalletWeights` and `auditWarehouse` reuse `totalWeight`; `reportIssues` reuses `auditWarehouse`.
- [ ] **Minimal Visibility & Scope:** Helper functions in `WarehouseOps` and backing state in `MemoryAuditSink` are `private`; zero `var`s anywhere in `WarehouseOps`, `storage.scala`, or `dispatch.scala`.
- [ ] **Exhaustive Matching:** Zero `case _` wildcards on `StorageUnit`, `DeliveryTruck`, or `AuditIssue`.
- [ ] **Own Edge-Case Tests (`MyEdgeCaseTests.scala`):** Covers empty warehouse, 3-level nested pallets, empty pallet inside a non-empty pallet, `packIntoBatches` on `Nil` / `maxBatchWeight <= 0` / exact-limit sums / consecutive oversized items, `bestTruckAllocation` on empty `trucks` / exact counts / ties, and `auditWarehouse` on exact `maxLoadKg` boundary / simultaneously overweight outer + inner pallets / healthy warehouse.
