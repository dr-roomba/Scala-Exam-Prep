class LockerNetworkManager(initialStations: Vector[LockerStation] = Vector.empty):
  private var stations: Map[String, LockerStation] =
    initialStations.map(s => s.stationId -> s).toMap

  def stationCount: Int = stations.size

  def apply(stationId: String): Option[LockerStation] = ???

  def addStation(station: LockerStation): Boolean = ???

  def depositAt(stationId: String, parcel: Parcel): Boolean = ???

  def collectFrom(stationId: String, code: ParcelCode): Option[Parcel] = ???

  def locateParcel(code: ParcelCode): Option[String] = ???

object LockerNetworkManager:
  def apply(initialStations: Vector[LockerStation] = Vector.empty): LockerNetworkManager =
    new LockerNetworkManager(initialStations)
