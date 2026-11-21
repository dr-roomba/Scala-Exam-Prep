case class ParcelCode private (value: String):
  private def copy(value: String = this.value): ParcelCode =
    new ParcelCode(value)

object ParcelCode:
  def apply(raw: String): Option[ParcelCode] = ???

case class Parcel private (
  code: ParcelCode,
  weightKg: Double,
  volumeCm3: Double
):
  private def copy(
    code: ParcelCode = this.code,
    weightKg: Double = this.weightKg,
    volumeCm3: Double = this.volumeCm3
  ): Parcel =
    new Parcel(code, weightKg, volumeCm3)

object Parcel:
  def apply(code: ParcelCode, weightKg: Double, volumeCm3: Double): Option[Parcel] = ???

  def parse(csvLine: String): Option[Parcel] = ???
