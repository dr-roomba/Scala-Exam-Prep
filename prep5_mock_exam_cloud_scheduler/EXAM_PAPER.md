# University College Dublin — School of Computer Science

## COMP30950 Multi-Paradigm Programming
### 2026 Mock Practical Examination — Cloud Compute Cluster & Job Scheduler

- **Duration:** 3 Hours *(Strictly Offline Simulation)*
- **Total Marks:** 100 *(Part 1: 20 | Part 2: 25 | Part 3: 35 | Part 4: 20)*
- **Grading Environment:** Scala `3.3.8`, `sbt test`

---

## General Instructions & Exam Rules

1. **Offline Exam Simulation:** This mock examination mirrors the 2026 COMP30950 practical exam rules (`ProgrammingExamRules.pdf`). Complete it under a strict 3-hour timer with no internet access and no AI tools. The only permitted reference is the module lecture slides viewed inside IntelliJ IDEA.
2. **Fresh Domain:** As announced for the 2026 exam, this paper introduces a new domain rather than extending the semester's Sensor Network assignments. It synthesizes the multi-paradigm skills developed across all lectures, practicals, and assignments.
3. **Repository & Build Rules:**
   - Do **not** add any new directories or package declarations (keep all `.scala` files in the default package under `src/main/scala/` and `src/test/scala/`).
   - Do **not** add dependencies to `build.sbt`.
   - Do **not** modify `InitialTests.scala` or `ExamTestsForStudents.scala`. You are strongly encouraged to write thorough edge-case tests in a **separate** file (e.g., `src/test/scala/MyEdgeCaseTests.scala`).
   - Your submission must compile and run cleanly via `sbt test` from the repository root.
4. **Test Contract & Backward Compatibility:** Essential API details (exact method names, parameter orders, return types, and floating-point tolerances) are clarified unambiguously in `InitialTests.scala` and `ExamTestsForStudents.scala`. Read both test files alongside this paper. You may add private helper methods/functions or refactor the starter code as needed, provided all tests in `InitialTests.scala` continue to pass throughout.
5. **Grading Split (80% Correctness / 20% Software Quality):** Approximately 80% of the mark is awarded for correctness (assessed via provided and unseen edge-case unit tests) and 20% for software quality (`SoftwareQuality.pdf`). **The software quality mark applies only to the proportion of code that is correct.** Prioritise working, well-tested code, and reserve time at the end to refine and simplify your solution.
6. **Module Programming Rules:** Use Scala 3 braceless 2-space indentation syntax. Do **not** use `for`-comprehensions, `return`, `break`/`continue`, `null`, thrown exceptions for domain failures, `.get` on `Option`, `var` in `case class`es, or `isInstanceOf`. Do **not** use out-of-scope Scala 3 features (`given`/`using`, `extension`, `opaque`, `export`, `Either`, `Try`).
7. **Assumptions:** Within any `Cluster`, all `ClusterUnit` IDs may be assumed distinct. Within any `List[Workload]`, all `Workload` IDs may be assumed distinct. No input validation is required on `Server`, `Rack`, `Cluster`, `BatchJob`, or `RealtimeJob` — only `ComputeSpec` (Part 2) requires smart-constructor validation.

---

## Overview of the Starter Repository

The repository compiles out of the box (`InitialTests` pass; `ExamTestsForStudents` fail with `NotImplementedError` on `???` stubs):

- `cluster.scala`: Recursive cluster hierarchy (`sealed trait ClusterUnit`, `Server(id, cpuCores, ramGb, hourlyCost = 0.0)`, `Rack(id, label, units)`, `Cluster(name, units)`).
- `workload.scala`: `ComputeSpec` smart-constructor stub, `sealed trait Workload` (`BatchJob`, `RealtimeJob`), and `ClusterStats`.
- `ClusterServices.scala`: Working baseline queries (`totalServers`, `totalCpuCores`, `allServerIds`) plus stubs for Parts 1–3.
- `deployment.scala`: `DeploymentStatus`, `DeploymentReport`, `DeploymentPipeline` hierarchy (`StandardPipeline`, `CanaryPipeline`), and `CostAuditor` stubs for Part 4.

---

## Part 1 — Top-Level Rack Cost Aggregation *(20 marks)*

`Server` already defines `hourlyCost: Double = 0.0` so that existing 3-argument constructions (such as `Server("s1", 8, 32)` in `InitialTests.scala`) remain backward compatible. Preserve this default parameter in any refactoring.

Implement `ClusterServices.topLevelRackCosts(c: Cluster): List[(String, Double)]`:

- For each **top-level `Rack`** directly contained in `c.units`, compute `(rack.id, totalCost)`, where `totalCost` is the sum of `hourlyCost` of **all `Server`s anywhere inside that rack's subtree** (including servers inside nested `Rack`s at any depth).
- Nested `Rack`s contribute their servers' costs to their enclosing top-level `Rack`'s total and must **not** appear as separate entries in the returned list.
- Top-level standalone `Server`s directly inside `c.units` are ignored.
- An empty top-level `Rack` (or one containing only empty nested racks) produces `(rack.id, 0.0)`.
- Return the resulting list **sorted alphabetically** (ascending) by `rack.id`. If `c.units` contains no top-level `Rack`s, return `Nil`.

---

## Part 2 — Smart Constructor, Single-Pass Summary & Partitioning *(25 marks)*

1. **Smart Constructor (`workload.scala`):** Implement `ComputeSpec.apply(cpuCores: Int, ramGb: Int): Option[ComputeSpec]`.
   - A `ComputeSpec` is valid if and only if `cpuCores > 0 && ramGb > 0`.
   - Return `Some(new ComputeSpec(cpuCores, ramGb))` when valid and `None` otherwise.
   - Keep both the primary constructor and `copy` `private` so external code can never construct an invalid `ComputeSpec`.

2. **Single-Pass Resource Summary (`ClusterServices.scala`):** Implement `ClusterServices.resourceSummary(c: Cluster): Option[ClusterStats]`.
   - Consider all `Server`s anywhere in `c` (standalone and inside nested racks).
   - If `c` contains zero servers, return `None`.
   - Otherwise return `Some(ClusterStats(serverCount, totalCores, minRamGb, maxRamGb, avgHourlyCost))`, where `avgHourlyCost` is the arithmetic mean of `hourlyCost` across all servers.
   - **Constraint:** Once the servers are collected, you **must** compute all 5 statistics in a **single pass using `foldLeft`** (do not traverse the server list multiple times with separate `.size`, `.sum`, `.min`, `.max` calls).

3. **Predicate Partitioning (`ClusterServices.scala`):** Implement `ClusterServices.filterServers(c: Cluster, pred: Server => Boolean): (List[Server], List[Server])`.
   - Partition all `Server`s in `c` into `(matching, nonMatching)`, where `matching` contains every server satisfying `pred` and `nonMatching` contains the rest.
   - Both returned lists must preserve the **depth-first traversal order** of servers in `c` (matching `allServerIds(c)`).

---

## Part 3 — Greedy Workload Scheduler *(35 marks)*

Implement `ClusterServices.scheduleJobs(c: Cluster, jobs: List[Workload]): Option[Map[String, Server]]` to assign every job in `jobs` to a **distinct** `Server` in `c` according to the following deterministic greedy specification:

1. **Empty Job List:** If `jobs` is empty, return `Some(Map.empty)` (even if `c` contains zero servers).
2. **Server Eligibility:** A `Server` can run a `Workload` if and only if:
   `server.cpuCores >= job.spec.cpuCores && server.ramGb >= job.spec.ramGb`.
3. **Job Urgency Weight:**
   - `BatchJob(_, _, priority)` has urgency `priority.toDouble`.
   - `RealtimeJob(_, _, slaMultiplier)` has urgency `100.0 * slaMultiplier`.
4. **Job Processing Order:** Process jobs in **descending order of urgency**, breaking ties by **ascending `job.id`** (lexicographical `String` order).
5. **Server Selection Rule:** For each job in processing order, consider all **remaining unassigned** eligible servers in `c` and assign the server that minimises:
   1. `hourlyCost` (ascending),
   2. tie-break by `cpuCores` (ascending),
   3. tie-break by `server.id` (ascending).
   Remove the chosen server from the unassigned pool and record `job.id -> server`.
6. **Failure:** If at any point no remaining unassigned server can satisfy the current job (including when `jobs.length > totalServers(c)`), return `None`. Otherwise return `Some(allocation)`.

**Notes:**
- *(1)* This specification defines a **deterministic single-pass greedy algorithm** in urgency order, **not** backtracking search. If an earlier, higher-urgency job claims a server that leaves a later job with no eligible remaining server, `scheduleJobs` must return `None`.
- *(2)* Scala's `sortBy` accepts tuple keys whose components are compared lexicographically (e.g., `xs.sortBy(x => (-score(x), x.id))`).
- *(3)* Implement this purely functionally (no `var`s or mutable collections).

---

## Part 4 — Deployment Pipelines & Cost Auditor *(20 marks)*

Complete `deployment.scala` using **Template Method** and a **Closure**:

1. **Template Method (`DeploymentPipeline`):**
   - Implement `final def deploy(c: Cluster, jobs: List[Workload], onAssigned: Option[(String, Server) => Unit] = None): DeploymentReport` as a **Template Method** executing these exact steps in order:
     1. Select the jobs to deploy via the `protected` hook method `selectJobs(jobs)` (which defaults to returning `jobs` unchanged).
     2. Schedule the selected jobs via `ClusterServices.scheduleJobs(c, selected)`.
     3. If scheduling returns `Some(allocation)`:
        - If `onAssigned` is `Some(callback)`, invoke `callback(jobId, server)` for every entry in `allocation` in **ascending `jobId` order**.
        - Compute `totalCost = computeCost(allocation)` using the abstract step `computeCost`.
        - Return `DeploymentReport(name, DeploymentStatus.Succeeded, allocation.size, totalCost)`.
     4. If scheduling returns `None`:
        - Do **not** invoke `onAssigned`.
        - Return `DeploymentReport(name, DeploymentStatus.Failed, 0, 0.0)`.
   - Complete the concrete subclasses:
     - `StandardPipeline`: `name = "Standard"`, inherits the default `selectJobs` hook, and defines `computeCost` as the sum of `hourlyCost` across all assigned servers.
     - `CanaryPipeline(val maxJobs: Int)`: `name = "Canary"`, **overrides the `selectJobs` hook** to retain only the first `maxJobs.max(0)` jobs from the input `jobs` list (preserving input order before scheduling), and defines `computeCost` as the sum of assigned servers' `hourlyCost` multiplied by `1.10` (10% monitoring overhead).

2. **Cost Auditor (`CostAuditor`) — Mandatory Closure Constraint:**
   - Implement `runAudit(pipeline: DeploymentPipeline, c: Cluster, jobs: List[Workload]): DeploymentReport`.
   - At the start of every `runAudit` call, reset `_highCostCount` to `0` and `_auditedServerIds` to `Nil`.
   - **Mandatory Closure:** `runAudit` **must** pass a **closure** as `onAssigned = Some(...)` to `pipeline.deploy` that updates `_highCostCount` (counting each assigned server with `server.hourlyCost > highCostThreshold`, strictly greater than) and records each assigned server's `id` in `_auditedServerIds` in callback order (ascending `jobId`).
   - Return the `DeploymentReport` produced by `pipeline.deploy`.
   - *Note:* Calculating `_highCostCount` or `_auditedServerIds` by calling `scheduleJobs` directly instead of passing a closure to `pipeline.deploy` violates the design requirement and will **not** earn full marks for Part 4.

---

## Post-Exam Self-Grading Rubric

Use this checklist **after** your 3-hour timer stops to audit your work as Mel's grading suite and quality review would.

### A. Correctness & Edge-Case Audit *(80 marks)*

- [ ] `InitialTests.scala` (6/6) and `ExamTestsForStudents.scala` (10/10) are **unmodified** and **100% green** via `sbt test`.
- [ ] **Part 1 (20m):** Empty cluster (`Nil`), standalone-only cluster (`Nil`), empty top-level rack (`(id, 0.0)`), deeply nested racks (3+ levels) folded into enclosing top-level rack only, unsorted input rack order sorted alphabetically by `rack.id`, `Server` 3-arg backward compatibility intact.
- [ ] **Part 2 (25m):** `ComputeSpec(0, 1)`, `ComputeSpec(1, 0)`, negative values (`None`), `new ComputeSpec` and `.copy` remain inaccessible; `resourceSummary` returns `None` on empty cluster AND cluster with only empty racks, handles 1-server cluster (`minRamGb == maxRamGb`), genuinely uses **one** `foldLeft` pass; `filterServers` handles empty cluster `(Nil, Nil)`, all-matching, none-matching, and preserves depth-first order.
- [ ] **Part 3 (35m):** `jobs == Nil` on empty cluster returns `Some(Map.empty)`; equal urgency between `BatchJob(priority = 150)` and `RealtimeJob(slaMultiplier = 1.5)` breaks tie by ascending `job.id`; equal server `hourlyCost` breaks tie by lowest `cpuCores` then ascending `server.id`; RAM-only or CPU-only mismatch rejected; greedy order scarcity returns `None` rather than backtracking; no server assigned twice.
- [ ] **Part 4 (20m):** `deploy` is `final`, uses `selectJobs` hook + abstract `computeCost`; `CanaryPipeline(0)` and `CanaryPipeline(-3)` succeed with 0 jobs and cost `0.0`; `onAssigned` is never called on `Failed` deployment; `onAssigned` fires in ascending `jobId` order on `Succeeded`; `CostAuditor` uses strict `>` (`hourlyCost == threshold` is NOT counted), uses a closure passed to `pipeline.deploy`, and resets state cleanly even when a second `runAudit` fails!

### B. Software Quality Audit — Mapped to `SoftwareQuality.pdf` *(20 marks)*

*Remember: quality marks scale with your correctness score (e.g., 16/20 quality × 100% correctness = 16/20).*

| `SoftwareQuality.pdf` Criterion | What to Check in Your Solution | Score |
|---|---|---:|
| **1. Zero Warnings & Clean Scala 3 Layout** | `sbt test` produces **0 compiler warnings** (including zero non-exhaustive match warnings!). Braceless 2-space indentation throughout; no `for`-comprehensions, `return`, `null`, `.get`, `var` in case classes, or `isInstanceOf`. | / 4 |
| **2. DRY & Shared Private Helpers** | Common tree flattening (e.g., `private def allServers(c: Cluster): List[Server]` or shared subtree cost helper) factored out instead of copy-pasted across `ClusterServices`. No duplicated cost logic between `StandardPipeline` and `CanaryPipeline`. | / 4 |
| **3. SRP, Short Methods & Cognitive Load (KISS)** | Every helper does one clear job (~3–10 lines each). Complex logic (`scheduleJobs`, `resourceSummary`) decomposed into clean, self-explanatory private/nested helpers with intention-revealing names. | / 4 |
| **4. Minimal Visibility, Scope & Immutability** | Helper methods marked `private` (or `protected` in `DeploymentPipeline`); local `val`s instead of extra fields; zero `var`s outside `CostAuditor`'s two private fields; `sealed` matches are exhaustive without hiding missing cases behind `case _`. | / 4 |
| **5. Paradigm Fidelity, Naming & Comments** | True Template Method (`final deploy` + `protected` hook + abstract step), genuine closure in `CostAuditor`, single-pass `foldLeft` in `resourceSummary`, Tell Don't Ask, minimal/zero redundant comments (extract private helpers instead of commenting blocks), plus thorough `MyEdgeCaseTests.scala`. | / 4 |
