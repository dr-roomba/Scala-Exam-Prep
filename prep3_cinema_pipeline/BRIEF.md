# COMP30950 Multi-Paradigm Programming — Exam Prep 3
## Cinema Ticketing, Pricing & Screening Workflows

**Level:** 3 of 5 | **Target Time:** 105–120 minutes | **Total Marks:** 100 (20 + 25 + 30 + 25)

---

### Preamble & Rules

This assignment builds on Prep 1–2 (immutability, `Option`, HOF pipelines, companion/singleton objects, encapsulation) and focuses on **inheritance, subtype polymorphism, the Template Method pattern, traits & mixins (`Ordered[T]`), the singleton Null Object pattern, functional Strategy, and closures/callbacks**.

- **Part Dependencies:** Parts 1, 2, and 3 are independent of one another. Part 4 builds on the checkout classes from Part 2.
- **Working Code First:** Approximately 80% of marks are awarded for correctness and 20% for software quality. The quality grade applies **only** to working, correct code — make sure `sbt test` compiles and passes before polishing.
- **API Contract:** Essential details omitted from this brief (exact method names, parameter order, return types, and formatting) are specified unambiguously in `src/test/scala/ExamTestsForStudents.scala`. Read both test files alongside this brief.
- **Do Not Modify Provided Files:** Do **not** alter `build.sbt`, `InitialTests.scala`, or `ExamTestsForStudents.scala`. Ensure `InitialTests.scala` continues to pass throughout. Place your own edge-case tests in a separate file (e.g., `src/test/scala/MyEdgeCaseTests.scala`).
- **COMP30950 Constraints:**
  - Use **Scala 3.3.8 braceless 2-space indentation syntax** and keep all source files in the **default (unnamed) package** directly inside `src/main/scala/`.
  - **Do NOT use:** `for`-comprehensions, `return`, `break`/`continue`, `null`, thrown exceptions for domain errors, `.get` on `Option`, `var` in case classes, or `isInstanceOf` / type-tag branching.
  - Stay strictly within module scope (no `given`/`using`, `extension`, `opaque`, `export`, `Either`, `Try`, variance annotations, or stackable `abstract override` traits).

### Starter Repository Overview

The starter repository compiles out of the box (`InitialTests` pass; `ExamTestsForStudents` fail on `???`):

- `bookingLogger.scala` — `trait BookingLogger`, working `MemoryLogger`, and Part 1 stub `object SilentLogger`.
- `orders.scala` — working `case class TicketOrder` (with `subtotal`) and `case class Receipt`.
- `screening.scala` — `trait Captioned`, `trait PremiumAudio`, `sealed trait Screening extends Ordered[Screening]`, working `StandardScreening`, and `ImaxScreening`.
- `ScreeningSchedule.scala` — working `count`, `findById`, `sortedScreenings`, and Part 3 stub `filterScreenings`.
- `checkout.scala` — `sealed abstract class CheckoutProcess` and subclasses `StandardCheckout`, `MemberCheckout`, and `VipCheckout`.
- `PricingRules.scala` — `object PricingRules` with `type PricingRule` and Part 3 strategy stubs.
- `BoxOffice.scala` — `class BoxOffice` with `processSingle` and Part 4 stub `processBatch`.
- `BatchAuditor.scala` — `class BatchAuditor` with Part 4 stub `audit`.

---

### Part 1 — Traits, `Ordered[Screening]` & Singleton Null Object (20 marks)

1. **`Screening.compare(that: Screening): Int`** (`screening.scala`):
   - `Screening` already mixes in `Ordered[Screening]`. Implement `compare` so screenings are ordered **ascending by `startMinutes`**, with ties broken **alphabetically (lexicographically) by `title`**.
   - Implementing `compare` automatically enables `schedule.sortedScreenings`, `screenings.sorted`, `<`, `>`, `<=`, and `>=` across all `Screening` subtypes.
2. **Capability Traits & Mixin Composition** (`screening.scala`):
   - On `trait Captioned`, implement `def captionTag: String` returning `s"CC:$subtitleLanguage"`.
   - On `trait PremiumAudio`, implement `def audioTag: String` returning `s"AUDIO:$audioFormat"`.
   - On `case class ImaxScreening` (which mixes in `Screening, Captioned, PremiumAudio` using Scala 3 comma-separated syntax), implement `def featureSummary: String` returning:
     `s"$title [$formattedStartTime] ($captionTag, $audioTag)"`
     *(e.g., `"Interstellar [20:00] (CC:EN, AUDIO:Dolby Atmos)"`)*.
3. **Singleton Null Object `SilentLogger`** (`bookingLogger.scala`):
   - Implement `SilentLogger.log(message: String): Unit` as a harmless no-op (`()`), enabling `SilentLogger` to act as the default `BookingLogger` without `Option` or `null` checks.

---

### Part 2 — Checkout Workflows: Mandatory Template Method Pattern (25 marks)

Complete the sealed checkout hierarchy in `checkout.scala`:

1. **`CheckoutProcess`** (`sealed abstract class CheckoutProcess(val logger: BookingLogger = SilentLogger)`):
   - `final def isValid(order: TicketOrder): Boolean`: returns `true` iff `order.screeningId` is non-blank (`!order.screeningId.isBlank`), `order.seats > 0`, and `order.baseUnitPrice` is finite and strictly positive (`order.baseUnitPrice.isFinite && order.baseUnitPrice > 0.0`).
   - `final def checkout(order: TicketOrder): Option[Receipt]` (**Template Method**):
     1. If `!isValid(order)`, return `None` immediately **without** calling `logger.log`.
     2. Compute `val subtotal = order.subtotal`.
     3. Compute `val discounted = applyDiscount(order, subtotal)` — note that `applyDiscount` returns the **price after discount**, NOT the discount amount subtracted (e.g., 15% off `100.0` returns `85.0`).
     4. Add the optional hook fee: `val finalTotal = math.max(0.0, discounted + extraFee(order))`.
     5. Build `val receipt = Receipt(order.screeningId, order.seats, finalTotal, receiptLabel)`.
     6. Log the receipt via `logger.log` in the exact format:
        `f"[$receiptLabel] ${order.screeningId} x${order.seats} = $finalTotal%.2f"`
        *(e.g., `"[VIP] S2 x2 = 39.00"`)*.
     7. Return `Some(receipt)`.
   - Keep `protected def extraFee(order: TicketOrder): Double = 0.0` as the default hook method.

2. **Concrete Subclasses**:
   - `StandardCheckout`: 0% discount (`subtotal`), inherits default `extraFee` (`0.0`), label `"STANDARD"`.
   - `MemberCheckout`: 15% discount if `order.isMember` is `true` (`subtotal * 0.85`), otherwise 0% (`subtotal`); inherits default `extraFee` (`0.0`); label `"MEMBER"`.
   - `VipCheckout` (subclass of `MemberCheckout`): 20% discount if `order.isMember` is `true`, otherwise 5% discount; overrides `extraFee` hook to charge **€3.50 per seat** (`3.50 * order.seats`); label `"VIP"`. *(Hint: notice that `VipCheckout` gives an additional 5% off `subtotal` beyond `MemberCheckout`, so `applyDiscount` can refine `super.applyDiscount(order, subtotal)` without duplicating the `order.isMember` branch!)*

> **Mandatory Architectural Requirement (Part 2):** `checkout` **must** remain `final` on `CheckoutProcess` and orchestrate the shared workflow via the Template Method pattern (`isValid`, `applyDiscount`, `extraFee`, `receiptLabel`). Subclasses must **not** duplicate validation, subtotal computation, logging, or `Receipt` creation, and `isInstanceOf` must **not** appear anywhere.

---

### Part 3 — Functional Strategy Pattern & Schedule Partitioning (30 marks)

1. **`object PricingRules`** (`PricingRules.scala`):
   Uses the function type alias `type PricingRule = (TicketOrder, Double) => Double`, where each strategy takes `(order, subtotal)` and returns the resulting price.
   - `val noDiscount: PricingRule`: returns `subtotal` unchanged.
   - `def percentOff(pct: Double): PricingRule`: returns a `PricingRule` lambda reducing `subtotal` by `pct` percent:
     - If `pct <= 0.0`, returns `subtotal` unchanged.
     - Otherwise computes `subtotal * (1.0 - pct / 100.0)`, floored at `0.0` (so `pct >= 100.0` yields `0.0`).
   - `def groupFlatOff(minSeats: Int, amountOff: Double): PricingRule`: returns a `PricingRule` lambda that, if `order.seats >= minSeats` and `amountOff > 0.0`, subtracts `amountOff` from `subtotal` (floored at `0.0`); otherwise returns `subtotal` unchanged.
   - `def bestPrice(rules: List[PricingRule]): PricingRule`: returns a **new `PricingRule` lambda** that evaluates every rule in `rules` on `(order, subtotal)` and returns the **lowest** price produced, or returns `subtotal` unchanged if `rules` is empty.

2. **`ScreeningSchedule.filterScreenings`** (`ScreeningSchedule.scala`):
   - `def filterScreenings(pred: Screening => Boolean): (List[Screening], List[Screening])`
   - Returns `(matching, nonMatching)` — all screenings satisfying `pred` in the first list, and all others in the second list, preserving original order in both lists.

---

### Part 4 — Backward-Compatible Callback & Auditing Closure (25 marks)

1. **`BoxOffice.processBatch`** (`BoxOffice.scala`):
   - `def processBatch(orders: List[TicketOrder], onReceipt: Option[Receipt => Unit] = None): List[Receipt]`
   - Processes each order in `orders` in order via `process.checkout(order)`.
   - Discards invalid orders (`None`) and returns all valid `Receipt`s in order.
   - Whenever an order succeeds (`Some(receipt)`) and `onReceipt` is `Some(callback)`, invokes `callback(receipt)`. Must **not** invoke the callback on invalid orders.
   - Because `onReceipt` defaults to `None`, existing 1-argument callers `office.processBatch(orders)` continue to work seamlessly.

2. **`BatchAuditor.audit`** (`BatchAuditor.scala`):
   - `BatchAuditor` maintains private counters `_vipCount: Int` and `_totalRevenue: Double` exposed via read-only `def vipCount: Int` and `def totalRevenue: Double`.
   - `def audit(office: BoxOffice, orders: List[TicketOrder]): List[Receipt]` processes `orders` through `office` and returns the resulting `List[Receipt]`.
   - Updates `_vipCount` (+1 for each valid receipt whose `label == "VIP"`) and `_totalRevenue` (adding each valid receipt's `finalTotal`). Calling `audit` multiple times on the same `BatchAuditor` **accumulates** across all audited batches.

> **Mandatory Architectural Requirement (Part 4):** `BatchAuditor.audit` **must** pass a **closure** as `Some(...)` to `office.processBatch(orders, Some(...))` to update `_vipCount` and `_totalRevenue` as receipts are emitted. Recomputing these totals by inspecting or folding the returned `List[Receipt]` does **not** satisfy Part 4.

---

### Notes

- **Comparing primitives and strings:** `Int.compare(a, b)` (or `startMinutes.compare(that.startMinutes)`) avoids integer subtraction overflow; `String.compareTo` compares strings lexicographically.
- **Formatting doubles:** Use `f"[$receiptLabel] ${order.screeningId} x${order.seats} = $finalTotal%.2f"` (two decimal places).
- **Safe validation & aggregation:** `Double.isFinite` rejects both `NaN` and `±Infinity`; `String.isBlank` detects empty or whitespace-only strings; `List.partition` splits a list into `(matching, nonMatching)` in one call; `minOption.getOrElse(subtotal)` avoids crashing on empty lists.
- **Optional callback invocation:** `onReceipt.foreach(cb => cb(receipt))` invokes a callback inside `Option` cleanly without `.get` or `isDefined`.

---

### Post-Completion Self-Check (`SoftwareQuality.pdf` Rubric)

Before considering Prep 3 finished, review your code against Mel's quality guidelines:

1. **Correctness & Edge Cases:** Do `InitialTests`, `ExamTestsForStudents`, and your own `MyEdgeCaseTests` all pass? Did you test negative/over-100% `percentOff`, `groupFlatOff` exceeding subtotal, `Double.NaN` / `Infinity` unit prices, empty schedules/order batches, ties in `Ordered`, and multi-batch `BatchAuditor` accumulation?
2. **DRY & Template Method:** Is all checkout validation, subtotal calculation, clamping, and logging written **once** in `CheckoutProcess.checkout`? Does `VipCheckout` refine `super.applyDiscount` rather than repeating `if order.isMember`?
3. **Zero `isInstanceOf` / Minimal Visibility:** Are there zero uses of `isInstanceOf`, zero public `var`s, and no redundant `val`/`var` on forwarded subclass constructor parameters?
4. **Pure FP & Idiomatic Style:** Zero `for`-comprehensions, zero `return`, zero `.get` on `Option`, clean Scala 3 braceless 2-space indentation, short single-purpose methods, and minimal comments?
