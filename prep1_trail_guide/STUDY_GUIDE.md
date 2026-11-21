# Study Guide — Prep 1: Hiking Trail Network & Elevation Analytics

Complete this short study guide **before** opening `BRIEF.md` or starting to code. It points you to the exact lecture slides to review, warns you about classic COMP30950 pitfalls, and includes a copy-pasteable prompt for **Gemini Study & Learn mode** to drill every underlying Scala 3 skill without spoiling the assignment.

---

## 1. Where This Fits in the 5-Assignment Progression

| Prep | Focus | Status |
|---|---|---|
| **Prep 1 (This Assignment)** | **Scala 3 Foundations & Pure Functional Collection Pipelines** (`enum`, `case class`, `Vector`/`Map`/`Tuple`, `Option`, `map`/`filter`/`flatMap`/`foldLeft`/`partition`/`sortBy`/`mkString`/`minByOption`/`maxByOption`, safe `zip(drop(1))`) | **Start here (60–75 min)** |
| Prep 2 | Classes, Uniform Access, Companion Objects, Smart Constructors (`apply: Option[T]`), `Option` Chaining & Functional Core / Imperative Shell | Next |
| Prep 3 | Inheritance, Subtype Polymorphism, Mandatory Template Method, Traits/`Ordered[T]`, Null Object, FP Strategy & Closures | Later |
| Prep 4 | Recursive `sealed trait` ADTs, Structural `List` Recursion (`Nil` / `::`), Greedy `Option[Map]` Optimization & Open Polymorphic Handlers | Later |
| Prep 5 | Full 3-Hour Timed Mock Exam synthesizing all 4 archetypes | Capstone |

Every COMP30950 practical exam (2022–2025) requires fluent, bug-free collection pipelines under time pressure. Prep 1 isolates that exact skill so it becomes second nature before you add OO hierarchies, smart constructors, or recursive trees.

---

## 2. Suggested Lecture Slides to Read

All filenames below are exact filenames inside `d:\Studying\Scala Exam\Lecture Slides\`:

1. **`02_ScalaOveriew.pdf`** *(note exact spelling: `Overiew`)*
   - **Slides 3–5:** Scala 3 braceless 2-space syntax, `val` vs `var`, functional `map` vs imperative loops.
   - **Slides 6–9:** Static typing, type inference, chained pipelines, immutable collections (`List`, `Vector`, `Map`, `Set`).
   - **Slide 11:** Object-functional design philosophy (*FP for low-level logic, OO for high-level structure*).

2. **`03_ControlStructuresFunctions.pdf`**
   - **Slides 2–7:** `val` vs `var`, operators as methods (no `++`), `if ... then ... else ...` as expressions, `Unit`.
   - **Slides 8–10:** Indented code blocks as values (no braces, no `return`), `s"..."` and **`f"..."` formatted string interpolation** (`f"...%2.2f"`).
   - **Slides 14–16:** Basic `match` expressions and **Scala 3 `enum`** (`.values`, `.ordinal`, `valueOf`).
   - **Slides 17–18:** Hard module rules — no `break`/`continue`, **no `for`-comprehensions in problem solving**.

3. **`04_CollectionClasses.pdf`**
   - **Slides 2–3:** Immutable collections hierarchy and structural sharing (persistence).
   - **Slide 7:** **`Vector`** (`Vector(...)`, `:+` append, `+:` prepend, `.updated`).
   - **Slides 9–10:** **`Option[A]`** preview (`Some`, `None`, `minOption`, `getOrElse`).
   - **Slides 11–14 & 16–17:** **Immutable `Map`** (`Map.empty[K, V]`, `getOrElse`, `get`, immutable `+` update, and **incrementing a counter in an immutable `Map`** on **Slide 16**).
   - **Slide 15:** `val`/`var` (reference) vs mutability/immutability (object).
   - **Slide 18:** **Scala 3 Tuples** (0-indexed access: `t(0)`, `t(1)`).

4. **`05_Lambdas and Collection Processing.pdf`**
   - **Slides 2–6:** Anonymous functions (lambdas) and Higher-Order Functions (HOFs).
   - **Slides 7–9:** Internal vs external iteration ("Tell, don't ask"), avoiding `foreach`, and 4-stage `_` placeholder shortening (each `_` is a distinct parameter!).
   - **Slides 10–13:** `map`, `filter`, `take`/`drop`/`takeWhile`/`dropWhile`.
   - **Slides 14–17:** `reduce` vs safe `reduceOption`, and **`foldLeft`** (including changing result type and step-by-step trace on **Slide 17**; `mkString` noted on **Slide 17**).
   - **Slides 18–21:** `flatten` and **`flatMap`** (flattening nested collections and `Option`-producing functions).

5. **`19_Lists.pdf`**
   - **Slides 10–14:** HOFs recap, **`partition`**, **`find`**, `span` (**Slide 12**), and **`forall`/`exists`** quantifiers (**Slides 13–14**).
   - **Slide 15:** Sorting (`sortWith`; also see `sortBy` on **Slide 19** of `18_Case_classes_tutorial(handout).pdf`).
   - **Slides 16–17:** **`zip`**, `zipWithIndex`, `unzip`, and folding over zipped pairs using 0-indexed `pair(0)` / `pair(1)`.

6. **Quick 1-Slide Bonus Preview — `18_Case_classes_tutorial(handout).pdf`**
   - **Slide 15:** Safe consecutive pairs via **`xs.zip(xs.drop(1))`** (never `xs.tail`!), `{ case (a, b) => ... }` tuple destructuring, and the rule **"Test list code on 0, 1 and 2 elements."**

---

## 3. Key Rules & Pitfalls to Keep in Mind

1. **Never write `xs.zip(xs.tail)`** — if `xs` is empty, `.tail` throws `UnsupportedOperationException`. Always write **`xs.zip(xs.drop(1))`**, which safely yields an empty collection for both 0-element and 1-element inputs.
2. **Never call crashing `maxBy`, `minBy`, `reduce`, or `head`** on collections that might be empty. Use **`maxByOption`**, **`minByOption`**, **`reduceOption`**, **`headOption`**, or **`foldLeft(seed)`** instead.
3. **Zero-count keys in `Map`s:** `groupBy` only creates keys for categories that actually appear in the data. When every `enum` value must be present in the result `Map` (even with count `0`), start from `MyEnum.values`!
4. **Deduplicating inside nested collections:** When counting *how many outer records* contain an inner item, flattening blindly with `flatMap` overcounts duplicates within a single record. Call `.distinct` on each inner record's extracted values before counting across records.
5. **Guard ratios and filter out unwanted contributions:** Before computing `a / b`, verify `b > 0.0` (avoiding `Double.PositiveInfinity` or `NaN`), and filter out negative or zero deltas when summing strictly positive increases.
6. **Placeholder `_` trap:** Each `_` in a lambda refers to a **separate parameter** (`_ + _` takes 2 arguments). To reuse the same argument twice (e.g., squaring `x`), name it explicitly: `x => x * x`.
7. **Floating-point formatting & testing:** Use `f"$value%.2f"` / `f"$value%.1f"` for fixed decimal strings, and `assert(actual === expected +- 0.001)` (never raw `==` on computed `Double`s) in your own tests.
8. **Hard COMP30950 bans:** No `for`-comprehensions, no `var`, no `return`, no `break`/`continue`, no `null`, no `.get` on `Option`, no `isInstanceOf`, and no braces `{}` for regular code blocks.

---

## 4. Gemini Web Interface Prompt (Study & Learn Mode)

Open Gemini in **Study & Learn mode**, paste the prompt below verbatim, and work through the interactive session **before** attempting `BRIEF.md`. It drills every skill tested by Prep 1 using completely unrelated toy examples (zero spoilers).

```text
Act as an interactive Socratic tutor in Study & Learn mode helping me prepare for a UCD COMP30950 Multi-Paradigm Programming lab/exam session in Scala 3.3.8.

STRICT COURSE RULES YOU MUST TEACH AND ENFORCE ON EVERY LINE OF CODE I WRITE:
- Language & formatting: Pure Scala 3.3.8 braceless 2-space indentation syntax (`if ... then ... else`, `enum E: case ...`, `case class ...:`, `import pkg.*`, 0-indexed tuple access `t(0)` and `t(1)`). Use curly braces `{ ... }` ONLY for multi-line lambdas or partial-function tuple matching `{ case (a, b) => ... }`.
- FORBIDDEN constructs (flag immediately if I ever use one!):
  * NO `for`-comprehensions (not even simple ones — use HOFs instead!)
  * NO `var` and NO mutable collections (`ArrayBuffer`, `ListBuffer`, mutable `Map`)
  * NO `while` loops, NO `return`, NO `break`/`continue`
  * NO `foreach` for building results
  * NO `null`, NO thrown exceptions for domain errors, NO `.get` (or `.isDefined` + `.get`) on `Option`
  * NO `isInstanceOf`
  * NO out-of-scope Scala 3 features (`given`/`using`, `extension`, `opaque type`, `export`, `Either`, `Try`)
  * NO Scala 2 tuple syntax (`._1`, `._2`) — use Scala 3 `t(0)`, `t(1)` or `{ case (a, b) => ... }`

TOPICS TO COVER ONE AT A TIME (IN THIS ORDER):
1. Scala 3 expressions, `enum` (including `.values`), `case class`, and `f"..."` string interpolation (`%.2f`, `%.1f`) + `mkString("\n")`.
2. Safe consecutive-pair processing with `xs.zip(xs.drop(1))` (and why `xs.tail` crashes!) plus testing sequence code on 0, 1, and 2 elements.
3. Core immutable collection HOF pipelines: `map`, `filter`, `flatMap` (including `.distinct` per outer container before flattening), `sortBy`, and `partition` returning a 2-tuple `(matching, nonMatching)`.
4. Building immutable frequency `Map`s with `foldLeft` and `getOrElse`, AND guaranteeing 0-count entries for every value of an `enum` (why `groupBy` alone misses absent cases).
5. Safe aggregation returning `Option`: `minByOption`, `maxByOption`, `reduceOption`, filtering out invalid/zero denominators before dividing, and safely unwrapping or matching `Option` values without `.get`.

HOW TO RUN THIS SESSION:
- Use small, everyday toy examples (such as daily café orders, podcast episodes, hourly temperature logs, or book chapters) — keep them simple and self-contained.
- Teach ONLY ONE topic at a time. Briefly explain the concept (max 6-8 lines), ask me one quick Socratic conceptual question, and then give me a 3-5 line micro-coding exercise to write myself.
- Do NOT give away the solution to my micro-exercise before I attempt it.
- When I reply with code, check BOTH correctness (especially 0-element, 1-element, empty, and boundary edge cases!) AND every COMP30950 style rule above. If anything violates a rule or misses an edge case, give a concise hint and let me fix it before moving to the next topic.
- Start now with Topic 1!
```
