import org.scalatest.funsuite.AnyFunSuite
import org.scalactic.Tolerance.convertNumericToPlusOrMinusWrapper

class InitialTests extends AnyFunSuite:

  private val tolerance = 0.001

  private val s1 = Server("s1", 4, 16)
  private val s2 = Server("s2", 8, 32)
  private val s3 = Server("s3", 16, 64)
  private val s4 = Server("s4", 2, 8)
  private val subRack = Rack("r-sub", "GPU Shelf", List(s3))
  private val rack1 = Rack("r1", "Compute Row A", List(s2, subRack))
  private val emptyRack = Rack("r-empty", "Spare", Nil)
  private val nestedCluster = Cluster("eu-west-1", List(s1, rack1, emptyRack, s4))

  test("ExamInitial: Server supports 3-argument construction with default hourlyCost 0.0") {
    val srv = Server("srv-1", 8, 32)
    assert(srv.id === "srv-1")
    assert(srv.cpuCores === 8)
    assert(srv.ramGb === 32)
    assert(srv.hourlyCost === 0.0 +- tolerance)
  }

  test("ExamInitial: totalServers counts servers across standalone units and nested racks") {
    assert(ClusterServices.totalServers(nestedCluster) === 4)
  }

  test("ExamInitial: totalServers returns 0 for an empty cluster and empty racks") {
    assert(ClusterServices.totalServers(Cluster("empty", Nil)) === 0)
    val onlyEmptyRacks = Cluster("empty-racks", List(emptyRack, Rack("r2", "Empty Parent", List(emptyRack))))
    assert(ClusterServices.totalServers(onlyEmptyRacks) === 0)
  }

  test("ExamInitial: totalCpuCores sums CPU cores across all nested servers") {
    assert(ClusterServices.totalCpuCores(nestedCluster) === 30)
    assert(ClusterServices.totalCpuCores(Cluster("empty", Nil)) === 0)
  }

  test("ExamInitial: allServerIds returns server IDs in depth-first order") {
    assert(ClusterServices.allServerIds(nestedCluster) === List("s1", "s2", "s3", "s4"))
    assert(ClusterServices.allServerIds(Cluster("empty", Nil)) === Nil)
  }

  test("ExamInitial: baseline functions handle a cluster with only standalone servers") {
    val edge = Cluster("edge", List(Server("e1", 2, 4), Server("e2", 4, 8)))
    assert(ClusterServices.totalServers(edge) === 2)
    assert(ClusterServices.totalCpuCores(edge) === 6)
    assert(ClusterServices.allServerIds(edge) === List("e1", "e2"))
  }
