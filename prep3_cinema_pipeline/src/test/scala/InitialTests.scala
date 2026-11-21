import org.scalatest.funsuite.AnyFunSuite
import org.scalactic.Tolerance.convertNumericToPlusOrMinusWrapper

class InitialTests extends AnyFunSuite:
  private val tolerance = 0.001

  test("Initial: StandardScreening formats start time in 24-hour HH:mm format") {
    val morning = StandardScreening("S1", "Paddington", 545, 9.50)
    val evening = StandardScreening("S2", "Arrival", 1170, 12.00)
    assert(morning.formattedStartTime === "09:05")
    assert(evening.formattedStartTime === "19:30")
  }

  test("Initial: ScreeningSchedule reports screening count") {
    val s1 = StandardScreening("S1", "Arrival", 1140, 12.00)
    val s2 = StandardScreening("S2", "Dune", 1260, 13.50)
    assert(ScreeningSchedule(Nil).count === 0)
    assert(ScreeningSchedule(List(s1, s2)).count === 2)
  }

  test("Initial: ScreeningSchedule.findById returns Some for existing id and None otherwise") {
    val s1 = StandardScreening("S1", "Arrival", 1140, 12.00)
    val s2 = StandardScreening("S2", "Dune", 1260, 13.50)
    val schedule = ScreeningSchedule(List(s1, s2))
    assert(schedule.findById("S2") === Some(s2))
    assert(schedule.findById("S99") === None)
  }

  test("Initial: TicketOrder.subtotal computes seats times baseUnitPrice") {
    val order = TicketOrder("S1", 3, 11.50, false)
    assert(order.subtotal === 34.50 +- tolerance)
  }

  test("Initial: MemoryLogger records log messages in insertion order") {
    val logger = MemoryLogger()
    assert(logger.entries === Vector.empty)
    logger.log("first")
    logger.log("second")
    assert(logger.entries === Vector("first", "second"))
  }
