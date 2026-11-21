# COMP30950 Multi-Paradigm Programming — Prep Assignment 1
## Hiking Trail Network & Elevation Analytics

**Level:** 1 of 5 (Foundations & Functional Collection Pipelines)  
**Target Time:** 60–75 minutes | **Total Marks:** 100 (25 + 25 + 25 + 25) | **Scala:** 3.3.8

---

### Preamble & Rules

This assignment tests core Scala 3 expressions, `enum`, basic `case class` design, immutable collections (`Vector`, `Map`, `Tuple`), `Option`, and higher-order function (HOF) pipelines.

- **Dependencies between parts:** Parts 2 and 4 rely on `Trail.totalDistance` and `Trail.totalElevationGain` from Part 1. Part 3 is independent of Parts 1 and 2.
- **Working code first:** Ensure your project compiles cleanly and passes `sbt test` at all times. An incomplete working solution earns partial marks; code that does not compile earns zero.
- **Marks & Quality:** As in the practical exam, grading is ~80% correctness and ~20% software quality, and quality marks apply only to correct code.
- **Provided tests:** `InitialTests.scala` passes out of the box and must continue to pass. `ExamTestsForStudents.scala` compiles immediately and fails only on the `???` stubs; it defines the exact public API contract (names, parameter order, return types, formatting). **Do not modify `InitialTests.scala`, `ExamTestsForStudents.scala`, or `build.sbt`, and do not add new directories.**
- **Implementation Constraints (COMP30950 rules):**
  - Use **Scala 3 braceless 2-space indentation syntax** (curly braces `{ ... }` only for multi-line lambdas, partial-function tuple patterns `{ case (a, b) => ... }`, and `test("...") { ... }`).
  - **Do NOT use:** `var`, mutable collections, `for`-comprehensions, `while` loops, `return`, `break`/`continue`, `null`, thrown exceptions, `.get` on `Option`, or out-of-scope Scala 3 features (`given`/`using`, `extension`, `opaque type`, `export`, `Either`, `Try`).
  - Keep all source files in the **default (unnamed) package** directly inside `src/main/scala/`.

---

### Starter Code Overview

The baseline domain model is provided across three files in `src/main/scala/`:

- `GeoPoint.scala`: `case class GeoPoint(x: Double, y: Double, elevation: Double)` with `horizontalDistanceTo(other: GeoPoint): Double` and `elevationChangeTo(other: GeoPoint): Double`.  
  **Units:** planar coordinates `x` and `y` are in **kilometres (`km`)**; `elevation` is height above sea level in **metres (`m`)**.
- `trail.scala`: `case class Waypoint(name: String, location: GeoPoint)` (with `distanceTo`), `enum Difficulty` (`Easy`, `Moderate`, `Hard`), and `case class Trail(id: String, name: String, difficulty: Difficulty, waypoints: Vector[Waypoint])` with `waypointCount`, `isEmpty`, `startWaypoint`, and `endWaypoint`.
- `TrailGuide.scala`: `case class TrailGuide(trails: Vector[Trail])` with `trailCount`, `isEmpty`, `trailById`, `addTrail`, and companion `TrailGuide.empty`.

Implement the eight `= ???` methods below without breaking any existing baseline behavior.

---

### Part 1: Trail Distance & Elevation Gain (25 marks)

Implement the following two methods on `Trail` (in `trail.scala`):

1. **`totalDistance: Double`**  
   Computes the total horizontal length of the trail (in `km`) by summing `horizontalDistanceTo` between every consecutive pair of waypoints `(waypoints(i), waypoints(i + 1))`.  
   - Ignore elevation when calculating horizontal distance.
   - Return `0.0` if the trail has `0` or `1` waypoints.

2. **`totalElevationGain: Double`**  
   Computes the cumulative positive elevation climbed along the trail (in `m`) by summing `next.location.elevation - prev.location.elevation` over all consecutive pairs of waypoints where that difference is **strictly positive (`> 0.0`)**.  
   - Flat (`== 0.0`) and downhill (`< 0.0`) segments contribute `0.0` and do **not** reduce total elevation gain.
   - Return `0.0` if the trail has `0` or `1` waypoints.

---

### Part 2: Formatted Summary & Difficulty Counts (25 marks)

Implement the following two methods on `TrailGuide` (in `TrailGuide.scala`):

1. **`formattedSummary: String`**  
   Returns a multi-line summary of all trails sorted alphabetically by `name` (preserving original order on equal names). Each trail is formatted on its own line as:
   ```text
   <name> (<Difficulty>): <dist>km, +<gain>m
   ```
   where `<dist>` is `totalDistance` formatted to **2 decimal places** and `<gain>` is `totalElevationGain` formatted to **1 decimal place** (for example, `"Amber Valley Stroll (Easy): 2.50km, +35.5m"`).  
   - Join lines with `"\n"` (no trailing newline).
   - Return `""` if `trails` is empty.

2. **`countByDifficulty: Map[Difficulty, Int]`**  
   Returns a `Map` recording how many trails in `trails` have each `Difficulty`.  
   - **All three** `Difficulty` values (`Difficulty.Easy`, `Difficulty.Moderate`, `Difficulty.Hard`) **must** appear as keys in the returned `Map`, mapped to `0` when no trails have that difficulty.

---

### Part 3: Network-Wide Waypoint Frequency & Steepest Climb (25 marks)

Implement the following two methods on `TrailGuide` (in `TrailGuide.scala`):

1. **`mostCommonWaypoint: Option[(String, Int)]`**  
   Determines which waypoint `name` appears on the largest number of trails across the guide, returning `Some((waypointName, trailCount))`.  
   - **Distinct per trail:** Count how many **trails** contain each waypoint name. If a loop trail visits the same waypoint name multiple times, that trail contributes only **`1`** to that waypoint name's count.
   - Return `None` if there are no trails or no waypoints on any trail.
   - If multiple waypoint names tie for the maximum count, returning any tied `(waypointName, trailCount)` is acceptable.

2. **`steepestSegment: Option[(String, Double)]`**  
   Across all trails in the guide, inspects every consecutive pair of waypoints `(prev, next)`. Let `dist = prev.location.horizontalDistanceTo(next.location)` (in `km`) and `climb = next.location.elevation - prev.location.elevation` (in `m`).  
   - Consider **only** climbing segments with strictly positive horizontal distance and strictly positive climb (`dist > 0.0` and `climb > 0.0`). Its gradient is `climb / dist` (in `m/km`).
   - Ignore flat segments (`climb == 0.0`), downhill segments (`climb < 0.0`), and zero-horizontal-distance segments (`dist == 0.0`).
   - Return `Some((trail.name, maxGradient))` for the segment with the greatest gradient across all trails, or `None` if no qualifying segment exists.
   - If multiple segments tie for the maximum gradient, returning any tied `(trail.name, maxGradient)` is acceptable.

---

### Part 4: Nearest Waypoint & Length Classification (25 marks)

Implement the following two methods on `TrailGuide` (in `TrailGuide.scala`):

1. **`nearestWaypoint(from: GeoPoint, requiredDifficulty: Difficulty): Option[(String, Double)]`**  
   Searches all waypoints belonging to trails whose `difficulty == requiredDifficulty` and returns `Some((waypoint.name, horizontalDistance))` for the waypoint closest to `from` by horizontal distance (`from.horizontalDistanceTo(waypoint.location)`).  
   - Ignore `elevation` when measuring distance.
   - Return `None` if no trails have `requiredDifficulty`, or if all matching trails have no waypoints.
   - If multiple waypoints tie for the minimum horizontal distance, returning any tied `(waypoint.name, distance)` is acceptable.

2. **`classifyByLength(minKm: Double): (Vector[Trail], Vector[Trail])`**  
   Partitions `trails` into `(longTrails, shortTrails)`:
   - `longTrails` (tuple element `0`): all trails with `totalDistance >= minKm` (inclusive boundary).
   - `shortTrails` (tuple element `1`): all trails with `totalDistance < minKm`.
   - Preserve the original relative order of trails in both vectors.

---

### Notes

1. `import scala.math.*` gives access to standard math functions (`sqrt`, `pow`, etc.), though `GeoPoint.horizontalDistanceTo` is already implemented for you.
2. To form consecutive pairs safely from any `Vector`, use `xs.zip(xs.drop(1))` — **never** `xs.zip(xs.tail)`, which crashes on an empty collection. Always test sequence logic on **0, 1, and 2 elements**.
3. Use Scala's `f` string interpolator for fixed-decimal formatting, e.g. `f"${t.totalDistance}%.2f"` and `f"${t.totalElevationGain}%.1f"`.
4. `Difficulty.values` returns an `Array` of all cases in the `Difficulty` enum.
5. Prefer safe `Option`-returning collection methods such as `minByOption` and `maxByOption` (which return `None` on empty collections) over `minBy`/`maxBy` (which throw exceptions).
6. Scala 3 tuples are 0-indexed (`pair(0)`, `pair(1)`), and tuple pairs can be destructured in lambdas using `{ case (a, b) => ... }` or `(a, b) => ...`.
7. **Write your own edge-case tests** in a separate test file (e.g., `src/test/scala/MyEdgeCaseTests.scala`). `ExamTestsForStudents.scala` provides minimal acceptance tests only — make sure you test 0-waypoint and 1-waypoint trails, all-downhill/flat trails, zero-horizontal-distance segments, empty `TrailGuide`, ties, and exact threshold boundaries!

---

### Post-Completion `SoftwareQuality.pdf` Self-Check Rubric

Before finishing, reread your solution carefully and check every item:

- [ ] **Craft & Simplify (KISS):** Reread every method once tests pass — can any pipeline or expression be made clearer or shorter without losing readability?
- [ ] **Single Responsibility & Short Methods:** Does each method do one clear job? Extract small `private` helper methods (e.g., `private def consecutiveWaypoints` on `Trail`) rather than repeating `waypoints.zip(waypoints.drop(1))` (DRY!).
- [ ] **Tell, Don't Ask & Distribution of Functionality:** Does segment-level logic live on `Trail`/`GeoPoint`/`Waypoint` rather than having `TrailGuide` repeatedly dig into raw `.location.elevation` fields?
- [ ] **Naming & Visibility:** Are helper names intention-revealing and marked `private` so the public API stays minimal?
- [ ] **Minimal Comments:** Remove any comments that restate *what* the code does; replace commented sub-blocks with well-named `private` helpers.
- [ ] **Layout & Hard Rules:** Clean 2-space Scala 3 braceless indentation throughout; zero `var`s, zero `for`-comprehensions, zero `return`s, zero `.get` on `Option`, zero `.tail` on possibly empty sequences.
- [ ] **Verification:** Both `InitialTests.scala`, `ExamTestsForStudents.scala`, and your own `MyEdgeCaseTests.scala` pass cleanly under `sbt test`.
