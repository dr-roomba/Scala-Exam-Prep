sealed trait AuditIssue:
  def unitCode: String
  def message: String

case class OverweightPallet(
  unitCode: String,
  actualKg: Double,
  maxLoadKg: Double
) extends AuditIssue:
  def message: String =
    f"Pallet $unitCode overweight: $actualKg%.2f kg exceeds $maxLoadKg%.2f kg limit"

case class EmptyPallet(unitCode: String) extends AuditIssue:
  def message: String =
    s"Pallet $unitCode is empty"

abstract class AuditSink:
  def record(issue: AuditIssue): Unit

class ConsoleAuditSink extends AuditSink:
  def record(issue: AuditIssue): Unit = ???

class MemoryAuditSink extends AuditSink:
  def record(issue: AuditIssue): Unit = ???
  def recorded: Vector[AuditIssue] = ???
