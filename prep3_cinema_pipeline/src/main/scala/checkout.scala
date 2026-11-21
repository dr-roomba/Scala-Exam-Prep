sealed abstract class CheckoutProcess(val logger: BookingLogger = SilentLogger):
  final def isValid(order: TicketOrder): Boolean = ???

  final def checkout(order: TicketOrder): Option[Receipt] = ???

  protected def applyDiscount(order: TicketOrder, subtotal: Double): Double

  protected def extraFee(order: TicketOrder): Double = 0.0

  protected def receiptLabel: String

class StandardCheckout(logger: BookingLogger = SilentLogger) extends CheckoutProcess(logger):
  protected def applyDiscount(order: TicketOrder, subtotal: Double): Double = ???

  protected def receiptLabel: String = ???

sealed class MemberCheckout(logger: BookingLogger = SilentLogger) extends CheckoutProcess(logger):
  protected def applyDiscount(order: TicketOrder, subtotal: Double): Double = ???

  protected def receiptLabel: String = ???

class VipCheckout(logger: BookingLogger = SilentLogger) extends MemberCheckout(logger):
  override protected def applyDiscount(order: TicketOrder, subtotal: Double): Double = ???

  override protected def extraFee(order: TicketOrder): Double = ???

  override protected def receiptLabel: String = ???
