import Main.dataCollector

import java.io.{BufferedWriter, FileWriter}
import java.time.LocalDate
import scala.io.Source

class SimulationRunner(initialDate: LocalDate, plantManager: PlantManager, plantScheduler: PlantScheduler, fileName: String = "historydata.csv") {
  private var currentDate: LocalDate = initialDate

  def runSimulation(days: Int): Unit = {
    val writer = new BufferedWriter(new FileWriter(fileName, true))
    try {
      // Check if the file is empty and add header
      val source = Source.fromFile(fileName)
      val isEmpty = source.getLines().isEmpty
      source.close()
      if (isEmpty) {
        // Add header if the file is empty
        writer.write("Date, Total Output, Efficiency\n")
      }

      (1 to days).foreach { _ =>
        plantScheduler.runDailyOperations(currentDate)

        val operatingPlants = plantManager.getAllPlants.values.filter(plant => plant.getStatus == "Running" && plant.getDeviceStatus == DeviceStatus.Normal)
        val totalOutput = operatingPlants.map(_.getCurrentOutput).sum
        val maxOutput = operatingPlants.map(_.getMaxOutput).sum
        val efficiency = if (maxOutput > 0) totalOutput / maxOutput else 0

        writer.write(s"$currentDate, $totalOutput, $efficiency\n")

        // Move to next day
        currentDate = currentDate.plusDays(1)
      }
    } finally {
      writer.close()
    }
  }
}


