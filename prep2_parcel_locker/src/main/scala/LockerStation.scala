case class LockerStation(
  stationId: String,
  compartments: Vector[Compartment]
):
  def totalCompartments: Int = compartments.size

  def freeCount: Int = compartments.count(_.isFree)

  def compartmentWithId(id: Int): Option[Compartment] =
    compartments.find(_.id == id)

  def deposit(parcel: Parcel): Option[LockerStation] = ???

  def collect(code: ParcelCode): Option[(Parcel, LockerStation)] = ???

  def stats: StationStats = ???

  def depositBatch(csvLines: List[String]): (LockerStation, Int) = ???

object LockerStation:
  def empty(stationId: String): LockerStation =
    LockerStation(stationId, Vector.empty)

  def withSizes(stationId: String, sizes: Vector[LockerSize]): LockerStation =
    val comps = sizes.zipWithIndex.map((size, idx) => Compartment(idx + 1, size))
    LockerStation(stationId, comps)
