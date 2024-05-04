import Main.dataCollector
import scala.util.Random

class AlertSystem(plantManager: PlantManager) {
  private val random = new Random()

  // Check the output of all plants and warn if it is less than 10% of the maximum output
  def checkAlerts(): Unit = {
    plantManager.getAllPlants.foreach { case (id, plant) =>
      if (plant.getStatus.toLowerCase == "running" && plant.getCurrentOutput < plant.getMaxOutput * 0.1) {
        println(s"Low output warning: $id")
      }
    }
  }

  // Simulated equipment damage, 1% probability of damage to any one piece of equipment per day
  def simulateDeviceDamage(): Unit = {
    plantManager.getAllPlants.foreach { case (id, plant) =>
      if (random.nextDouble() < 0.01) { // 1% probability of device damage
        plant.setDeviceStatus(DeviceStatus.Damaged)
        println(s"Device damaged: $id")
        dataCollector.collectData() // Collect data to document damage status
      }
    }
  }
}