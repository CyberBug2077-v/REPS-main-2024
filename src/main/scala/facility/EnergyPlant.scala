abstract class EnergyPlant(val id: String, val maxOutput: Double) {
  protected var currentOutput: Double = 0.0
  private var deviceStatus: DeviceStatus = DeviceStatus.Normal
  private var status: String = "Shutdown"

  def getMaxOutput: Double = maxOutput
  def getCurrentOutput: Double = currentOutput
  def getDeviceStatus: DeviceStatus = deviceStatus
  def getStatus: String = status

  def setStatus(newStatus: String): Unit = {
    println(s"Setting status of plant $id to $newStatus")
    status = newStatus
  }

  def setDeviceStatus(newStatus: DeviceStatus): Unit = {
    println(s"Setting device status of plant $id to $newStatus")
    deviceStatus = newStatus
  }

  def start(): Unit = {
    try {
      println(s"Starting plant $id...")
      status = "Running"
      updateOutput()
      println(s"Plant $id is now running with output $currentOutput MW.")
    } catch {
      case e: Exception =>
        println(s"Failed to start plant $id: ${e.getMessage}")
        setDeviceStatus(DeviceStatus.Damaged)
    }
  }

  def shutdown(): Unit = {
    try {
      println(s"Shutting down plant $id...")
      status = "Shutdown"
      currentOutput = 0.0
      println(s"Plant $id has been shut down.")
    } catch {
      case e: Exception =>
        println(s"Failed to shut down plant $id: ${e.getMessage}")
        setDeviceStatus(DeviceStatus.Damaged)
    }
  }

  def updateOutput(): Unit
}

sealed trait DeviceStatus
object DeviceStatus {
  case object Normal extends DeviceStatus
  case object Damaged extends DeviceStatus
}
