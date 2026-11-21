import org.scalatest.funsuite.AnyFunSuite
import org.scalactic.Tolerance.convertNumericToPlusOrMinusWrapper
import scala.collection.mutable.ListBuffer

class ExamTestsForStudents extends AnyFunSuite:

  private val tolerance = 0.001

  private def spec(cores: Int, ram: Int): ComputeSpec =
    ComputeSpec(cores, ram).getOrElse(fail(s"Expected valid ComputeSpec($cores, $ram)"))

  // EXAM TESTS PART 1

  test("StudentExamPart1: topLevelRackCosts sums server costs per top-level rack sorted by rack id") {
    val standalone = Server("s0", 4, 16, 5.00)
    val rackB = Rack("rack-b", "Storage", List(
      Server("sb1", 8, 32, 1.50),
      Server("sb2", 8, 64, 2.25)
    ))
    val subRackA = Rack("rack-a-sub", "Nested", List(
      Server("sa2", 16, 64, 4.10)
    ))
    val rackA = Rack("rack-a", "Compute", List(
      Server("sa1", 4, 16, 0.90),
      subRackA
    ))
    val cluster = Cluster("prod", List(standalone, rackB, rackA))

    val costs = ClusterServices.topLevelRackCosts(cluster)
    assert(costs.length === 2)
    assert(costs(0)(0) === "rack-a")
    assert(costs(0)(1) === 5.00 +- tolerance)
    assert(costs(1)(0) === "rack-b")
    assert(costs(1)(1) === 3.75 +- tolerance)
  }

  test("StudentExamPart1: topLevelRackCosts returns empty list when cluster has no top-level racks") {
    val cluster = Cluster("standalone-only", List(
      Server("s1", 4, 16, 1.00),
      Server("s2", 8, 32, 2.00)
    ))
    assert(ClusterServices.topLevelRackCosts(cluster) === Nil)
  }

  // EXAM TESTS PART 2

  test("StudentExamPart2: ComputeSpec smart constructor validates positive cores and RAM") {
    val valid = ComputeSpec(4, 16)
    assert(valid.map(s => (s.cpuCores, s.ramGb)) === Some((4, 16)))
    assert(ComputeSpec(0, 16) === None)
    assert(ComputeSpec(4, -8) === None)
    assertDoesNotCompile("new ComputeSpec(4, 16)")
    assertDoesNotCompile("val s: ComputeSpec = ???; s.copy(cpuCores = 0)")
  }

  test("StudentExamPart2: resourceSummary computes ClusterStats across nested servers and returns None when empty") {
    assert(ClusterServices.resourceSummary(Cluster("empty", Nil)) === None)

    val s1 = Server("s1", 4, 16, 1.00)
    val s2 = Server("s2", 8, 64, 2.50)
    val s3 = Server("s3", 12, 32, 4.00)
    val cluster = Cluster("mix", List(
      s1,
      Rack("r1", "Row 1", List(s2, Rack("r2", "Row 2", List(s3))))
    ))

    ClusterServices.resourceSummary(cluster) match
      case Some(stats) =>
        assert(stats.serverCount === 3)
        assert(stats.totalCores === 24)
        assert(stats.minRamGb === 16)
        assert(stats.maxRamGb === 64)
        assert(stats.avgHourlyCost === 2.50 +- tolerance)
      case None =>
        fail("Expected Some(ClusterStats) for non-empty cluster")
  }

  test("StudentExamPart2: filterServers partitions servers by predicate preserving depth-first order") {
    val s1 = Server("s1", 4, 16, 1.00)
    val s2 = Server("s2", 8, 32, 2.00)
    val s3 = Server("s3", 16, 64, 4.00)
    val s4 = Server("s4", 2, 8, 0.50)
    val cluster = Cluster("dc", List(
      s1,
      Rack("r1", "Main", List(s2, Rack("r2", "Sub", List(s3)))),
      s4
    ))

    val (large, small) = ClusterServices.filterServers(cluster, _.ramGb >= 32)
    assert(large === List(s2, s3))
    assert(small === List(s1, s4))
  }

  // EXAM TESTS PART 3

  test("StudentExamPart3: scheduleJobs assigns jobs in urgency order to cheapest capable distinct servers") {
    val s1 = Server("s1", 4, 16, 1.00)
    val s2 = Server("s2", 8, 32, 2.00)
    val s3 = Server("s3", 16, 64, 4.00)
    val cluster = Cluster("compute", List(s1, Rack("r1", "Main", List(s2, s3))))

    val jobs = List(
      BatchJob("b1", spec(4, 16), priority = 10),
      RealtimeJob("r1", spec(8, 32), slaMultiplier = 1.5),
      BatchJob("b2", spec(4, 16), priority = 50)
    )

    val expected = Map("r1" -> s2, "b2" -> s1, "b1" -> s3)
    assert(ClusterServices.scheduleJobs(cluster, jobs) === Some(expected))
  }

  test("StudentExamPart3: scheduleJobs returns None on insufficient capacity and Some(Map.empty) for no jobs") {
    val cluster = Cluster("tiny", List(Server("s1", 4, 16, 1.00)))
    assert(ClusterServices.scheduleJobs(cluster, Nil) === Some(Map.empty))

    val tooBig = List(BatchJob("big", spec(8, 16), priority = 5))
    assert(ClusterServices.scheduleJobs(cluster, tooBig) === None)

    val tooMany = List(
      BatchJob("j1", spec(2, 8), priority = 1),
      BatchJob("j2", spec(2, 8), priority = 2)
    )
    assert(ClusterServices.scheduleJobs(cluster, tooMany) === None)
  }

  // EXAM TESTS PART 4

  test("StudentExamPart4: StandardPipeline deploys all jobs and invokes optional callback in jobId order") {
    val s1 = Server("s1", 4, 16, 1.50)
    val s2 = Server("s2", 8, 32, 3.00)
    val cluster = Cluster("prod", List(s1, s2))
    val jobs = List(
      BatchJob("job-z", spec(4, 16), priority = 10),
      RealtimeJob("job-a", spec(8, 32), slaMultiplier = 1.0)
    )

    val observed = ListBuffer.empty[(String, String)]
    val report = StandardPipeline().deploy(
      cluster,
      jobs,
      Some((jobId, srv) => observed += ((jobId, srv.id)))
    )

    assert(report.pipelineName === "Standard")
    assert(report.status === DeploymentStatus.Succeeded)
    assert(report.assignedCount === 2)
    assert(report.totalHourlyCost === 4.50 +- tolerance)
    assert(observed.toList === List(("job-a", "s2"), ("job-z", "s1")))
  }

  test("StudentExamPart4: CanaryPipeline limits deployed jobs and applies 10% monitoring overhead") {
    val s1 = Server("s1", 4, 16, 1.00)
    val s2 = Server("s2", 8, 32, 2.00)
    val cluster = Cluster("canary-dc", List(s1, s2))
    val jobs = List(
      BatchJob("j1", spec(4, 16), priority = 10),
      BatchJob("j2", spec(8, 32), priority = 20),
      BatchJob("j3", spec(4, 16), priority = 30)
    )

    val stdReport = StandardPipeline().deploy(cluster, jobs)
    assert(stdReport.pipelineName === "Standard")
    assert(stdReport.status === DeploymentStatus.Failed)
    assert(stdReport.assignedCount === 0)
    assert(stdReport.totalHourlyCost === 0.0 +- tolerance)

    val canaryReport = CanaryPipeline(2).deploy(cluster, jobs)
    assert(canaryReport.pipelineName === "Canary")
    assert(canaryReport.status === DeploymentStatus.Succeeded)
    assert(canaryReport.assignedCount === 2)
    assert(canaryReport.totalHourlyCost === 3.30 +- tolerance)
  }

  test("StudentExamPart4: CostAuditor audits high-cost servers via closure and resets between runs") {
    val s1 = Server("s1", 4, 16, 1.50)
    val s2 = Server("s2", 8, 32, 2.00)
    val s3 = Server("s3", 16, 64, 4.50)
    val cluster = Cluster("audit-dc", List(s1, s2, s3))
    val jobs = List(
      BatchJob("j-a", spec(4, 16), priority = 30),
      BatchJob("j-c", spec(16, 64), priority = 10),
      BatchJob("j-b", spec(8, 32), priority = 20)
    )

    val auditor = CostAuditor(2.00)

    val report1 = auditor.runAudit(StandardPipeline(), cluster, jobs)
    assert(report1.status === DeploymentStatus.Succeeded)
    assert(report1.assignedCount === 3)
    assert(report1.totalHourlyCost === 8.00 +- tolerance)
    assert(auditor.highCostCount === 1)
    assert(auditor.auditedServerIds === List("s1", "s2", "s3"))

    val report2 = auditor.runAudit(CanaryPipeline(1), cluster, jobs)
    assert(report2.status === DeploymentStatus.Succeeded)
    assert(report2.assignedCount === 1)
    assert(report2.totalHourlyCost === 1.65 +- tolerance)
    assert(auditor.highCostCount === 0)
    assert(auditor.auditedServerIds === List("s1"))
  }
