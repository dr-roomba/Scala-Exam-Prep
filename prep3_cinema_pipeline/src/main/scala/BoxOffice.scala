class BoxOffice(val process: CheckoutProcess):
  def processSingle(order: TicketOrder): Option[Receipt] =
    process.checkout(order)

  def processBatch(
    orders: List[TicketOrder],
    onReceipt: Option[Receipt => Unit] = None
  ): List[Receipt] = ???
