class ScreeningSchedule(val screenings: List[Screening]):
  def count: Int = screenings.length

  def findById(id: String): Option[Screening] =
    screenings.find(_.id == id)

  def sortedScreenings: List[Screening] =
    screenings.sorted

  def filterScreenings(pred: Screening => Boolean): (List[Screening], List[Screening]) = ???
