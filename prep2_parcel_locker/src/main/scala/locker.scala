enum LockerSize:
  case Small, Medium, Large

  def maxWeightKg: Double = this match
    case Small  => 2.0
    case Medium => 10.0
    case Large  => 25.0

  def maxVolumeCm3: Double = this match
    case Small  => 5000.0
    case Medium => 25000.0
    case Large  => 80000.0

  def canHold(weightKg: Double, volumeCm3: Double): Boolean =
    weightKg <= maxWeightKg && volumeCm3 <= maxVolumeCm3

case class Compartment(
  id: Int,
  size: LockerSize,
  occupant: Option[Parcel] = None
):
  def isFree: Boolean = occupant.isEmpty

  def canFit(parcel: Parcel): Boolean =
    isFree && size.canHold(parcel.weightKg, parcel.volumeCm3)

case class StationStats(
  totalCompartments: Int,
  occupiedCount: Int,
  totalWeightKg: Double,
  heaviestCode: Option[String]
)
