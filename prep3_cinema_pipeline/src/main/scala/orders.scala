case class TicketOrder(
  screeningId: String,
  seats: Int,
  baseUnitPrice: Double,
  isMember: Boolean
):
  def subtotal: Double = seats * baseUnitPrice

case class Receipt(
  screeningId: String,
  seats: Int,
  finalTotal: Double,
  label: String
)
