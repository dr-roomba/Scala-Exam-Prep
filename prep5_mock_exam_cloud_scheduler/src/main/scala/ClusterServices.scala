object ClusterServices:

  def totalServers(c: Cluster): Int =
    def countUnits(units: List[ClusterUnit]): Int =
      units.foldLeft(0) { (acc, unit) =>
        unit match
          case _: Server => acc + 1
          case Rack(_, _, subUnits) => acc + countUnits(subUnits)
      }
    countUnits(c.units)

  def totalCpuCores(c: Cluster): Int =
    def sumCores(units: List[ClusterUnit]): Int =
      units.foldLeft(0) { (acc, unit) =>
        unit match
          case Server(_, cores, _, _) => acc + cores
          case Rack(_, _, subUnits) => acc + sumCores(subUnits)
      }
    sumCores(c.units)

  def allServerIds(c: Cluster): List[String] =
    def collectIds(units: List[ClusterUnit]): List[String] =
      units.flatMap {
        case Server(id, _, _, _) => List(id)
        case Rack(_, _, subUnits) => collectIds(subUnits)
      }
    collectIds(c.units)

  def topLevelRackCosts(c: Cluster): List[(String, Double)] = ???

  def resourceSummary(c: Cluster): Option[ClusterStats] = ???

  def filterServers(
    c: Cluster,
    pred: Server => Boolean
  ): (List[Server], List[Server]) = ???

  def scheduleJobs(
    c: Cluster,
    jobs: List[Workload]
  ): Option[Map[String, Server]] = ???
