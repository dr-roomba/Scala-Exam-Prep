# COMP30950 Multi-Paradigm Programming — Prep Assignment 2

**Smart Parcel Locker Network**  
Level 2 of 5 | Suggested Time: **90 minutes** | Total: **100 marks** (`20 + 25 + 30 + 25`)

---

## Context & General Instructions

You are building the software for a **Smart Parcel Locker Network**. Parcels carry validated tracking codes and physical dimensions, locker stations hold typed compartments (`Small`, `Medium`, `Large`) that store parcels immutably, and a thin stateful manager coordinates multiple stations across a network.

This assignment practises **Functional Core, Imperative Shell**:
- `ParcelCode`, `Parcel`, `LockerSize`, `Compartment`, `StationStats`, and `LockerStation` form a **pure, immutable functional core** (`parcel.scala`, `locker.scala`, `LockerStation.scala`).
- `LockerNetworkManager` (`LockerNetworkManager.scala`) is the **thin stateful imperative shell**, holding the sole `private var` in the entire system and delegating all domain decisions to `LockerStation`.

Please read these rules carefully before starting:
1. **Sequential Parts**: Parts 1–4 build on one another. Essential API details not repeated in prose are specified unambiguously in `src/test/scala/ExamTestsForStudents.scala` — read that file alongside this brief.
2. **Do Not Modify Provided Tests or Build Config**: Leave `build.sbt`, `InitialTests.scala`, and `ExamTestsForStudents.scala` untouched. Ensure all tests in `InitialTests.scala` continue to pass. Write your own thorough edge-case tests in a separate file (e.g. `src/test/scala/MyEdgeCaseTests.scala`).
3. **Working Code & Quality Marks**: Roughly **80%** of marks are for correctness under automated testing (including unseen edge-case tests) and **20%** for software quality (`SoftwareQuality.pdf`). Quality marks apply *only* to the proportion of correct code — get each part compiling and passing before refactoring.
4. **Hard Module Constraints**:
   - Use **Scala 3.3.8 braceless 2-space indentation syntax** (braces `{ ... }` only for multi-line lambdas, partial-function tuple matching `{ case (a, b) => ... }`, and ScalaTest `test("...") { ... }`).
   - **No `var`s or mutable collections** anywhere in the functional core (only the single `private var stations` in `LockerNetworkManager` is permitted).
   - **No `for`-comprehensions**, no `for`/`while` loops, no `return`, no `break`/`continue`.
   - **No `null`**, no thrown exceptions for domain/parsing failures, and **no `.get` on `Option`**.
   - **No new directories or dependencies**; keep all source files in the default (unnamed) package in `src/main/scala/`.

---

## Provided Baseline

The starter repository compiles out of the box (`InitialTests` pass; `ExamTestsForStudents` fail on `???`):

- `enum LockerSize` (`Small`, `Medium`, `Large`) with capacities:
  - `Small`: max `2.0` kg, max `5000.0` cm³
  - `Medium`: max `10.0` kg, max `25000.0` cm³
  - `Large`: max `25.0` kg, max `80000.0` cm³
  - Helper `canHold(weightKg: Double, volumeCm3: Double): Boolean` (inclusive `<=` on both dimensions).
- `case class Compartment(id: Int, size: LockerSize, occupant: Option[Parcel] = None)` with `isFree` and `canFit(parcel: Parcel)`.
- `case class StationStats(totalCompartments: Int, occupiedCount: Int, totalWeightKg: Double, heaviestCode: Option[String])`.
- `case class LockerStation(stationId: String, compartments: Vector[Compartment])` with working `totalCompartments`, `freeCount`, `compartmentWithId(id: Int)`, and companion helpers `LockerStation.empty(stationId)` and `LockerStation.withSizes(stationId, sizes)`.
- `class LockerNetworkManager(initialStations: Vector[LockerStation] = Vector.empty)` with `private var stations`, `stationCount`, and companion `apply`.

---

## Part 1 — Smart Constructors & CSV `Option` Parsing (20 marks)

Complete `ParcelCode` and `Parcel` in `src/main/scala/parcel.scala`. Both case classes already have a `private` primary constructor and a `private def copy` so invalid instances cannot be created or copied from outside.

1. **`ParcelCode.apply(raw: String): Option[ParcelCode]`**
   - Trims leading and trailing whitespace from `raw` and converts it to uppercase.
   - Returns `Some(new ParcelCode(normalised))` if and only if the trimmed string has length between **4 and 12 (inclusive)** and every character is alphanumeric (`_.isLetterOrDigit` — no spaces, hyphens, or symbols).
   - Otherwise returns `None`.
2. **`Parcel.apply(code: ParcelCode, weightKg: Double, volumeCm3: Double): Option[Parcel]`**
   - Returns `Some(new Parcel(code, weightKg, volumeCm3))` if and only if both `weightKg` and `volumeCm3` are **finite** (`isFinite`) and **strictly positive** (`> 0.0`).
   - Rejects `0.0`, negative values, `Double.NaN`, `Double.PositiveInfinity`, and `Double.NegativeInfinity` with `None`.
3. **`Parcel.parse(csvLine: String): Option[Parcel]`**
   - Parses a single CSV line of the form `"CODE,weightKg,volumeCm3"` containing **exactly three** comma-separated fields (leading/trailing whitespace around each field is trimmed).
   - Composes `ParcelCode.apply`, `String.toDoubleOption`, and `Parcel.apply` using `flatMap` — **no exceptions and no `.get`**. Returns `None` for any malformed or invalid line.

---

## Part 2 — Immutable Core State Transitions (25 marks)

Complete the two state-transition methods on `LockerStation` in `src/main/scala/LockerStation.scala`:

1. **`deposit(parcel: Parcel): Option[LockerStation]`**
   - Returns `None` if a parcel with the same `code` is **already stored** anywhere in this station, or if no free compartment can fit `parcel`.
   - Otherwise selects the **smallest fitting free compartment** (minimum `size.maxVolumeCm3`), breaking ties by **lowest `compartment.id`**.
   - Places `parcel` into that compartment and returns `Some(updatedStation)`, preserving the original order of `compartments` and leaving `this` unchanged.
2. **`collect(code: ParcelCode): Option[(Parcel, LockerStation)]`**
   - Returns `None` if no compartment currently stores a parcel with `code`.
   - Otherwise removes the parcel (resetting that compartment's `occupant` to `None`) and returns `Some((parcel, updatedStation))`, preserving the original order of `compartments` and leaving `this` unchanged.

---

## Part 3 — Single-Pass `foldLeft` Analytics & Batch Folding (30 marks)

Complete the two analytical/batch methods on `LockerStation` in `src/main/scala/LockerStation.scala`:

1. **`stats: StationStats`**
   - **Mandatory Constraint**: Compute all four fields of `StationStats` in a **single pass** over `compartments` using `foldLeft` (do not run multiple separate passes such as `.count` + `.sum` + `.maxByOption`).
   - `totalCompartments`: total number of compartments in the station (`0` if empty).
   - `occupiedCount`: number of occupied compartments (`0` if none).
   - `totalWeightKg`: sum of `weightKg` of all currently stored parcels (`0.0` if none).
   - `heaviestCode`: `Some(parcel.code.value)` for the heaviest stored parcel, or `None` if no parcel is stored. If multiple stored parcels tie for the maximum weight, choose the one in the compartment with the **lowest `compartment.id`** (regardless of vector order).
2. **`depositBatch(csvLines: List[String]): (LockerStation, Int)`**
   - Processes `csvLines` in list order using `foldLeft`, starting from `(this, 0)`.
   - For each line, parses it with `Parcel.parse` and attempts `deposit` against the current station state.
   - If parsing and depositing both succeed, advances to the updated station and increments the count; otherwise skips the line and leaves the station and count unchanged.
   - Returns `(finalStation, depositedCount)`.

---

## Part 4 — Imperative Shell Facade & `apply` Lookup (25 marks)

Complete `LockerNetworkManager` in `src/main/scala/LockerNetworkManager.scala`. Keep the manager thin: delegate all parcel/compartment logic to `LockerStation` (**Tell, Don't Ask**) and avoid duplicating update boilerplate.

1. **`apply(stationId: String): Option[LockerStation]`**
   - Instance `apply` lookup (e.g. `manager("DUB-01")`), returning `Some(station)` if registered or `None` otherwise.
2. **`addStation(station: LockerStation): Boolean`**
   - Registers `station` under `station.stationId` and returns `true`. If a station with that `stationId` is already registered, leaves the network unchanged and returns `false`.
3. **`depositAt(stationId: String, parcel: Parcel): Boolean`**
   - Delegates to `LockerStation.deposit` for the target station. If the station exists and accepts the parcel, updates `stations` and returns `true`; otherwise leaves state unchanged and returns `false`.
   - Use a private helper method (e.g. `private def updateStation(...)`) to keep state updates clean and DRY.
4. **`collectFrom(stationId: String, code: ParcelCode): Option[Parcel]`**
   - Delegates to `LockerStation.collect` for the target station. If found, updates `stations` with the freed compartment and returns `Some(parcel)`; otherwise leaves state unchanged and returns `None`.
5. **`locateParcel(code: ParcelCode): Option[String]`**
   - Returns `Some(stationId)` for the station currently storing `code`, or `None` if no registered station holds it (in normal operation a parcel resides in at most one station).

---

## Notes

1. `String.split(",", -1)` retains trailing empty fields (so `"AB12,1.0,100,"` splits into 4 parts and is rejected), whereas `split(",")` drops trailing empty strings.
2. `Double.isFinite` checks that a number is neither `NaN` nor infinite; `String.toDoubleOption` parses a `Double` safely into an `Option[Double]`.
3. You may add small helper methods (e.g. `contains(code: ParcelCode): Boolean` on `LockerStation` or `Compartment`) to keep code readable and uphold *Tell, Don't Ask* — never reach through `station.compartments` from `LockerNetworkManager`.
4. Floating-point results are tested with a tolerance of `+/- 0.001`.
5. `LockerNetworkManager` is an instantiable class (with companion `apply`) rather than a global singleton `object`, so tests stay isolated without needing a global `clear()` call.

---

## Post-Completion Quality Self-Check (`SoftwareQuality.pdf`)

Before considering your solution finished, review every file against Mel's 20% quality rubric:
- [ ] **Functional Core / Imperative Shell**: Zero `var`s or mutable collections outside `LockerNetworkManager.stations`.
- [ ] **Smart Constructors**: Neither `new ParcelCode(...)`, `code.copy(...)`, `new Parcel(...)`, nor `parcel.copy(...)` can bypass validation from outside `parcel.scala`.
- [ ] **No Forbidden Constructs**: Zero `for`-comprehensions, `return`, `null`, exceptions, or `.get` on `Option`.
- [ ] **Single-Pass Constraint**: `LockerStation.stats` performs exactly one `foldLeft` traversal of `compartments`.
- [ ] **Tell, Don't Ask & DRY**: `LockerNetworkManager` delegates all parcel/station logic to `LockerStation` and factors repeated state updates into a clean private helper.
- [ ] **Short, Clear Methods & Minimal Visibility**: Helper methods are `private` where appropriate, concise (~3–10 lines), self-explanatory without noisy comments, and formatted in clean 2-space Scala 3 braceless style.
- [ ] **Edge-Case Test Suite**: You added `src/test/scala/MyEdgeCaseTests.scala` covering boundaries (`4`/`12` chars, exact capacity fit, `0.0`/`NaN`/`Infinity`, trailing CSV comma, out-of-order compartment IDs, tie-breaking, duplicate codes within a batch, empty/full stations, unknown station IDs) and `sbt test` passes completely.
