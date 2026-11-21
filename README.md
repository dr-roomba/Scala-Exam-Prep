# COMP30950 Multi-Paradigm Programming — 2026 Practical Exam Prep Suite

Welcome to the progressive 5-assignment preparation suite for **UCD COMP30950 Multi-Paradigm Programming** (Mel Ó Cinnéide, Scala `3.3.8`). Every project mirrors the exact 2026 exam build setup, coding constraints, and testing style, using **five completely novel domains** so you practice designing under pressure rather than memorizing past solutions.

---

## 1. Exam Profile & Strategy (Grounded in the 2026 Corpus)

Facts synthesized from [`ProgrammingExamRules.pdf`](../Past%20Assessments%20and%20Information/ProgrammingExamRules.pdf), [`ProgrammingExamSetup.pdf`](../Past%20Assessments%20and%20Information/ProgrammingExamSetup.pdf), [`SoftwareQuality.pdf`](../Past%20Assessments%20and%20Information/SoftwareQuality.pdf), and the 2022–2025 practical exams:

| Dimension | 2026 Exam Specification |
| :--- | :--- |
| **Date & Duration** | Tuesday **24/11/2026**, **18:00**, **3 hours** (arrive ≥ 10 min early; no exit in first 60 min or last 15 min) |
| **Toolchain & Compiler** | **Scala `3.3.8`**, **sbt `1.12.13`**, **`scalatest-funsuite` `3.2.18`** (`AnyFunSuite` only — **no** `FlatSpec` or `Matchers`) |
| **Environment** | **Strictly offline** (Wi-Fi & Bluetooth off except brief initial test download & single final push). **No AI tools, no USBs.** |
| **Only 3 Allowed Apps** | 1. **IntelliJ IDEA** &nbsp;·&nbsp; 2. **Terminal 1** (`java AutoCommit.java`) &nbsp;·&nbsp; 3. **Terminal 2** (`sbt` / `sbt test`) |
| **Only Allowed Reference** | `module_slides.pdf` bundled in the exam repo — opened **exclusively inside IntelliJ's PDF Viewer plugin**. Accessing any other file on your laptop is prohibited. |
| **Critical 2026 Domain Rule** | **The 2026 exam will NOT extend the Sensor Network assignment.** Expect a fresh, intuitive domain (greenfield or small unfamiliar starter repo) testing the skills from the semester. |
| **Grading Split** | **~80% Correctness / ~20% Software Quality** — and **quality marks apply ONLY to the proportion of correct code**. Correctness is *sine qua non*: get it working first, then refactor. |
| **Difficulty Profile** | **3 or 4 sequential parts = 100 marks**: Part 1 (~20%) very straightforward; **≥ 60%** straightforward programming; final **30–40%** algorithmic or constrained by a mandatory OO/FP design pattern. |
| **Strict Repo Rules** | **Never edit provided unit tests.** Do **not** add new directories or dependencies to `build.sbt`. Add only `.scala` files and `ide-events.csv`. `sbt test` must run cleanly from repo root. |

### High-Leverage Exam-Day Strategy
1. **Read the exam paper and `ExamTestsForStudents.scala` side-by-side.** Mel's papers explicitly state that omitted details are resolved unambiguously by the provided tests — use them to lock down exact class/method names, parameter orders, tuple indices (`t(0)`), map keys, formatting, and `+- 0.001` floating-point tolerance.
2. **Expect 4 recurring exam archetypes** (observed across 2022–2025):
   - *Warm-up (20–30%)*: Backward-compatible extension (default parameter) or foundational domain types.
   - *Collection Pipeline / Aggregation (25–30%)*: `Map` histograms, formatted summaries, single-pass `foldLeft` stats, or predicate partitioning.
   - *Core Algorithmic / Tree / State Transformation (30–40%)*: Recursive ADT traversal, immutable state updates, greedy `sortBy` + `zip.toMap`, returning `Option` on failure.
   - *Mandatory Design Pattern / Paradigm Constraint (20–30%)*: Grades **how** you build it, not just output (e.g., `final` **Template Method**, `Option[T => Unit]` callback + **mandatory closure**, or higher-order predicate parameter).
3. **Never alter provided tests — write your own `MyEdgeCaseTests.scala`.** Provided student tests only cover happy paths (2–3 tests per part), while unseen grading tests target **0, 1, and 2 elements, ties, boundary values, `None`/empty inputs, and nested structures**. Extra tests in a separate file both catch bugs and boost your quality grade.
4. **Keep `InitialTests` green at all times.** Use default parameters (`param: T = default`) when extending existing classes/methods so backward compatibility never breaks.
5. **Budget time explicitly:** ~10 min reading paper + initial tests, ~130 min implementing & edge-case testing part-by-part, ~25 min refactoring for `SoftwareQuality.pdf` (20% grade), ~15 min buffer + submission checklist. Use the provided paper UCD exam book for design sketches — it can only help your quality mark.

---

## 2. Progressive 5-Assignment Roadmap

All five projects use the exact 2026 exam config (`build.sbt` with Scala `3.3.8` & `scalatest-funsuite` `3.2.18`, `sbt 1.12.13`, flat default package, pure Scala 3 braceless syntax, compiling `= ???` skeletons where `InitialTests` pass immediately and `ExamTestsForStudents` fail strictly with `NotImplementedError`). Zero overlap with past/current domains (Bus, Chess, Cards, Hotels, Sensors, Bank Ledger, Greeter).

| Prep & Folder | Brief & Study Guide | Difficulty & Time | Novel Domain | Builds On | New Topics & Patterns Tested |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Prep 1**<br>[`prep1_trail_guide`](./prep1_trail_guide/) | [BRIEF.md](./prep1_trail_guide/BRIEF.md)<br>[STUDY_GUIDE.md](./prep1_trail_guide/STUDY_GUIDE.md) | ★☆☆☆☆<br>**60–75 min** | Hiking Trail Network & Elevation Analytics | Semester baseline (P2–P3, Asgn 1) | Scala 3 expressions, `enum`, `case class`, `Vector`/`Map`/`Tuple`, HOF pipelines (`map`, `filter`, `flatMap`, `foldLeft`, `mkString`, `sortBy`, `maxByOption`/`minByOption`, `partition`), safe `zip(drop(1))`, `Double` `+- 0.001`, 0/1/2-element edge cases. |
| **Prep 2**<br>[`prep2_parcel_locker`](./prep2_parcel_locker/) | [BRIEF.md](./prep2_parcel_locker/BRIEF.md)<br>[STUDY_GUIDE.md](./prep2_parcel_locker/STUDY_GUIDE.md) | ★★☆☆☆<br>**90 min** | Smart Parcel Locker Network | Prep 1 (HOFs, `Vector`/`Map`/`Tuple`, edge cases) | Classes & primary constructors, Uniform Access, Companion Objects & `apply`, **Smart Constructors** (`private` ctor + `apply: Option[T]`), `Option` chaining & CSV parsing (`toDoubleOption`/`flatMap`), single-pass `foldLeft` stats, **Functional Core / Imperative Shell**. |
| **Prep 3**<br>[`prep3_cinema_pipeline`](./prep3_cinema_pipeline/) | [BRIEF.md](./prep3_cinema_pipeline/BRIEF.md)<br>[STUDY_GUIDE.md](./prep3_cinema_pipeline/STUDY_GUIDE.md) | ★★★☆☆<br>**105–120 min** | Cinema Ticketing, Pricing & Screening Workflows | Prep 1–2 (immutability, `Option`, companions, HOFs) | Inheritance (`super`, `sealed`/`final`/`protected`), Subtype Polymorphism (no `isInstanceOf`), **Mandatory Template Method** (`final` skeleton + abstract + `protected` hook), Traits & Mixins (`Ordered[T]`, capability traits, comma `extends`), **Singleton Null Object**, **FP Strategy** (`type` alias + lambdas), predicate HOF, backward-compatible `Option[T => Unit] = None` callback + **mandatory closure**. |
| **Prep 4**<br>[`prep4_warehouse_tree`](./prep4_warehouse_tree/) | [BRIEF.md](./prep4_warehouse_tree/BRIEF.md)<br>[STUDY_GUIDE.md](./prep4_warehouse_tree/STUDY_GUIDE.md) | ★★★★☆<br>**135–150 min** | Recursive Warehouse Logistics & Dispatch Planner | Prep 1–3 (HOFs, `Option`, `sealed` hierarchies, polymorphism) | Recursive `sealed trait` ADTs (Leaf Rule, exhaustive matching, avoiding the `case _` trap), nested recursive helpers + `foldLeft` tree traversals, top-level vs nested composite distinction, **mandatory structural `List` recursion (`Nil` / `x :: xs`)**, greedy `Option[Map[String, T]]` allocation (`sortBy` + `zip.toMap`), second sealed ADT with multi-key stable `sortBy` + open polymorphic `abstract class` sinks (`Console`/`Memory`). |
| **Prep 5 (Mock Exam)**<br>[`prep5_mock_exam_cloud_scheduler`](./prep5_mock_exam_cloud_scheduler/) | [EXAM_PAPER.md](./prep5_mock_exam_cloud_scheduler/EXAM_PAPER.md)<br>[STUDY_GUIDE.md](./prep5_mock_exam_cloud_scheduler/STUDY_GUIDE.md) | ★★★★★<br>**180 min strict** | Cloud Compute Cluster & Job Scheduler (**Full 3-Hour Mock Exam**) | **Synthesizes Prep 1–4** in an authentic 2-page 2026 exam paper | **P1 (20m)**: backward-compatible default field + top-level recursive cost sum.<br>**P2 (25m)**: smart constructor + single-pass `foldLeft` stats + predicate HOF partitioning.<br>**P3 (35m)**: recursive tree flattening + greedy `Option[Map[String, Server]]` scheduling over sealed `Workload` ADT.<br>**P4 (20m)**: **Mandatory Template Method + `Option` callback + mandatory stateful closure** auditor. |

---

## 3. Recommended Study Workflow for Each Prep Assignment

Follow the same disciplined 3-step loop for Preps 1 through 4 (and run Prep 5 under strict exam simulation rules):

### Step 1 — Read the Suggested Lecture Slides (Offline Habit)
- Open the exact slide decks listed in that assignment's `STUDY_GUIDE.md` from [`../Lecture Slides/`](../Lecture%20Slides/).
- Practice opening PDFs **inside IntelliJ's PDF Viewer** so navigating `module_slides.pdf` offline becomes muscle memory before exam day.

### Step 2 — Interactive Gemini Warm-Up (15–25 min, Study & Learn Mode)
- Open the assignment's `STUDY_GUIDE.md` and copy the self-contained **Gemini Study & Learn Mode Prompt**.
- Paste it into Gemini with **Study & Learn mode** enabled.
- Work through its Socratic questions and 3–5 line micro-exercises on unrelated toy examples (**zero spoilers** of the prep domain) until every concept clicks under Mel's exact Scala 3.3.8 rules.

### Step 3 — Implement, Edge-Case Test & Refactor in IntelliJ
1. **Verify the clean skeleton:** Open the prep folder in IntelliJ (or `cd` into it in a terminal) and run:
   ```bash
   sbt test
   ```
   Confirm that **all `InitialTests` PASS** and **`ExamTestsForStudents` compile cleanly and FAIL strictly with `NotImplementedError` (`???`)**.
2. **Read spec + tests together:** Open `BRIEF.md` (or `EXAM_PAPER.md`) side-by-side with `src/test/scala/ExamTestsForStudents.scala` to verify exact signatures, types, ordering, and edge-case rules.
3. **Implement part-by-part:** Complete Part 1 → Part 4 sequentially, running `sbt test` after each part and ensuring `InitialTests` stay green.
4. **Add edge-case tests in a separate file:** Never modify `InitialTests.scala` or `ExamTestsForStudents.scala`. Create `src/test/scala/MyEdgeCaseTests.scala` (`extends AnyFunSuite`) testing 0/1/2 elements, ties, boundaries, invalid inputs (`None`, `NaN`/`Infinity`, blank strings, negative values), nested trees, and custom test spies.
5. **Refactor for the 20% quality mark:** Run a focused 10–15 minute refactoring pass against Section 5 (`SoftwareQuality.pdf`), re-run `sbt test`, and confirm **zero compiler warnings** (especially exhaustiveness or unreachable-case warnings).

> **Prep 5 Mock Exam Protocol:** Disconnect Wi-Fi and Bluetooth, close all browsers and AI tools, keep only IntelliJ (with slides in PDF Viewer) + one `sbt test` terminal + blank paper open, start a **180-minute timer**, and grade yourself afterwards using the rubric in [`prep5_mock_exam_cloud_scheduler/EXAM_PAPER.md`](./prep5_mock_exam_cloud_scheduler/EXAM_PAPER.md).

---

## 4. Offline Exam-Day Lecture Slides Quick-Lookup Index

Use this index to locate exact syntax, design patterns, and lecturer rules inside [`../Lecture Slides/`](../Lecture%20Slides/) (and the bundled `module_slides.pdf`) in seconds during the exam. Slide numbers are verified against all 18 course decks (396 slides total).

| Topic / Exam Pattern | Exact Slide Deck(s) | Verified Slide Numbers & Key Anchors |
| :--- | :--- | :--- |
| **Scala 3 Style, Expressions, `enum`, `s`/`f` Strings, Default/Named Args, `lazy val`** | [`02_ScalaOveriew.pdf`](../Lecture%20Slides/02_ScalaOveriew.pdf)<br>[`03_ControlStructuresFunctions.pdf`](../Lecture%20Slides/03_ControlStructuresFunctions.pdf) | `02` *(note spelling `Overiew`)*: **s3–11** (2-space braceless style, `@main`, `val`/`var`, OO+FP fusion).<br>`03`: **s2–10** (`val`/`var`, `if/then/else`, `Unit`, blocks as values, no `return`, `s`/`f` strings); **s14–16** (`match`, `enum`, `.values`/`.ordinal`/`.valueOf`); **s17–21** (no `break`/no `for`, recursion, default/named args, `def`/`val`/`lazy val`). |
| **Collections (`Vector`, `Map`, `Tuple`, `List`, `Array`) & `val` vs Immutability** | [`04_CollectionClasses.pdf`](../Lecture%20Slides/04_CollectionClasses.pdf) | **s2–3** (immutable hierarchy & persistence); **s7** (`Vector`: `:+`, `+:`, `.updated`); **s9–10** (`Option` preview, `minOption`); **s11–17** (immutable `Map`, `get`/`getOrElse`, `+`, counter increment **s16**, `keySet`/`values` **s17**); **s15** (`val`/`var` vs object mutability); **s18** (`Tuple` 0-indexed `t(0)`); **s19** (`List`, `::`, `Nil`). |
| **HOFs & Collection Pipelines (`map`, `filter`, `flatMap`, `foldLeft`, `reduceOption`, `mkString`)** | [`05_Lambdas and Collection Processing.pdf`](../Lecture%20Slides/05_Lambdas%20and%20Collection%20Processing.pdf) | **s2–6** (lambdas & HOFs); **s7–11** (avoid `foreach`, Tell Don't Ask, `_` shortening **s9**, `map` **s10–11**); **s12–13** (`filter`, `take`/`drop`/`takeWhile`/`dropWhile`); **s14–17** (`reduce`/`reduceOption` **s14–15**, `foldLeft` **s16–17**, `mkString` **s17** notes); **s18–21** (`flatten`, `flatMap`, Cartesian product). |
| **Classes, Primary Constructors, Uniform Access & Functional Core / Imperative Shell** | [`06_Classes.pdf`](../Lecture%20Slides/06_Classes.pdf) | **s2–3** (classes, zero-arg mutator `()`, file naming); **s4–6** (getters/`_=` setters, **Uniform Access Principle s6**); **s7** (class-based privacy); **s8–10** (primary constructor `val`/`var`/plain/`private`); **s12–16** (immutable `BankAccount` returning `Option`); **s17** (**Functional Core, Imperative Shell**). |
| **Singleton Objects, Companion Objects & `apply` Factories** | [`06a_Objects.pdf`](../Lecture%20Slides/06a_Objects.pdf)<br>[`06b_TheApplyMethod.pdf`](../Lecture%20Slides/06b_TheApplyMethod.pdf) | `06a`: **s2–6** (lazy singleton `object`, DRY/SRP); **s7–9** (**companion object** replacing `static`, same file, mutual `private` access).<br>`06b`: **s2–4** (`apply` & stateful function objects); **s5–8** (constructor proxies without `new`, `Array(...)` vs `arr(i)`); **s9–10** (custom subtype factory `apply`, guards, `private` primary constructor). |
| **`Option`, Smart Constructors & Safe CSV Parsing** | [`14_Options and Null Objects.pdf`](../Lecture%20Slides/14_Options%20and%20Null%20Objects.pdf)<br>[`18_Case_classes_tutorial(handout).pdf`](../Lecture%20Slides/18_Case_classes_tutorial%28handout%29.pdf) | `14`: **s5–9** (`Option`/`Some`/`None`, avoid `.get` **s7**, `getOrElse` **s8**, `match` **s9**); **s10–12** (returning `Option` instead of `NaN`/exceptions); **s13–14** (`flatMap(_.toIntOption)`); **s15** (**Smart Constructor** `private` ctor + `apply: Option`).<br>`18`: **s11** (`case class Amount private(...)` — *see Scala 3.3.8 `.copy` note below!*); **s12** (`Option.map` vs `flatMap`); **s13–14** (CSV parsing via `toDoubleOption`/`toLongOption` + `.isBlank` + `flatMap`). |
| **Null Object Pattern** | [`14_Options and Null Objects.pdf`](../Lecture%20Slides/14_Options%20and%20Null%20Objects.pdf) | **s17–22** (`NullPet` evolution → singleton `object NullPet extends Pet`, default arg `pet: Pet = NullPet`, `.length` counting caveat **s22**). |
| **Inheritance, `super`, Modifiers (`sealed`/`final`/`protected`) & Composition over Inheritance** | [`07_Inheritance.pdf`](../Lecture%20Slides/07_Inheritance.pdf) | **s2–5** ("is-a" test); **s6–12** (`extends`, dynamic binding **s8**, forwarding superclass params **without `val`/`var`** **s11**); **s13–18** (`override`, `super` refinement **s14**, overloading vs overriding **s16**, overriding parameterless `def` with `val` **s17**); **s19–20** (`final`/`open`/`sealed`/`protected`); **s21–23** (abstract classes); **s30–35** (**Composition over Inheritance**: `RobotArm`, `Polygon`/`Rectangle`). |
| **Type Hierarchy, Polymorphism, Anti-Patterns (`isInstanceOf`) & Fragile Base Class** | [`08_TypeSystems.pdf`](../Lecture%20Slides/08_TypeSystems.pdf)<br>[`10_IntroToPolymorphism.pdf`](../Lecture%20Slides/10_IntroToPolymorphism.pdf)<br>[`10_SubtypePolymorphism.pdf`](../Lecture%20Slides/10_SubtypePolymorphism.pdf) | `08`: **s18–19** (`class == type + implementation`, static vs dynamic type); **s20–21** (Scala 3 hierarchy: `Any` → `Matchable` → `AnyVal`/`AnyRef` → `Null` → `Nothing`).<br>`10_Intro`: **s5–10** (ad hoc, parametric `[A]`, upper bounds `[A <: Ordered[A]]` **s9**, `List[+A]` **s10**).<br>`10_Subtype`: **s4–11** (3 pillars); **s12–16** (message passing & self-sends **s16**); **s17–18** (ban `isInstanceOf` & type tags); **s25–27** (Fragile Base Class & LSP). |
| **Template Method Pattern** | [`11_TemplateMethod.pdf`](../Lecture%20Slides/11_TemplateMethod.pdf) | **s9–18** (`GamePlayer` skeleton, dynamic self-sends, Inversion of Control); **s19–21** (3 helper types: **abstract** mandatory **s19**, **`final`** fixed + **`final` template method s20**, **`protected` hook** optional default **s21**); **s22–24** (`Vehicle.applyNctTest`). |
| **Traits, Mixins, Linearisation & `Ordered[T]`** | [`12_Traits.pdf`](../Lecture%20Slides/12_Traits.pdf) | **s4–9** (interfaces, concrete methods, Scala 3 trait constructors **s7**, `Showable` enrichment **s8–9**); **s10–17** (Diamond Problem, rightmost linearisation **s14**, comma mixin syntax `extends T1, T2` **s16**, objects extending traits **s17**); **s18–21** (Thin vs Rich interfaces, **`Ordered[A]`** + `compare` → `<`/`>`/`.sorted`); **s22** (*"If in doubt, prefer a trait"*). |
| **Case Classes, Exhaustiveness, `case _` Trap, Recursive Trees & Matching vs Polymorphism** | [`13_CaseClasses.pdf`](../Lecture%20Slides/13_CaseClasses.pdf)<br>[`18_Case_classes_tutorial(handout).pdf`](../Lecture%20Slides/18_Case_classes_tutorial%28handout%29.pdf) | `13`: **s2–11** (immutable `val`, no `var`, `equals`/`copy`/`unapply`, identity); **s13–21** (`match`, `\|`, guards **s14–15**, `case p: Phone` vs `case Phone` **s16–18**, type erasure `Map[?, ?]` **s21**); **s22–25** (`sealed` exhaustiveness, **Leaf Rule s25**); **s26–34** (**Recursive Trees**: `Item`/`Article`/`Bundle`, standalone functions **s33**); **s35–40** (OO vs Matching).<br>`18`: **s4–10** (`sealed trait Txn`, match only where subtypes differ **s8**, **`case _` trap s10**); **s17–23** (multi-key stable `sortBy` **s19**, polymorphic `FlagHandler`/`MemoryHandler` **s20**, **Matching vs Polymorphism s21 & s23**). |
| **Callbacks, Closures & Strategy Pattern** | [`17_Lambdas in Software Design.pdf`](../Lecture%20Slides/17_Lambdas%20in%20Software%20Design.pdf) | **s4–15** (`Alarm` → `Wakeable` → `Timer(alertFunc: () => Unit)` callbacks; formal **Closures** capturing lexical private state **s12–14**); **s16–22** (**Strategy**: OO trait **s17–19** vs FP `type Alias = A => B` + lambdas **s20–22**); **s23** (**Lambda vs Trait** decision rule). |
| **Lists, Structural Recursion (`Nil`/`::`), Safe `zip(drop(1))` & `LazyList`** | [`18_Case_classes_tutorial(handout).pdf`](../Lecture%20Slides/18_Case_classes_tutorial%28handout%29.pdf)<br>[`19_Lists.pdf`](../Lecture%20Slides/19_Lists.pdf) | `18`: **s15–16** (**safe `ts.zip(ts.drop(1))`** instead of crashing `ts.tail`; *"Test list code on 0, 1 and 2 elements"*; `foldLeft`).<br>`19`: **s2–5** (`Nil`, right-associative `::`, `head`/`tail`/`isEmpty`); **s6–7** (**structural `List` recursion** `case List()` / `case y :: ys`); **s8–9** (`:::`, `take`/`drop`/`splitAt`, avoid `length`/`last`/`init` and indexing); **s12–15** (`partition`, `find`, `span`, `forall`/`exists` **s13–14**, `sortWith` **s15**); **s16–17** (`zip`, `zipWithIndex`, `unzip`, `pair(0)`); **s18–19** (**`LazyList`** `#::`). |

> **Scala 3.3.8 Smart Constructor `.copy` Gotcha:** `18_Case_classes_tutorial(handout).pdf` (s11) notes that `case class Amount private (value: Double)` hides `.copy` in newer Scala 3.x releases, **but in Scala `3.3.8` (the 2026 grading compiler) the synthesized `.copy` method remains public** unless you explicitly declare `private def copy(...)` (or use a non-case class). Keep this in mind if an exam or test checks that invalid instances cannot be constructed via `.copy`!

### High-Frequency Exam Idioms (Commit to Memory)
```scala
// 1. Safe consecutive pairs (works on 0, 1, and 2+ elements — NEVER use .tail!)
val pairs = items.zip(items.drop(1))

// 2. Deterministic stable multi-key sorting (tuple keys sort lexicographically)
val sorted = issues.sortBy(i => (i.timestamp, rank(i), i.id))

// 3. Greedy pairing into Map (e.g., highest-value items with lowest-rate slots)
val allocation: Map[String, Item] = sortedKeys.zip(sortedValues).toMap

// 4. Smart Constructor with Scala 3.3.8 .copy protection
case class PositiveAmount private (value: Double):
  private def copy(value: Double = this.value): PositiveAmount =
    new PositiveAmount(value)

object PositiveAmount:
  def apply(value: Double): Option[PositiveAmount] =
    if value.isFinite && value > 0.0 then Some(new PositiveAmount(value)) else None
```

---

## 5. `SoftwareQuality.pdf` 20% Quality Checklist & Hard Module Rules

**Overarching Principle:** *Write Once, Read Many.* Remember that the 20% quality grade applies **only to your working, correct code** — finish passing tests first, then spend 15–20 minutes running this exact checklist before submitting.

### The 4 Core Principles
1. **SRP (Single Responsibility Principle):** Each class, object, and method does one cohesive job.
2. **KISS / Minimise Cognitive Load:** Reread working code and simplify unnecessarily clever or nested logic.
3. **DRY (Don't Repeat Yourself):** Factor duplicated logic into clean helpers, companion traits, or supertype members.
4. **Reduce Dependency:** Keep coupling low; prefer composition over inheritance and narrow interfaces.

### 10-Point Inspection Checklist (Directly Graded)
- [ ] **Craft & refactor after passing tests** — never submit your rough first draft.
- [ ] **Clean Scala 3 braceless layout** — 2-space soft tabs; use `{ ... }` **only** for multi-line lambdas, partial-function `{ case ... => ... }`, and `test("...") { ... }`.
- [ ] **Minimal, high-value comments** — explain *why* only when non-obvious; never narrate *what* the code does.
- [ ] **Clear, intention-revealing names** — rename vague variables (`x`, `tmp`, `res`) once their role is clear.
- [ ] **Short, single-purpose methods** — two 5-line methods beat one 10-line method; use nested recursive helpers (`def outer(...) = def loop(...) ...`) when only part of an operation recurses.
- [ ] **Minimal variable count & scope** — never promote a local `val` into a class field.
- [ ] **Minimal visibility & immutability** — default to `private` helpers and `val`; use `sealed` on ADT/class hierarchies and `final` on Template Methods; **never** put `val`/`var` on forwarded superclass constructor parameters.
- [ ] **Tell, Don't Ask** — put domain behavior on the class that owns the data rather than pulling fields out via getters.
- [ ] **Distribute functionality cleanly** — keep a pure **Functional Core** separate from any thin stateful **Imperative Shell**; avoid God classes.
- [ ] **Replace commented blocks with `private` methods** — if a block needs a comment to explain what it does, extract it into a named `private def`.

### Hard COMP30950 Rules (Zero-Tolerance Violations)

| Never Do This | Do This Instead |
| :--- | :--- |
| `for`-comprehensions for problem solving, `while` loops, or `foreach` + `var` to build collections | HOF pipelines (`map`, `filter`, `flatMap`, `foldLeft`, `partition`, `exists`, `forall`, `zip`) or pure recursion |
| `return` statements, `break` / `continue`, `scala.util.control.Breaks` | Last expression in a block is the return value; use recursion or HOFs |
| `null`, thrown exceptions (`require` / `throw`) for domain/validation failures, `.get` or `.isDefined` + `.get` on `Option` | `Option` (`Some` / `None`), `getOrElse`, pattern matching, `map` / `flatMap`, Smart Constructors (`apply: Option[T]`) |
| `var` inside a `case class` | Immutable `val` fields + `.copy(...)` updates |
| `isInstanceOf[...]` or behavior-switching type-tag fields in OO hierarchies | Subtype polymorphism (overriding / dynamic dispatch) or exhaustive `match` on a `sealed trait` ADT |
| Careless `case _` catch-all on a `sealed trait` | Match every subtype explicitly so the compiler warns if a case is missed |
| `xs.zip(xs.tail)` (crashes on empty collection) | `xs.zip(xs.drop(1))` — always test on 0, 1, and 2 elements |
| Out-of-scope Scala 3 / test features (`given`/`using`, `extension`, `opaque`, `export`, `Either`, `Try`, `A \| B`, variance `+A`/`-A`, stackable `abstract override`, `FlatSpec`, `Matchers`) | Stick strictly to taught COMP30950 Scala 3.3.8 constructs and `AnyFunSuite` (`assert(a === b)`, `+- 0.001`) |

---

## 6. Pre-Exam Technical Setup Checklist

Verify every item **offline before exam day** (grounded in [`ProgrammingExamSetup.pdf`](../Past%20Assessments%20and%20Information/ProgrammingExamSetup.pdf), [`ActivityTracker.pdf`](../Past%20Assessments%20and%20Information/ActivityTracker.pdf), and [`ProgrammingExamRules.pdf`](../Past%20Assessments%20and%20Information/ProgrammingExamRules.pdf)):

### Before Exam Day (Offline Dry Run)
- [ ] **JDK 11+** and **Git 2.25+** installed and accessible from terminal (`java -version`, `git --version`).
- [ ] **IntelliJ IDEA** configured with:
  - Scala plugin (matching **Scala `3.3.8`** / **sbt `1.12.13`**).
  - **PDF Viewer** plugin (by *Ivan Posti*) — test opening `module_slides.pdf` directly inside IntelliJ.
  - **Activity Tracker** plugin (by *Dmitry Kandalov*) configured with **EXACT settings**:
    - **ON:** `Track IDE actions`, `Track keyboard`
    - **OFF:** `Poll IDE state`, `Track key released`, `Track mouse`
- [ ] Clone your personal exam repo (`https://github.com/UCD-Mel/30950_<studentNumber>_exam`) and verify Git credentials work for `git push origin main`.
- [ ] **Turn Wi-Fi completely OFF** and verify inside the repo root:
  1. `sbt test` compiles and passes completely offline (proves Scala `3.3.8` & `scalatest-funsuite` `3.2.18` are cached locally).
  2. `java AutoCommit.java` starts cleanly and auto-commits changes every ~20 seconds.
  3. `module_slides.pdf` opens smoothly inside IntelliJ's PDF Viewer.
- [ ] Pack: **UCD student card**, fully charged laptop + power adapter, optional earplugs or ear-defender headphones (no music), water / small unobtrusive snack.

### Exam Start Protocol
- [ ] Place UCD student card face up and **switched-off mobile phone** on desk.
- [ ] Download initial student tests, then **turn Wi-Fi and Bluetooth OFF immediately**.
- [ ] Keep **only 3 apps open**: IntelliJ IDEA, Terminal 1 running `java AutoCommit.java` in repo root, Terminal 2 for `sbt test` in repo root.
- [ ] Click **Start Tracking** in Activity Tracker.

### End-of-Exam Submission Sequence (Exact Order)
1. Run `sbt test` from repo root — confirm clean compilation, passing `InitialTests` & `ExamTestsForStudents`, and zero prohibited files/directories.
2. In IntelliJ, **Stop Tracking** in Activity Tracker → `Current Log -> Open in File Manager` (or `Open in IDE` on Linux) → confirm `ide-events.csv` is **non-empty** → move `ide-events.csv` into the repo root (replacing `ide-events[REPLACE AT EXAM END!].csv`) and wait ~20 seconds for `AutoCommit.java` to commit it.
3. Stop `AutoCommit.java` with `Ctrl+C`.
4. Run one final manual check (`git status`) and commit anything unstaged (`git add -A` then `git commit -m "Final exam submission"`).
5. Re-enable network and run a **single** `git push origin main`.
6. Verify your pushed files (all `.scala` files + `ide-events.csv`) on GitHub, leave the paper exam book and exam paper on your desk, and complete the emailed post-exam questionnaire within 24 hours.
