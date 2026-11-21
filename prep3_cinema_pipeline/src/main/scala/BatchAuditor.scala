class BatchAuditor:
  private var _vipCount: Int = 0
  private var _totalRevenue: Double = 0.0

  def vipCount: Int = _vipCount

  def totalRevenue: Double = _totalRevenue

  def audit(office: BoxOffice, orders: List[TicketOrder]): List[Receipt] = ???
