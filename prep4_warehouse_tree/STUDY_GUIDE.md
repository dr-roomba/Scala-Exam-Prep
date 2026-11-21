# Study Guide — Prep 4: Recursive Warehouse Logistics & Dispatch Planner

Complete this quick review **before** opening `src/main/scala/` or `BRIEF.md`.

---

## 1. Where This Fits in the Progression

- **Builds on Prep 1–3:**
  - From **Prep 1**: immutable collections (`List`, `Vector`, `Map`, `Tuple`), core HOFs (`map`, `filter`, `flatMap`, `foldLeft`, `sortBy`, `partition`), `Double` tolerance (`+- 0.001`), and 0/1/2-element edge-case testing.
  - From **Prep 2**: safe `Option` returns (`Some`/`None`, never `.get`) and the **Functional Core, Imperative Shell** separation.
  - From **Prep 3**: subtype polymorphism via `abstract class` / `trait`, passing predicate lambdas (`T => Boolean`), and keeping existing tests passing when extending a codebase.
- **New Core Skills Mastered in Prep 4:**
  1. **Recursive `sealed trait` ADTs** (Composite/Tree data models following the **Case Class Leaf Rule**).
  2. **Nested recursive helper functions + `foldLeft` tree traversals** without unnecessary intermediate lists (the exact `HotelServices` style from the 2025 exam).
  3. **Top-level vs all-depths tree semantics** (a recurring exam trap: knowing when to inspect only top-level children vs recursing through every nested container).
  4. **Explicit structural `List` recursion** with `Nil` and `head :: tail`, using `O(1)` prepend + `.reverse` instead of slow appends or array-style indexing.
  5. **Greedy `Option[Map[String, T]]` optimization** via `sortBy` + `zip.toMap` over a `sealed` ADT.
  6. **Multi-key stable `sortBy`** using tuple keys on a second `sealed` ADT, paired with **open polymorphic `abstract class` sinks** (the **Matching vs Polymorphism** design synthesis from Deck 18).
- **Leads directly into Prep 5:** Prep 5 is a full 3-hour mock exam combining Prep 1–4 (tree ADTs, smart constructors, greedy `Option[Map]` scheduling, Template Method, and closures) under strict exam conditions.

---

## 2. Suggested Lecture Slides to Read

All filenames below are in `d:\Studying\Scala Exam\Lecture Slides\`:

### Primary Decks (Read Carefully)
1. **`13_CaseClasses.pdf`** *(40 slides)*
   - **Slides 2–11:** Case classes, immutability (never `var`), structural equality, `copy`, `unapply`.
   - **Slides 13–20:** Pattern matching syntax, guards (`if ...`), type patterns (`case p: Foo`, avoiding the `case Foo` pitfall on s18), deconstruction.
   - **Slides 22–25:** `sealed trait` exhaustiveness checking and the **Case Class Leaf Rule** (s25: case classes must be concrete leaves; never extend a case class from another case class).
   - **Slides 26–34:** Recursive tree structures (`Item`/`Article`/`Bundle`), combining matching + `foldLeft`/`flatMap` + recursion, standalone function convention (s33).
   - **Slides 35–40:** OO Polymorphism vs Case Classes & Pattern Matching (when to choose `sealed` ADTs).

2. **`18_Case_classes_tutorial(handout).pdf`** *(23 slides)*
   - **Slides 4–9:** `sealed trait` with shared members (`account`, `timestamp`, `amount`); **match only where subtypes differ** (s8).
   - **Slide 10:** Exhaustiveness checking and **the `case _` trap** — why a wildcard silently defeats compiler warnings when a new subtype is added.
   - **Slides 15–16:** Running totals with `foldLeft` (no `var`/loop) and the golden rule: **"Test list code on 0, 1 and 2 elements."**
   - **Slides 17–19:** Second sealed ADT family (`Flag`) and deterministic **multi-key stable `sortBy`** with tuple keys `(key1, rank(f))` (s19).
   - **Slides 20–23:** Unsealed polymorphic `abstract class` handlers (`ConsoleHandler` / `MemoryHandler`, s20) and the **Matching vs Polymorphism** rule (*"Matching varies behaviour by what the data is; polymorphism by who does the work"*, s21 & s23).

3. **`19_Lists.pdf`** *(20 slides)*
   - **Slides 2–5:** Immutable singly-linked `List`, `Nil`, right-associative `::`, `head`/`tail`/`isEmpty`.
   - **Slides 6–7:** Structural `List` recursion with `case List()` / `case Nil` and `case y :: ys` (`isort` example).
   - **Slides 8–9:** `:::`, `reverse` (prepend + `reverse` is $O(n)$), why `last`/`init`/`length` are linear, and **never index a `List` like an `Array`** (s9).
   - **Slides 10–14:** `flatMap`, `partition(p)` returning `(matching, nonMatching)` (s12), `find`, `span`, `forall`, `exists`.
   - **Slides 15–17:** Sorting (`sortWith`) and lockstep pairing with `zip` (s16–17).

### Quick Refreshers (Skim if Rusty)
4. **`05_Lambdas and Collection Processing.pdf`** *(23 slides)* — **Slides 16–21** (`foldLeft` avoiding intermediate lists, `flatten`, `flatMap`).
5. **`14_Options and Null Objects.pdf`** *(23 slides)* — **Slides 5–11** (`Option`/`Some`/`None`, returning `Option` on failure, never calling `.get`).

---

## 3. Key Rules & Pitfalls to Keep in Mind

1. **The `case _` Exhaustiveness Trap (`18_Case_classes_tutorial(handout).pdf`, s10):**
   On a `sealed trait`, writing `case _ => ...` turns off Scala's compile-time exhaustiveness warning. Always match every subtype explicitly (e.g., `case _: TypeA => ...` and `case _: TypeB => ...`) so the compiler catches missing cases.
2. **Type-Pattern Syntax Pitfall (`13_CaseClasses.pdf`, s18):**
   Write `case _: Subtype` or `case s: Subtype` (or deconstruct `case Subtype(x, y)`). Writing bare `case Subtype =>` matches the *companion object*, NOT instances of the class!
3. **Top-Level vs All-Depths Tree Recursion:**
   Read the specification carefully for every tree function:
   - Does it ask for **only top-level composites** (inspecting `root.children` directly, recursing *inside* each top-level composite only to aggregate its value)?
   - Or does it ask for **every composite at all depths** (recursing into child composites and collecting issues/results from both outer and inner composites)?
4. **Avoid Intermediate Lists with `foldLeft`:**
   For numeric sums over recursive trees, prefer `children.foldLeft(0.0)((acc, child) => acc + ...)` over `children.map(...).sum` (which allocates a temporary list at every tree node).
5. **Idiomatic Structural `List` Recursion (`19_Lists.pdf`, s6–9):**
   - Pattern match on `Nil` and `head :: tail`.
   - Accumulate items in reverse using `head :: acc` ($O(1)$) and call `.reverse` when closing a group, rather than appending with `:+` ($O(n)$ per step).
   - Never treat a `List` like an `Array` (`xs(i)` is $O(n)$).
   - Make sure every recursive call strictly advances to `tail` (or clearly decreases the input size) so termination is guaranteed.
6. **Boundary Conditions (`<=` vs `<`, Empty Inputs):**
   Double-check exact equality boundaries (`<=` limit fits vs `>` limit overflows) and test every function on `0`, `1`, and `2` elements, empty containers, and nested containers.
7. **Matching vs Polymorphism (`18_Case_classes_tutorial(handout).pdf`, s20–21):**
   - Put properties shared by every subtype directly on the `sealed trait` (no `match` needed to read them).
   - Use `sealed trait` + exhaustive `match` for closed data families where operations vary by *what the data is*.
   - Use an **unsealed** `abstract class` (or `trait`) for open behavioural consumers/sinks where new implementations in other files vary *who does the work*.
8. **Hard COMP30950 Rules:**
   - Pure Scala 3.3.8 braceless 2-space indentation; flat default package.
   - **Never use:** `for`-comprehensions, `return`, `break`/`continue`, `null`, thrown exceptions for domain failures, `.get` on `Option`, `var` in case classes, `isInstanceOf`, `given`/`using`, `extension`, `opaque`, `export`, `Either`, or `Try`.

---

## 4. Gemini Web Interface Prompt (Study & Learn Mode)

Copy and paste the prompt below into **Gemini** with **Study & Learn mode** enabled **before** starting Prep 4. It drills every technique you need using unrelated toy examples — with zero spoilers for the assignment.

```text
You are my interactive Scala tutor in Study & Learn mode for UCD's COMP30950 Multi-Paradigm Programming module. I want to master recursive sealed ADTs, tree traversals, structural List recursion, greedy Option[Map] pairing, stable multi-key sorting, and the Matching vs Polymorphism design tradeoff BEFORE attempting a practice exam.

HARD LANGUAGE & STYLE RULES (enforce strictly on BOTH your examples and my answers):
- Use ONLY Scala 3.3.8 braceless 2-space indentation syntax (`sealed trait X:`, `if ... then ... else`, `match` without outer braces, 0-indexed tuples `t(0)`). Braces `{ ... }` are only allowed for partial-function tuple lambdas `{ case (a, b) => ... }`.
- NEVER use or accept: `for`-comprehensions, `return`, `break`/`continue`, `null`, thrown exceptions for domain errors, `.get` on `Option`, `var` inside case classes, `var`/`while` loops in pure functional code, `isInstanceOf`, or out-of-scope Scala 3 features (`given`/`using`, `extension`, `opaque type`, `export`, `Either`, `Try`).
- ALWAYS enforce: `val`, immutable `List`/`Vector`/`Map`, `Option`/`Some`/`None`, exhaustive pattern matching over `sealed trait`s (flag any catch-all `case _` on a sealed trait as a style bug!), `foldLeft` instead of `map(...).sum` when avoiding intermediate lists, and `Nil` / `head :: tail` for structural List recursion.

HOW TO TEACH ME (Study & Learn Mode):
- Teach ONE topic at a time in the order below. Do NOT dump all explanations at once.
- For each topic:
  1. Give a concise 4–6 line conceptual explanation with a tiny unrelated toy example (use toy domains like a filesystem `FsNode` (`File`/`Folder`), recipe steps, sports teams, or alert notifications — NEVER use warehouses, pallets, shipping, trucks, hotels, card games, chess, buses, or sensors).
  2. Ask me 1 quick Socratic check question OR give me a 3–6 line micro-coding exercise to write myself.
  3. Wait for my answer. Critique BOTH correctness AND COMP30950 style (flagging any `case _`, `var`, `for`, `return`, `.get`, list indexing, or intermediate-list allocation). Do not move to the next topic until I get it right.

TOPICS TO COVER IN ORDER:
1. Recursive `sealed trait` ADTs (Leaf Rule, shared trait fields, avoiding the `case _` exhaustiveness trap and the `case Foo` vs `case _: Foo` type-pattern pitfall).
2. Recursive tree traversals with nested helper functions and `foldLeft` — specifically contrasting (a) aggregating ONLY top-level composite nodes (including nested leaves in each top-level node's total, without emitting nested composites separately) vs (b) collecting results from ALL composites at every nesting depth, plus depth-first leaf flattening.
3. Explicit structural `List` recursion using `Nil` and `head :: tail` with an accumulator (prepending with `::` and reversing when closing a group) to group consecutive elements under a numeric cap, handling empty lists, non-positive caps, and single elements that exceed the cap.
4. Predicate partitioning with `partition(pred)` returning `(List[A], List[A])`, plus greedy pairing returning `Option[Map[String, A]]` using `sortBy` (ascending & descending), checking insufficient elements for `None`, and building maps with `zip` + `.toMap`.
5. Multi-key deterministic stable sorting (`sortBy(x => (rank(x), x.id))`) on a second `sealed trait` ADT (with exhaustive `rank` matching), combined with an unsealed polymorphic `abstract class` consumer (`Console` + `Memory` implementations) — reinforcing "Matching varies behaviour by what the data is; polymorphism by who does the work."

Start now with Topic 1 only, using a small `FsNode` (`File` / `Folder`) example, and end with your first micro-question for me.
```
