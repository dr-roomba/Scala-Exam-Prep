# Prep 3 Study Guide — Cinema Ticketing, Pricing & Screening Workflows

Read this guide and review the listed lecture slides **before** opening `BRIEF.md` or writing code.

---

## 1. Where This Fits in the Progression

| Prep | Focus | Key Concepts |
|---|---|---|
| **Prep 1** (`prep1_trail_guide`) | Functional foundations | Scala 3 expressions, `enum`, `case class`, immutable collections, HOF pipelines, `zip(drop(1))`, `Double` tolerance |
| **Prep 2** (`prep2_parcel_locker`) | Encapsulation & `Option` architecture | Classes, Uniform Access, companion `apply`, smart constructors, `Option` chaining, Functional Core / Imperative Shell, single-pass `foldLeft` |
| **Prep 3** (`prep3_cinema_pipeline`) **(You are here)** | **OO + FP Design Patterns** | **Inheritance (`super`, `sealed`/`final`/`protected`), Subtype Polymorphism (no `isInstanceOf`), Template Method, Traits & Mixins (`Ordered[T]`, capability traits), Singleton Null Object, FP Strategy (`type` alias + lambdas), predicate HOFs, optional `Option[T => Unit] = None` callbacks + lexical Closures** |
| **Prep 4** (`prep4_warehouse_tree`) | Recursive ADTs & Algorithms | Recursive `sealed trait` trees, structural `List` recursion, greedy `Option[Map[...]]` allocation, stable multi-key `sortBy` |
| **Prep 5** (`prep5_mock_exam_cloud_scheduler`) | Full 3-Hour Mock Exam | Timed synthesis of every archetype in an authentic 2026 exam paper |

**What Prep 3 carries forward from Prep 1–2:** immutable `case class` value objects, `Option[T]` for validation failures without exceptions, HOF collection pipelines (`map`, `flatMap`, `find`, `minOption`, `partition`), clean visibility (`private var` behind read-only `def`), and `+- 0.001` floating-point testing.

**What Prep 3 introduces:** structuring extensible OO hierarchies cleanly with **Template Method** and **Traits**, eliminating `null`/`Option` checks with the **singleton Null Object** pattern, replacing heavyweight single-method strategy classes with **FP Strategy lambdas + `type` aliases**, and decoupling side-effecting observers with **optional callbacks & closures**.

---

## 2. Suggested Lecture Slides to Read

All filenames match `d:\Studying\Scala Exam\Lecture Slides\` verbatim:

1. **`07_Inheritance.pdf`** (36 slides)
   - **Slides 2–5:** The "is-a" test (necessary, not sufficient).
   - **Slides 6–12:** `extends`, dynamic method binding, **superclass constructor forwarding** (s11 — why forwarded parameters must NOT have `val`/`var`), subtype typing (s12).
   - **Slides 13–18:** Compulsory `override` on concrete methods (s18), **`super` refinement** (s14), overloading vs overriding (s16), overriding a parameterless `def` with a `val` (s17).
   - **Slides 19–23:** Modifiers `final`, `open`, `sealed` (*"For COMP30950 assignments, all classes can be sealed"*, s20), `private` vs `protected`, and abstract classes (s21–23, previewing Template Method on s23).
   - **Slides 24–29:** `BankAccount` -> `SavingsAccount` -> `SSA` refining `withdraw` via `super`.
   - **Slides 30–35:** Composition over Inheritance (`Robot`/`RobotArm`, `Polygon`/`Rectangle`).

2. **`08_TypeSystems.pdf`** (22 slides)
   - **Slides 11–13:** Why static typing rejects subclass-only calls on a supertype reference — lift behavior to the supertype instead of downcasting.
   - **Slides 18–21:** `class == type + implementation`, static type vs dynamic type (s19), and the Scala 3 Type Hierarchy (`Any` -> `Matchable` -> `AnyVal`/`AnyRef` -> `Null` [avoid!] -> `Nothing`, s20–21).

3. **`10_IntroToPolymorphism.pdf`** (11 slides)
   - **Slides 2–5:** "Single interface, many forms"; ad hoc vs parametric vs subtype polymorphism.
   - **Slides 7–10:** Generics (`Box[A]`), upper type bounds (`[A <: Ordered[A]]`, s9), conceptual `List[+A]` covariance (variance annotations explicitly out of scope, s10).

4. **`10_SubtypePolymorphism.pdf`** (28 slides)
   - **Slides 2–11:** Three pillars of subtype polymorphism — polymorphic references (s5–6), dynamic binding (s7–8), method overriding (s9–11).
   - **Slides 12–16:** Polymorphic collections, message passing, and **message send to `self`** (s16 `TaxPayer` — the exact dispatch mechanism behind Template Method).
   - **Slides 17–18:** Two classic anti-patterns — explicit `isInstanceOf[...]` checks (s17) and behavior-switching type tags (s18).
   - **Slides 19–27:** Fragile Base Class Problem (s25–26) and well-designed hierarchies / LSP (s27).

5. **`11_TemplateMethod.pdf`** (25 slides)
   - **Slides 2–8:** Overriding for refinement (`Manager.weeklyPay = super.weeklyPay + bonus`, s4) and abstract method contracts.
   - **Slides 9–18:** Template Method pattern (`GamePlayer` -> `ChessPlayer`, self-dispatch / Inversion of Control, s18).
   - **Slides 19–21:** **Three types of helper methods** — abstract (mandatory, s19), `final` (fixed + making the template method itself `final`, s20), and `protected` **hook methods** (optional default, s21).
   - **Slides 22–24:** Business-rule workflow enforcement (`Vehicle.applyNctTest`).

6. **`12_Traits.pdf`** (23 slides)
   - **Slides 2–9:** Traits as interfaces and capability mixins; Scala 3 trait constructors (s7); **traits combining abstract + concrete members** (`Showable.showHtml`, s8–9).
   - **Slides 10–14:** Multiple inheritance, the Diamond Problem, and Scala trait linearisation (rightmost trait checked first, s14).
   - **Slides 15–17:** Scala 3 comma-separated multi-trait mixins (`extends HasLegs, HasTail`, s16) and singleton objects extending traits (`object Bran extends ...`, s17).
   - **Slides 18–22:** Thin vs Rich interfaces & **`Ordered[A]`** (s20–21 — implementing `compare` unlocks `<`, `>`, `<=`, `>=`, and `.sorted`); Traits vs Abstract Classes (s22).

7. **`14_Options and Null Objects.pdf`** (23 slides)
   - **Slides 5–15:** Quick refresher on `Option`, avoiding `.get`, returning `Option` for failure, and `flatMap`.
   - **Slides 17–22:** **The Null Object Design Pattern** (`Pet` / `NullPet` -> **singleton `object NullPet extends Pet`**, s22, including the `.length` collection-counting caveat).

8. **`17_Lambdas in Software Design.pdf`** (23 slides)
   - **Slides 2–15:** Decoupling with callbacks (`Alarm` -> `Wakeable` -> `Timer(alertFunc: () => Unit)`), **formal Closures** (first-class function + free variables bound in lexical environment, capturing private state without polluting public APIs, s12–14).
   - **Slides 16–22:** **Strategy Pattern** — OO trait hierarchy (s17–19) vs **FP `type` alias + lambdas** (s20–22).
   - **Slide 23:** **Lambda vs Trait decision rule**.

9. **Quick lookup — `19_Lists.pdf` Slide 12:** `partition(p)` returning `(matching, nonMatching)` in one pass while preserving order.

---

## 3. Key Rules & Pitfalls to Keep in Mind

1. **NEVER use `isInstanceOf[...]` (or behavior-switching type tags):** If a method behaves differently across subtypes, put an abstract or hook method on the supertype and override it — let dynamic dispatch decide (`10_SubtypePolymorphism.pdf` s17–18).
2. **NEVER put `val` or `var` on forwarded superclass constructor parameters:** Write `class Sub(x: Int, val y: Int) extends Base(x)`, NOT `class Sub(val x: Int, ...) extends Base(x)` (`07_Inheritance.pdf` s11). Putting `val` on a forwarded param causes accidental overriding/shadowing.
3. **`override` rules:** Compulsory when overriding a **concrete** method (like a `protected` hook or a superclass concrete method); optional (not needed) when implementing an **abstract** method (`07_Inheritance.pdf` s18, s21).
4. **Template Method discipline:** Mark the template method itself **`final`** so subclasses cannot bypass invariant steps; make mandatory variation points **abstract** (`protected`), invariant steps **`final`**, and optional steps **`protected` hooks** with sensible defaults (`11_TemplateMethod.pdf` s19–21).
5. **Singleton Null Object:** A stateless Null Object should be a singleton **`object`** extending the service trait/class — never allocate fresh instances on every default call (`14_Options and Null Objects.pdf` s22). Beware that placing Null Objects inside a `List` still counts them in `.length`.
6. **Lambda vs Trait decision rule (`17_Lambdas in Software Design.pdf` s23):**
   - Choose a **lambda + `type` alias** when a strategy or callback is a single, stateless function (lightweight, composable, supports ad-hoc inline strategies).
   - Choose a **trait / abstract class** when behavior requires multiple coordinated methods, shared state, or restricted subtypes.
7. **Closures & Encapsulation:** A closure passed as a callback can read and update its enclosing class's **`private`** fields without exposing any public setters or forcing the service class to know about the caller (`17_Lambdas in Software Design.pdf` s12–14). Use `private var` + read-only `def` accessors, never public `var`s.
8. **Backward-compatible `Option` callbacks:** Adding `callback: Option[T => Unit] = None` with a default preserves all existing callers. Invoke it safely via `callback.foreach(cb => cb(value))` — **never** `.get` or `if callback.isDefined then callback.get(...)`.
9. **Safe `compare` & `Ordered[T]`:** Return `< 0`, `0`, or `> 0`. Prefer `a.compare(b)` over `a - b` (avoids overflow) and `s1.compareTo(s2)` for lexicographical string tie-breaks.
10. **Strict COMP30950 bans:** Pure Scala 3.3.8 braceless 2-space indentation; NO `for`-comprehensions, NO `return`, NO `break`/`continue`, NO `null`, NO thrown exceptions for domain errors, NO `.get` on `Option`, NO `var` in case classes, NO out-of-scope Scala 3 features (`given`/`using`, `extension`, `opaque`, `export`, `Either`, `Try`, stackable `abstract override` traits).

---

## 4. Gemini Web Interface Prompt (Study & Learn Mode)

Copy and paste the prompt below into **Gemini** with **Study & Learn mode** enabled before starting Prep 3. It drills every concept needed for this assignment using unrelated practice examples with zero spoilers.

```text
You are my interactive Scala 3 tutor in Study & Learn mode for UCD COMP30950 (Multi-Paradigm Programming), taught by Mel Ó Cinnéide. I want to master OO & FP design patterns in Scala 3.3.8 before attempting a timed programming exercise.

STRICT COURSE RULES (enforce these in every example you show and every exercise I submit):
- Language: Scala 3.3.8 ONLY, using braceless 2-space indentation syntax (`:`, `if ... then ... else`, comma-separated trait mixins `extends T1, T2`, universal apply without `new`). Use `{ ... }` ONLY for multi-line lambdas or partial functions.
- NEVER use or accept: `for`-comprehensions, `return`, `break`/`continue`, `null`, thrown exceptions for domain failures, `.get` on `Option`, `var` in `case class`, `isInstanceOf` or type-tag conditionals.
- Stay strictly within course scope: DO NOT mention or use `given`/`using`, `extension` methods, `opaque type`, `export`, `Either`, `Try`, variance annotations (`+A`/`-A`), or stackable `abstract override` traits.
- Prefer `val`, immutable collections (`List`, `Vector`), HOFs (`map`, `flatMap`, `partition`, `minOption`, `foreach` only for true side effects), `Option`, `sealed`/`final`/`protected`, and concise self-documenting code.

HOW TO TEACH ME (Study & Learn Mode):
- Teach ONE concept at a time in the exact order listed below.
- For each concept:
  1. Give a crisp 4–6 line explanation using a tiny toy example from an unrelated domain (such as music tracks, subscription plans, document exporters, quiz graders, or log/file scanners).
  2. Ask me ONE short Socratic question OR give me a 3–6 line micro-coding exercise to write myself.
  3. Wait for my answer before continuing.
  4. When I answer, critique BOTH correctness AND COMP30950 style (flag any `val` on forwarded constructor params, missing `override`, `isInstanceOf`, `.get`, braces, `return`, or `for`-comprehensions).
  5. Only move to the next concept once I demonstrate clear understanding.

CONCEPT SEQUENCE TO COVER:
1. Inheritance & Refinement: `sealed abstract class`, forwarding superclass constructor params WITHOUT `val`/`var`, compulsory `override` on concrete methods vs optional on abstract methods, `protected` visibility, and refining a superclass method using `super` (toy example: a `SubscriptionPlan` hierarchy).
2. Subtype Polymorphism & Dynamic Self-Dispatch: polymorphic collections, why `isInstanceOf` is an anti-pattern, and how a concrete superclass method calling an abstract/overridden method dispatches dynamically to the runtime subclass.
3. The Template Method Pattern: `final` template method orchestrating invariant (`final`), mandatory (`abstract`), and optional (`protected` hook with default) steps (toy example: a `DocumentExporter` with validation, formatting, optional watermark hook, and logging).
4. Traits, Comma Mixins & `Ordered[T]`: capability traits with both abstract members and concrete derived methods, mixing multiple traits via Scala 3 `extends T1, T2`, and mixing in `Ordered[T]` with `compare` (including a tie-breaker) so `.sorted`, `<`, and `>` work automatically (toy example: `Song` mixing in `Ordered[Song]`, `Playable`, `Downloadable`).
5. Singleton Null Object Pattern: using `object QuietNotifier extends Notifier` as a default parameter instead of `null` or `Option` checks, plus when Null Object has caveats (e.g., `.length` in collections).
6. Functional Strategy Pattern & Predicate Partitioning: replacing single-method strategy classes with a `type` alias (`type ScoreAdjuster = (Int, Double) => Double`), factory functions returning lambdas, a higher-order combinator returning a combined lambda (handling an empty list safely with `minOption.getOrElse`), plus `List.partition` for splitting by a predicate `T => Boolean` (toy example: a quiz grader).
7. Backward-Compatible Optional Callbacks & Lexical Closures: adding `onMatch: Option[String => Unit] = None` to an existing method without breaking 1-arg callers, invoking it safely without `.get`, and passing a closure from a tracker class that captures and updates its `private var` fields without exposing setters or recomputing from the returned list (toy example: a `FileScanner` and `WarningTracker`).

Start now with Concept 1 only. Keep your first message concise and end with a question or micro-exercise for me.
```
