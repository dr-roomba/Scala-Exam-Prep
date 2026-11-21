# Study Guide — Prep 5: Full 3-Hour Mock Practical Exam

Read this guide **before** starting your 3-hour offline timer. Once the timer starts, close this guide and work strictly with `EXAM_PAPER.md`, IntelliJ IDEA, your `sbt test` terminal, and the module lecture slides PDF inside IntelliJ.

---

## 1. Where This Fits in the Progression

Prep 5 is the **Level 5 capstone mock examination**. It synthesizes every recurring COMP30950 exam archetype from Preps 1–4 on a fresh domain (aligned with the 2026 rule that the practical exam will *not* extend the Sensor Network assignments):

| Earlier Prep | Skills Synthesized in Prep 5 |
|---|---|
| **Prep 1 (`prep1_trail_guide`)** | Scala 3 braceless syntax, `enum`, immutable `List`/`Map`/`Tuple` (`t(0)`), HOF pipelines, `sortBy`, `Double` tolerance (`+- 0.001`), 0/1/2-element edge cases. |
| **Prep 2 (`prep2_parcel_locker`)** | Smart constructors (`private` constructor + companion `apply: Option[T]`), `Option` chaining (`map`/`flatMap`/`getOrElse`), single-pass `foldLeft` statistics, backward-compatible defaults. |
| **Prep 3 (`prep3_cinema_pipeline`)** | **Mandatory Template Method** (`final` template + abstract step + `protected` hook), predicate HOFs, optional callback `Option[(A, B) => Unit] = None`, **mandatory stateful closure** capturing private state. |
| **Prep 4 (`prep4_warehouse_tree`)** | Recursive `sealed trait` ADTs (leaf rule, exhaustive pattern matching without `case _`), top-level vs nested subtree semantics, multi-key tuple `sortBy`, greedy immutable `Option[Map[String, T]]` resource allocation. |

---

## 2. Offline Exam-Day Lecture Slides Quick-Lookup Map

During the real 2026 exam, your **only** permitted reference is the bundled lecture slides PDF viewed inside IntelliJ's PDF Viewer plugin. Practice jumping directly to the exact deck and slide range below (all filenames match `d:\Studying\Scala Exam\Lecture Slides\`):

| When the Exam Tests... | Exact Slide Deck Filename | Verified Slides |
|---|---|---|
| Braceless syntax, `if/then/else`, `enum`, defaults, no `for`/`return`/`break` | `03_ControlStructuresFunctions.pdf` | s2, s6–9, s14–20 |
| Immutable `Vector`, `List` (`::`, `Nil`), `Map`, `Tuple` (`t(0)`), `val` vs mutability | `04_CollectionClasses.pdf` | s7, s9–19 |
| HOFs (`map`, `filter`, `take`/`drop`, `reduceOption`, `foldLeft`, `flatMap`, `mkString`) | `05_Lambdas and Collection Processing.pdf` | s9–21 |
| Primary constructors, Uniform Access, immutable domain + Functional Core / Imperative Shell | `06_Classes.pdf` | s2–10, s15–17 |
| Singleton `object`, companion objects (mutual `private` access), `apply` factories | `06a_Objects.pdf` / `06b_TheApplyMethod.pdf` | `06a` s2–9; `06b` s5–10 |
| `extends`, `override`, `super`, `sealed`/`final`/`protected`, abstract classes, composition | `07_Inheritance.pdf` | s11–23, s30–35 |
| Subtype polymorphism, dynamic self-sends, avoiding `isInstanceOf` & type-tag anti-patterns | `10_SubtypePolymorphism.pdf` | s4–18 |
| **Template Method** (`final` skeleton, abstract steps, `final` helpers, `protected` hooks) | `11_TemplateMethod.pdf` | s9–24 |
| Traits, mixins (`extends T1, T2`), rich interfaces (`Ordered[A]`), traits vs abstract classes | `12_Traits.pdf` | s4–9, s15–22 |
| **Case classes, `sealed` exhaustiveness, leaf rule & recursive trees** (`Item`/`Article`/`Bundle`) | `13_CaseClasses.pdf` | s2–11, s13–34 |
| **`Option` (`getOrElse`/`match`/`flatMap`), smart constructors (`private` + `apply`), Null Object** | `14_Options and Null Objects.pdf` | s5–15, s17–22 |
| **Lambda callbacks, formal closures & FP Strategy** (`type Alias = A => B`, Lambda vs Trait) | `17_Lambdas in Software Design.pdf` | s11–15, s16–23 |
| **Capstone ADT synthesis** (`case _` trap, smart constructor, `zip(drop(1))`, `foldLeft`, tuple `sortBy`) | `18_Case_classes_tutorial(handout).pdf` | s6–19, s21–23 |
| **`List` recursion (`Nil`/`::`), `partition`, `find`, `span`, `forall`, `exists`, `sortWith`, `zip`, `LazyList`** | `19_Lists.pdf` | s4–9, s12–19 |
| High-level Scala 3 philosophy (FP for low-level logic, OO for high-level structure) | `02_ScalaOveriew.pdf` | s3–6, s11 |

---

## 3. Simulated 3-Hour Offline Exam Protocol

To get maximum value from Prep 5, run it under **real exam conditions** (`ProgrammingExamRules.pdf`):

1. **Environment Lock-Down:**
   - Disconnect Wi-Fi/Bluetooth (or close all browsers, notes, and AI tools completely).
   - Keep **only** IntelliJ IDEA, one terminal running `sbt test`, and blank paper for sketching designs.
   - If you need syntax help, look it up **only** in the lecture slides PDF inside IntelliJ's PDF Viewer.
2. **Verify Starter Baseline Before Starting the Clock:**
   - Open `prep5_mock_exam_cloud_scheduler` in IntelliJ and run `sbt test` once so sbt warms up.
   - Confirm `InitialTests` **PASS (6/6)** and `ExamTestsForStudents` **FAIL strictly with `NotImplementedError` (10/10)**.
3. **Recommended 180-Minute Pacing Plan:**
   - **`0:00 – 0:15` (15 min) — Read & Map:** Read `EXAM_PAPER.md`, `InitialTests.scala`, `ExamTestsForStudents.scala`, and all 4 `src/main/scala/` files. Sketch tree helpers, state flow for Part 3, and Template Method hooks for Part 4 on paper.
   - **`0:15 – 0:35` (20 min) — Part 1 (20 marks):** Implement and verify Part 1 tests green.
   - **`0:35 – 1:05` (30 min) — Part 2 (25 marks):** Implement smart constructor, single-pass `foldLeft` summary, and predicate partitioning. Verify Part 2 tests green.
   - **`1:05 – 1:55` (50 min) — Part 3 (35 marks):** Implement pure functional greedy scheduler with exact urgency & server tie-breakers. Trace a tricky scarcity case by hand. Verify Part 3 tests green.
   - **`1:55 – 2:20` (25 min) — Part 4 (20 marks):** Implement `DeploymentPipeline.deploy` Template Method, `StandardPipeline`, `CanaryPipeline` hook + overhead, and `CostAuditor.runAudit` closure. Verify all 16 provided tests green.
   - **`2:20 – 2:45` (25 min) — Write `src/test/scala/MyEdgeCaseTests.scala`:** Never rely solely on the 10 minimal student tests! Write tests covering 0/1/2 elements, empty/deeply nested racks, smart-constructor boundary `0`/negative cases, equal-urgency and equal-cost tie-breaks, order-dependent greedy failure, `CanaryPipeline(0)`/`CanaryPipeline(-1)`, strict `>` threshold boundary, and failed `runAudit` state reset.
   - **`2:45 – 3:00` (15 min) — Refactor for the 20% Quality Grade (`SoftwareQuality.pdf`):** Eliminate compiler warnings, factor out duplicated tree/cost helpers, shorten long functions, tighten visibility (`private`/`protected`/`val`), remove redundant comments, and run a final `sbt test`.

---

## 4. Key COMP30950 Rules & Exam Pitfalls to Watch

- **Never touch `InitialTests.scala` or `ExamTestsForStudents.scala`** — grading replaces/augments tests against their exact API contract.
- **Scala 3.3.8 smart constructor `.copy` subtlety:** In Scala 3.3.8, `case class X private (...)` does *not* automatically hide the synthesized `.copy` method unless `private def copy(...)` is explicitly defined (already provided in `workload.scala` — do not remove it!).
- **Single-pass `foldLeft` means ONE traversal:** Computing `minRamGb`, `maxRamGb`, `totalCores`, `serverCount`, and `totalCost` with separate `.min`, `.max`, `.sum`, `.length` calls violates the Part 2 constraint. Seed your `foldLeft` cleanly (e.g., matching `case Nil => None` and `case head :: tail => ...` or using an accumulator tuple).
- **Avoid the `case _` exhaustiveness trap:** On `sealed trait ClusterUnit` and `sealed trait Workload`, match every case class explicitly so Scala 3 guarantees exhaustiveness with zero warnings.
- **Greedy order vs backtracking:** Read `Notes:` in Part 3 carefully — implement the exact greedy rule specified rather than inventing a backtracking search.
- **Callback order & closure reset:** In Part 4, remember `Map` iteration order is not sorted — sort entries by ascending `jobId` before invoking `onAssigned`, never invoke `onAssigned` on failure, and reset `_highCostCount` and `_auditedServerIds` at the start of every `runAudit` call.
- **Hard banned constructs:** No `for`-comprehensions, no `return`, no `break`/`continue`, no `null`, no thrown exceptions for domain errors, no `.get` on `Option`, no `var` in `case class`es, no `isInstanceOf`, and no out-of-scope Scala 3 features (`given`/`using`, `extension`, `opaque`, `export`, `Either`, `Try`).

---

## 5. Gemini Web Interface Prompt (Study & Learn Mode — Pre-Exam Warm-Up)

Before starting your 3-hour mock exam, open **Gemini** in **Study & Learn mode** and paste the prompt below for a focused 20–30 minute interactive warm-up across all 4 exam archetypes (using completely unrelated toy examples — zero spoilers):

```text
Act as an interactive UCD COMP30950 (Multi-Paradigm Programming) exam coach in Study & Learn mode. Help me run a sharp pre-exam warm-up drill in Scala 3.3.8 (braceless 2-space indentation syntax) covering the 4 core practical-exam patterns before I sit a timed 3-hour mock exam.

STRICT COURSE RULES (enforce these strictly and critique my code if I break ANY of them):
- Use pure Scala 3.3.8 braceless 2-space indentation syntax (use braces `{ ... }` ONLY for multi-line lambdas or `{ case (a, b) => ... }` partial functions).
- NEVER teach, use, or accept: `for`-comprehensions, `return`, `break`/`continue`, `null`, thrown exceptions for domain errors, `.get` (or `.isDefined` + `.get`) on `Option`, `var` inside a `case class`, `isInstanceOf`, or out-of-scope Scala 3 features (`given`/`using`, `extension`, `opaque type`, `export`, `Either`, `Try`, variance annotations, or stackable traits).
- ALWAYS enforce: `val` and immutable collections (`List`, `Vector`, `Map`, `Tuple` with 0-indexed `t(0)`), exhaustive pattern matching on `sealed trait` ADTs without careless `case _` catch-alls, HOFs (`map`, `filter`, `flatMap`, `foldLeft`, `partition`, `sortBy`, `zip`), `Option`/`Some`/`None`/`getOrElse`, `private` constructors with companion `apply: Option[T]` smart constructors (including `private def copy` for Scala 3.3.8 case classes), Template Method (`final` template + abstract step + `protected` hook), and closures for callbacks.

INTERACTIVE STUDY & LEARN PROTOCOL:
- Run 4 focused mini-drills, ONE concept at a time. Do NOT dump all explanations or solutions at once.
- For each drill: (1) briefly check my intuition with 1 Socratic question, (2) give me a 3–8 line micro-coding exercise on a small unrelated toy domain, (3) wait for my answer, (4) critique both correctness AND COMP30950 style/quality (DRY, SRP, zero warnings, edge cases on 0/1/2 elements), and (5) confirm mastery before moving to the next drill.
- Use fresh everyday toy domains (such as a botanical garden's plots & plants, a music festival's stages & bands, an artisan bakery's ovens & pastry orders, or a bookshop's shelves & shipments).

THE 4 DRILLS TO COVER IN ORDER:
1. Recursive Sealed ADTs & Top-Level vs Nested Aggregation: flattening a recursive leaf/container tree with `foldLeft`/`flatMap`, distinguishing direct top-level containers from nested ones, and avoiding the `case _` exhaustiveness trap.
2. Smart Constructors, Single-Pass `foldLeft` & `partition`: enforcing invariants with `case class ... private` + `private def copy` + companion `apply: Option[T]`, computing min/max/count/average in a single `foldLeft` pass returning `Option`, and preserving traversal order with `partition`.
3. Pure Functional Greedy Allocation with Multi-Key `sortBy`: sorting a second sealed ADT by a computed score plus lexicographical tie-breakers using tuple keys in `sortBy`, threading an immutable remaining-resources pool via `foldLeft` or structural recursion (`Nil` / `x :: xs`), and short-circuiting cleanly to `Option[Map[String, T]]`.
4. Template Method + Optional Callback + Stateful Closure: structuring a `sealed abstract class` with a `final` template method, a `protected` hook method, an abstract step, and an `Option[(String, T) => Unit] = None` callback invoked in deterministic sorted order; then writing a small stateful observer class that passes a closure to update private counters and resets cleanly on repeated runs.

Start now with Drill 1 only, using a brief Socratic warm-up question.
```
