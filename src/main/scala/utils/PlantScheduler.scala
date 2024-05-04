import java.time.LocalDate
import scala.collection.mutable
import scala.util.Random

class PlantScheduler(plantManager: PlantManager, alertSystem: AlertSystem, weatherData: WeatherData) {
  private val maxRunningDays = 30
  private var runningDays = mutable.Map[String, Int]()

  def initialize(startFraction: Double): Unit = {
    val allPlants = plantManager.getAllPlants.values.toList
    Random.shuffle(allPlants).take((allPlants.size * startFraction).toInt).foreach { plant =>
      plant.start()
      runningDays(plant.id) = 0
    }
  }

  def runDailyOperations(currentDate: LocalDate): Unit = {
    println(s"Simulating day for ${currentDate.toString}")
    weatherData.updateWeather()

    plantManager.getAllPlants.values.foreach { plant =>

      // Ensure that each power plant is properly initialized in runningDays
      if (!runningDays.contains(plant.id) && plant.getStatus == "Running") {
        runningDays(plant.id) = 0
      }

      if (plant.getStatus == "Running") {
        if (runningDays(plant.id) >= maxRunningDays) {
          plant.shutdown()
          println(s"Plant ${plant.id} shut down after $maxRunningDays days of continuous operation.")
          runningDays -= plant.id // Remove the entry as it's no longer running
        } else {
          runningDays(plant.id) += 1
        }
      }
      alertSystem.checkAlerts()
      alertSystem.simulateDeviceDamage()
    }
  }
}