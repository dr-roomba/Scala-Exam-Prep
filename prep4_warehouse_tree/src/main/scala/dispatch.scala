sealed trait DeliveryTruck:
  def id: String

case class StandardTruck(id: String) extends DeliveryTruck

case class InsuredTruck(id: String, rate: Double) extends DeliveryTruck
