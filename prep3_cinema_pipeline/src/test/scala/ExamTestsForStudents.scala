import org.scalatest.funsuite.AnyFunSuite
import org.scalactic.Tolerance.convertNumericToPlusOrMinusWrapper

class ExamTestsForStudents extends AnyFunSuite:
  private val tolerance = 0.001

  private def expectSome[A](opt: Option[A]): A =
    opt.getOrElse(fail("Expected Some, but got None"))

  // EXAM TESTS PART 1

  test("Part1: Screening implements Ordered[Screening] by startMinutes then title") {
    val zephyr = StandardScreening("S1", "Zephyr", 1140, 11.00)
    val arrival = StandardScreening("S2", "Arrival", 1140, 12.00)
    val dune = ImaxScreening("I1", "Dune", 960, 16.00, "EN", "Dolby Atmos")
    val schedule = ScreeningSchedule(List(zephyr, arrival, dune))

    assert(schedule.sortedScreenings.map(_.id) === List("I1", "S2", "S1"))
    assert(dune < arrival)
    assert(zephyr > arrival)
  }

  test("Part1: ImaxScreening mixes in Captioned and PremiumAudio capability traits") {
    val imax = ImaxScreening("I1", "Interstellar", 1200, 16.50, "EN", "Dolby Atmos")
    val captioned: Captioned = imax
    val audio: PremiumAudio = imax

    assert(captioned.captionTag === "CC:EN")
    assert(audio.audioTag === "AUDIO:Dolby Atmos")
    assert(imax.featureSummary === "Interstellar [20:00] (CC:EN, AUDIO:Dolby Atmos)")
  }

  test("Part1: SilentLogger acts as a no-op Null Object BookingLogger") {
    val logger: BookingLogger = SilentLogger
    assert(logger.log("ignored message") === ())
  }

  // EXAM TESTS PART 2

  test("Part2: StandardCheckout and MemberCheckout follow the checkout template workflow") {
    val stdLog = MemoryLogger()
    val stdOrder = TicketOrder("S1", 2, 12.0, true)
    val stdReceipt = expectSome(StandardCheckout(stdLog).checkout(stdOrder))
    assert(stdReceipt.screeningId === "S1")
    assert(stdReceipt.seats === 2)
    assert(stdReceipt.finalTotal === 24.0 +- tolerance)
    assert(stdReceipt.label === "STANDARD")
    assert(stdLog.entries === Vector("[STANDARD] S1 x2 = 24.00"))

    val memLog = MemoryLogger()
    val memCheckout: CheckoutProcess = MemberCheckout(memLog)
    val memberReceipt = expectSome(memCheckout.checkout(TicketOrder("S1", 2, 10.0, true)))
    val guestReceipt = expectSome(memCheckout.checkout(TicketOrder("S1", 2, 10.0, false)))
    assert(memberReceipt.finalTotal === 17.0 +- tolerance)
    assert(memberReceipt.label === "MEMBER")
    assert(guestReceipt.finalTotal === 20.0 +- tolerance)
    assert(memLog.entries === Vector("[MEMBER] S1 x2 = 17.00", "[MEMBER] S1 x2 = 20.00"))
  }

  test("Part2: VipCheckout applies VIP discount and per-seat lounge fee hook") {
    val vipLog = MemoryLogger()
    val vipCheckout: CheckoutProcess = VipCheckout(vipLog)

    val memberReceipt = expectSome(vipCheckout.checkout(TicketOrder("S2", 2, 20.0, true)))
    val guestReceipt = expectSome(vipCheckout.checkout(TicketOrder("S2", 2, 20.0, false)))

    assert(memberReceipt.finalTotal === 39.0 +- tolerance)
    assert(memberReceipt.label === "VIP")
    assert(guestReceipt.finalTotal === 45.0 +- tolerance)
    assert(vipLog.entries === Vector("[VIP] S2 x2 = 39.00", "[VIP] S2 x2 = 45.00"))

    val silentVip: CheckoutProcess = VipCheckout()
    val silentReceipt = expectSome(silentVip.checkout(TicketOrder("S2", 1, 20.0, false)))
    assert(silentReceipt.finalTotal === 22.5 +- tolerance)
  }

  test("Part2: CheckoutProcess rejects invalid orders with None and no log entry") {
    val logger = MemoryLogger()
    val checkout: CheckoutProcess = StandardCheckout(logger)

    assert(checkout.checkout(TicketOrder("S1", 0, 12.0, false)) === None)
    assert(checkout.checkout(TicketOrder("S1", 2, -5.0, false)) === None)
    assert(checkout.checkout(TicketOrder("   ", 2, 12.0, false)) === None)
    assert(logger.entries === Vector.empty)
  }

  // EXAM TESTS PART 3

  test("Part3: PricingRules provides noDiscount, percentOff, and groupFlatOff strategies") {
    val smallOrder = TicketOrder("S1", 2, 10.0, false)
    val groupOrder = TicketOrder("S1", 4, 10.0, false)

    val noDisc: PricingRules.PricingRule = PricingRules.noDiscount
    val quarterOff: PricingRules.PricingRule = PricingRules.percentOff(25.0)
    val groupDeal: PricingRules.PricingRule = PricingRules.groupFlatOff(4, 10.0)

    assert(noDisc(groupOrder, 40.0) === 40.0 +- tolerance)
    assert(quarterOff(groupOrder, 40.0) === 30.0 +- tolerance)
    assert(groupDeal(groupOrder, 40.0) === 30.0 +- tolerance)
    assert(groupDeal(smallOrder, 20.0) === 20.0 +- tolerance)
  }

  test("Part3: PricingRules.bestPrice chooses the lowest price or subtotal when empty") {
    val groupOrder = TicketOrder("S1", 4, 10.0, false)
    val combined: PricingRules.PricingRule = PricingRules.bestPrice(
      List(
        PricingRules.noDiscount,
        PricingRules.percentOff(10.0),
        PricingRules.groupFlatOff(4, 10.0)
      )
    )

    assert(combined(groupOrder, 40.0) === 30.0 +- tolerance)
    assert(PricingRules.bestPrice(Nil)(groupOrder, 40.0) === 40.0 +- tolerance)
  }

  test("Part3: ScreeningSchedule.filterScreenings partitions matching and non-matching screenings in order") {
    val s1 = StandardScreening("S1", "Matinee", 840, 9.00)
    val s2 = ImaxScreening("I1", "Dune", 1140, 16.00, "EN", "Dolby Atmos")
    val s3 = StandardScreening("S2", "Classic", 960, 10.00)
    val s4 = StandardScreening("S3", "Late Show", 1260, 12.00)
    val schedule = ScreeningSchedule(List(s1, s2, s3, s4))

    val (evening, daytime) = schedule.filterScreenings(_.startMinutes >= 1080)
    assert(evening.map(_.id) === List("I1", "S3"))
    assert(daytime.map(_.id) === List("S1", "S2"))
  }

  // EXAM TESTS PART 4

  test("Part4: BoxOffice.processBatch works with omitted callback and with Some callback") {
    val office = BoxOffice(StandardCheckout())
    val orders = List(
      TicketOrder("S1", 2, 10.0, false),
      TicketOrder("S2", 0, 10.0, false),
      TicketOrder("S3", 1, 15.0, true)
    )

    val withoutCallback = office.processBatch(orders)
    assert(withoutCallback.map(_.screeningId) === List("S1", "S3"))

    var notifiedIds = Vector.empty[String]
    val withCallback = office.processBatch(orders, Some(r => notifiedIds = notifiedIds :+ r.screeningId))
    assert(withCallback.map(_.screeningId) === List("S1", "S3"))
    assert(notifiedIds === Vector("S1", "S3"))
  }

  test("Part4: BatchAuditor counts VIP receipts and accumulates revenue via callback closure") {
    val auditor = BatchAuditor()
    val stdOffice = BoxOffice(StandardCheckout())
    val vipOffice = BoxOffice(VipCheckout())

    val stdOrders = List(
      TicketOrder("S1", 2, 10.0, false),
      TicketOrder("BAD", -1, 10.0, false)
    )
    val vipOrders = List(
      TicketOrder("S2", 2, 20.0, true),
      TicketOrder("S3", 1, 20.0, false)
    )

    val stdReceipts = auditor.audit(stdOffice, stdOrders)
    val vipReceipts = auditor.audit(vipOffice, vipOrders)

    assert(stdReceipts.length === 1)
    assert(vipReceipts.length === 2)
    assert(auditor.vipCount === 2)
    assert(auditor.totalRevenue === 81.5 +- tolerance)
  }
