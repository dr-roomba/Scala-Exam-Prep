# Study Guide — Prep 2: Smart Parcel Locker Network

Use this guide **before** opening the source files to review the exact lecture slides, internalise COMP30950's rules for these topics, and run a zero-spoiler interactive warm-up in Gemini's **Study & Learn** mode.

---

## 1. Where This Fits in the Progression

- **Builds directly on Prep 1 (`prep1_trail_guide`)**:
  - Scala 3.3.8 braceless 2-space syntax, `enum`, basic `case class`, `Vector`/`List`/`Map`/`Tuple` (0-indexed `t(0)`), HOF pipelines (`map`, `filter`, `flatMap`, `foldLeft`, `exists`, `find`, `minByOption`), `Double` tolerance (`+- 0.001`), and empty/boundary edge-case testing.
- **New COMP30950 Concepts Introduced in Prep 2**:
  - **Classes, Primary Constructors & Uniform Access Principle** (parameterless computed `def` vs `val`, zero-arg mutator `()` convention, class-based privacy).
  - **Companion Objects & the `apply` Method** (companion factory `apply` vs instance lookup `apply`, mutual `private` visibility).
  - **Smart Constructors** (`private` primary constructor + companion `apply(...): Option[T]` — and protecting `.copy` in Scala 3.3.8!).
  - **`Option` Chaining & Safe CSV Parsing** (`map` vs `flatMap` vs `getOrElse`, `toDoubleOption`, zero `.get` and zero exceptions).
  - **Single-Pass `foldLeft` with Multi-Field Accumulators** & sequential batch folding over immutable state.
  - **Functional Core, Imperative Shell** (pure immutable domain state transitions returning `Option` wrapped by a thin stateful OO facade following *Tell, Don't Ask*).

---

## 2. Suggested Lecture Slides to Read

All filenames live in `d:\Studying\Scala Exam\Lecture Slides\` (and are bundled into `module_slides.pdf` in the real exam):

| Priority | Exact Lecture Slide PDF | Slides to Focus On | What to Look For |
|---|---|---|---|
| **Core** | `06_Classes.pdf` | **Slides 2–10, 12–17** | Mutator `()` convention (s2–3); Uniform Access Principle (s4–6); primary constructor `val`/`var`/plain params (s8–10); mutable → **immutable `BankAccount` returning `Option`** (s12–16); **Functional Core, Imperative Shell** (s17). |
| **Core** | `06a_Objects.pdf` | **Slides 2–4, 7–9** | Singleton `object` caveats (global mutable state, s4); **Companion Objects** replacing Java `static`, same name & same file, mutual `private` access (s7–9). |
| **Core** | `06b_TheApplyMethod.pdf` | **Slides 2–10** | `apply` desugaring (s2–3); companion constructor proxies & Scala 3 auto-synthesis caveat (s5–7); **companion `apply` vs instance `apply`** (`Array(1,2,3)` vs `numbers(1)`, s8); custom factory `apply` + `private` primary constructor (s9–10). |
| **Core** | `14_Options and Null Objects.pdf` | **Slides 5–15** | `Option`/`Some`/`None` basics (s5–6); why `.isDefined`+`.get` and raw `.get` are bad (s7); `getOrElse` without exceptions (s8); `match` (s9); `Option` as 0-or-1 collection with `map`/`flatten`/`flatMap` (s13–14); **validated smart constructor** `private` ctor + `apply: Option[T]` (s15). |
| **Core** | `18_Case_classes_tutorial(handout).pdf` | **Slides 11–14, 16** | **Step 4 — Smart Constructors** (`case class Amount private`, `isFinite && > 0`, s11); **`Option.map` vs `Option.flatMap`** (s12); **safe CSV parsing** (`split`, `trim`, `toDoubleOption`/`toLongOption`, s13–14); running accumulation with `foldLeft` (s16). |
| **Refresher** | `04_CollectionClasses.pdf` | **Slides 7, 11–16, 18** | `Vector` `.updated` (s7); immutable `Map` `.get`/`+`/`getOrElse` (s11–14, 16); **`val`/`var` vs object mutability** (s15); Scala 3 0-indexed tuples `t(0)` (s18). |
| **Refresher** | `05_Lambdas and Collection Processing.pdf` | **Slides 16–17** | Curried `foldLeft` when the accumulator has a different type from the elements. |

---

## 3. Key COMP30950 Rules & Pitfalls to Keep in Mind

1. **Smart Constructors & `.copy` in Scala 3.3.8**:
   - Declaring `case class Foo private (x: Int)` blocks `new Foo(-1)` from outside, **but in Scala 3.3.8 the auto-generated `.copy` method is still public unless you also declare `private def copy(...)`!**
   - Notice `parcel.scala` already declares `private def copy` for you — never delete it, and always check how `copy` behaves if you write your own smart-constructor case classes on the exam!
2. **Never Call `.get` on an `Option`** (even after checking `.isDefined`):
   - Combine fallible steps with `.map`, `.flatMap`, `.getOrElse`, or `match`.
   - Use `.map` when your function returns a plain value `B`; use `.flatMap` when your function itself returns an `Option[B]` (avoiding `Option[Option[B]]`).
3. **`val`/`var` vs Object Mutability (`04_CollectionClasses.pdf` s15)**:
   - `val` locks the *reference*, not the object inside; `var` allows reassigning the reference to a *new immutable value*!
   - In **Functional Core, Imperative Shell**, the facade holds `private var state: Map[...]` pointing to an **immutable** `Map` of **immutable** domain objects — reassigning `state = state + (k -> updated)` replaces the reference safely without ever mutating any shared domain object!
4. **Companion `apply` vs Instance `apply` (`06b_TheApplyMethod.pdf` s7–8)**:
   - Defining a manual companion object can suppress Scala 3's auto-synthesised constructor proxy unless you define `apply` on the companion object (or use `new`).
   - Calling `ClassName(...)` invokes the **companion object's** `apply`; calling `instance(...)` invokes the **instance's** `apply`.
5. **Single-Pass `foldLeft` & Tie-Breaking**:
   - When asked for a single-pass `foldLeft`, keep all intermediate values in a single tuple accumulator (e.g. `(count, sum, bestOpt)`).
   - Never assume an input `Vector` is sorted by ID — explicitly compare tie-break keys inside the fold!
6. **CSV `split(",")` Gotcha**:
   - In Java/Scala, `"A,1,2,".split(",")` silently strips trailing empty strings and produces 3 elements! Use `line.split(",", -1)` so trailing commas produce 4 elements and are properly rejected.
7. **Tell, Don't Ask (`SoftwareQuality.pdf` #8)**:
   - Do not reach inside a domain object's internal collections from the manager facade. Put small focused query/transition methods on the domain object itself and delegate to them from the facade.

---

## 4. Gemini Web Interface Prompt (Study & Learn Mode)

Copy the prompt below into **Gemini** with **Study & Learn mode** enabled **before** you start coding Prep 2. It drills every technique needed for this assignment using unrelated toy examples — zero spoilers!

```text
You are my interactive study tutor in Study & Learn mode for UCD COMP30950 (Multi-Paradigm Programming), taught in Scala 3.3.8.

GOAL:
Prepare me for a programming exercise covering:
1. Scala 3 classes, primary constructor parameter visibility (`val`, `var`, plain param, `private`), and the Uniform Access Principle
2. Companion objects and the difference between companion `apply` (factories) and instance `apply` (function-like lookup)
3. Smart constructors (`private` primary constructor + `private def copy` in Scala 3.3.8 + companion `apply(...): Option[T]`)
4. Idiomatic `Option` composition (`map` vs `flatMap` vs `getOrElse`, safe CSV line parsing with `split(",", -1)` and `toDoubleOption`/`toIntOption`)
5. Immutable core state transitions returning `Option[State]` or `Option[(Result, State)]`
6. Single-pass `foldLeft` with multi-field tuple accumulators (including tie-breaking independent of input order) and sequential batch folding over `List[String]`
7. Functional Core, Imperative Shell architecture and Tell, Don't Ask (immutable core wrapped by a thin stateful manager with a single `private var` and a private `update` helper)

STRICT COURSE STYLE RULES (enforce these on BOTH your examples and MY answers!):
- Pure Scala 3.3.8 braceless 2-space indentation syntax (`if ... then ... else`, `match`, `class Foo:`, `object Foo:`). Only use `{ ... }` for multi-line lambdas or `{ case (a, b) => ... }` tuple pattern matching.
- NEVER use `for`-comprehensions, `return`, `break`/`continue`, `null`, thrown exceptions for domain/parsing errors, `.get` / `.isDefined + .get` on `Option`, or `isInstanceOf`.
- NEVER put `var` inside a `case class`, and keep domain classes 100% immutable.
- NEVER use out-of-scope Scala 3 features (`given`/`using`, `extension`, `opaque type`, `export`, `Either`, `Try`, union types `A | B`).
- Prefer `val`, immutable `Vector`/`List`/`Map`, HOFs, 0-indexed tuples `t(0)`, and `Double.isFinite`.

HOW TO RUN THIS SESSION (Study & Learn Mode):
- Use small, everyday, UNRELATED toy examples (e.g., `Temperature`, `Username`, `LibraryBook`, `VendingMachine`, `CoffeeOrder`, `GradeBook`).
- Teach ONE concept at a time (in order 1 through 7).
- For each concept:
  a) Give a crisp 4–6 line explanation with one tiny Scala 3 example.
  b) Ask me 1 short conceptual check question OR give me a 3–6 line micro-coding exercise to write myself.
  c) Wait for my answer before moving on!
  d) If I violate any COMP30950 rule (e.g., using `.get`, a `for`-comprehension, `return`, braces, or reaching into internals instead of Tell, Don't Ask), point out why and ask me to fix it.
- After Topic 7, give me one short 10-line synthesis drill combining smart-constructor parsing + `foldLeft` + immutable transition + thin stateful manager.

Start now with Topic 1 only.
```
