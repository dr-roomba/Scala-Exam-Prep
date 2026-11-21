object PricingRules:
  type PricingRule = (TicketOrder, Double) => Double

  val noDiscount: PricingRule = (_, _) => ???

  def percentOff(pct: Double): PricingRule = ???

  def groupFlatOff(minSeats: Int, amountOff: Double): PricingRule = ???

  def bestPrice(rules: List[PricingRule]): PricingRule = ???
