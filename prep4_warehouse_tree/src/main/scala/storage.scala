sealed trait StorageUnit:
  def code: String

case class StockItem(
  code: String,
  weightKg: Double,
  valueEuro: Double,
  fragile: Boolean = false
) extends StorageUnit

case class Pallet(
  code: String,
  maxLoadKg: Double,
  contents: List[StorageUnit]
) extends StorageUnit

case class Warehouse(
  name: String,
  units: List[StorageUnit]
)
