import java.io.{File, PrintWriter}
import scala.io.Source
import scala.util.control.NonFatal
import scala.util.Try

class DataCollector(plantManager: PlantManager, fileName: String, weatherData: WeatherData) {

  // Loads data from the specified CSV file and initializes plants based on that data.
  def loadData(): Unit = {
    Try(Source.fromFile(fileName)).toOption.foreach { source =>
      try {
        source.getLines().drop(1).foreach(processLine)
      } finally {
        source.close()
      }
    }
  }

  /**
   * Processes a single line from the CSV file, creating and configuring a plant object.
   *
   * @param line A string representing a single line of CSV file.
   */
  private def processLine(line: String): Unit = {
    val fields = line.split(",").map(_.trim)
    if (fields.length >= 5) {
      val (plantId, facilityType, status, maxOutput, deviceStatus) = (fields(0), fields(1), fields(2), fields(4).toDouble, fields(5))
      val plant = createPlant(facilityType, plantId, maxOutput)
      plant.setStatus(status)
      plant.setDeviceStatus(parseDeviceStatus(deviceStatus))
      plantManager.addPlant(plant)
    }
  }

  /**
   * Creates a plant object based on the facility type.
   *
   * @param facilityType A string indicating the type of the plant (Solar Panel, Wind Turbine, or Hydropower Plant).
   * @param id           Unique identifier for the plant.
   * @param maxOutput    The maximum output capacity of the plant.
   * @return Returns an instantiated EnergyPlant object.
   */
  private def createPlant(facilityType: String, id: String, maxOutput: Double): EnergyPlant = facilityType match {
    case "SolarPanel" => new SolarPanel(id, maxOutput, weatherData)
    case "WindTurbine" => new WindTurbine(id, maxOutput, weatherData)
    case "Hydropower" => new HydropowerPlant(id, maxOutput, weatherData)
    case _ => throw new IllegalArgumentException(s"Invalid facility type: $facilityType")
  }

  /**
   * Parses the device status from a string.
   *
   * @param status A string representing the device status.
   * @return Returns a DeviceStatus enumeration value.
   */
  private def parseDeviceStatus(status: String): DeviceStatus = status match {
    case "Normal" => DeviceStatus.Normal
    case "Damaged" => DeviceStatus.Damaged
    case _ => throw new IllegalArgumentException(s"Invalid device status: $status")
  }

  // Updates the CSV file with the current data from all plants managed by PlantManager.
  def collectData(): Unit = {
    plantManager.getAllPlants.foreach { case (_, plant) =>
      plant.updateOutput()
    }
    updateDataFile()
  }

  // Writes the current state of all plants into the CSV file.
  private def updateDataFile(): Unit = {
    Try(new PrintWriter(new File(fileName))).toOption.foreach { writer =>
      try {
        writer.println("Plant ID, Facility Type, Status, Current Output, Max Output, Device Status")
        plantManager.getAllPlants.foreach { case (id, plant) =>
          val line = List(id, plant.getClass.getSimpleName.replace("Plant", "").trim, plant.getStatus,
            plant.getCurrentOutput.toString, plant.getMaxOutput.toString, plant.getDeviceStatus.toString)
            .mkString(", ")
          writer.println(line)
        }
      } finally {
        writer.close()
      }
    }
  }
}

