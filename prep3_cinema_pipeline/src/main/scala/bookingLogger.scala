trait BookingLogger:
  def log(message: String): Unit

class MemoryLogger extends BookingLogger:
  private var _entries: Vector[String] = Vector.empty

  def log(message: String): Unit =
    _entries = _entries :+ message

  def entries: Vector[String] = _entries

object SilentLogger extends BookingLogger:
  def log(message: String): Unit = ???
