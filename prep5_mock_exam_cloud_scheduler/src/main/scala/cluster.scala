sealed trait ClusterUnit:
  def id: String

case class Server(
  id: String,
  cpuCores: Int,
  ramGb: Int,
  hourlyCost: Double = 0.0
) extends ClusterUnit

case class Rack(
  id: String,
  label: String,
  units: List[ClusterUnit]
) extends ClusterUnit

case class Cluster(name: String, units: List[ClusterUnit])
