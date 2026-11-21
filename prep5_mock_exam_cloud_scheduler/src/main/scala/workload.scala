case class ComputeSpec private (cpuCores: Int, ramGb: Int):
  private def copy(
    cpuCores: Int = this.cpuCores,
    ramGb: Int = this.ramGb
  ): ComputeSpec =
    new ComputeSpec(cpuCores, ramGb)

object ComputeSpec:
  def apply(cpuCores: Int, ramGb: Int): Option[ComputeSpec] = ???

sealed trait Workload:
  def id: String
  def spec: ComputeSpec

case class BatchJob(id: String, spec: ComputeSpec, priority: Int) extends Workload
case class RealtimeJob(id: String, spec: ComputeSpec, slaMultiplier: Double) extends Workload

case class ClusterStats(
  serverCount: Int,
  totalCores: Int,
  minRamGb: Int,
  maxRamGb: Int,
  avgHourlyCost: Double
)
