enum DeploymentStatus:
  case Succeeded, Failed

case class DeploymentReport(
  pipelineName: String,
  status: DeploymentStatus,
  assignedCount: Int,
  totalHourlyCost: Double
)

sealed abstract class DeploymentPipeline:
  def name: String

  protected def selectJobs(jobs: List[Workload]): List[Workload] = jobs

  protected def computeCost(allocation: Map[String, Server]): Double

  final def deploy(
    c: Cluster,
    jobs: List[Workload],
    onAssigned: Option[(String, Server) => Unit] = None
  ): DeploymentReport = ???

class StandardPipeline extends DeploymentPipeline:
  val name: String = "Standard"
  protected def computeCost(allocation: Map[String, Server]): Double = ???

class CanaryPipeline(val maxJobs: Int) extends DeploymentPipeline:
  val name: String = "Canary"
  protected def computeCost(allocation: Map[String, Server]): Double = ???

class CostAuditor(val highCostThreshold: Double):
  private var _highCostCount: Int = 0
  private var _auditedServerIds: List[String] = Nil

  def highCostCount: Int = _highCostCount
  def auditedServerIds: List[String] = _auditedServerIds

  def runAudit(
    pipeline: DeploymentPipeline,
    c: Cluster,
    jobs: List[Workload]
  ): DeploymentReport = ???
