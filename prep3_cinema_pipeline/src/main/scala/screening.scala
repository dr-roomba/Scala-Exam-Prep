trait Captioned:
  def subtitleLanguage: String
  def captionTag: String = ???

trait PremiumAudio:
  def audioFormat: String
  def audioTag: String = ???

sealed trait Screening extends Ordered[Screening]:
  def id: String
  def title: String
  def startMinutes: Int
  def basePrice: Double

  def formattedStartTime: String =
    val hours = startMinutes / 60
    val mins = startMinutes % 60
    f"$hours%02d:$mins%02d"

  def compare(that: Screening): Int = ???

case class StandardScreening(
  id: String,
  title: String,
  startMinutes: Int,
  basePrice: Double
) extends Screening

case class ImaxScreening(
  id: String,
  title: String,
  startMinutes: Int,
  basePrice: Double,
  subtitleLanguage: String,
  audioFormat: String
) extends Screening, Captioned, PremiumAudio:
  def featureSummary: String = ???
