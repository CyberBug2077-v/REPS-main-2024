import java.util.{Timer, TimerTask}
import java.time.LocalDateTime
import scala.util.Random

class WeatherData {
  private val random = new Random()

  // Initialize weather data
  private var sunlightIntensity: Double = initialSunlightIntensity
  private var windSpeed: Double = initialWindSpeed
  private var waterFlow: Double = initialWaterFlow

  // Functions that define initial values for weather conditions
  private def initialSunlightIntensity: Double = {
    val hour = LocalDateTime.now().getHour
    if (hour >= 6 && hour <= 18) random.nextDouble() * (1 - Math.abs(hour - 12) / 6.0) else 0.1 * random.nextDouble()
  }
  private def initialWindSpeed: Double = {
    val month = LocalDateTime.now().getMonthValue
    if (month <= 2 || month >= 11) 0.5 + random.nextDouble() * 0.5 else 0.1 + random.nextDouble() * 0.3
  }
  private def initialWaterFlow: Double = {
    val month = LocalDateTime.now().getMonthValue
    if (month >= 3 && month <= 5 || month >= 9 && month <= 11) 0.3 + random.nextDouble() * 0.7 else 0.1 + random.nextDouble() * 0.2
  }

  // Calculate weather data based on current time
  private def updateWeatherData(): Unit = {
    val now = LocalDateTime.now()
    sunlightIntensity = calculateSunlightIntensity(now)
    windSpeed = calculateWindSpeed(now)
    waterFlow = calculateWaterFlow(now)
  }

  // Calculate the sunlight intensity at the current time
  private def calculateSunlightIntensity(time: LocalDateTime): Double = {
    val hour = time.getHour
    // Adjust sunlight intensity according to time of day (sunrise to sunset)
    if (hour >= 6 && hour <= 18) Math.max(0.1, 1 - Math.abs(hour - 12) / 6.0) else 0.1
  }

  // Calculate wind speed based on month
  private def calculateWindSpeed(time: LocalDateTime): Double = {
    val month = time.getMonthValue
    // High wind speeds in winter (November-February), low wind speeds the rest of the year
    if (month <= 2 || month >= 11) 0.5 + random.nextDouble() * 0.5 else 0.1 + random.nextDouble() * 0.3
  }

  // Calculation of water flow based on month
  private def calculateWaterFlow(time: LocalDateTime): Double = {
    val month = time.getMonthValue
    // High water flow during the rainy season (March-May, September-November)
    if (month >= 3 && month <= 5 || month >= 9 && month <= 11) 0.3 + random.nextDouble() * 0.7 else 0.1 + random.nextDouble() * 0.2
  }

  def getSunlightIntensity: Double = sunlightIntensity
  def getWindSpeed: Double = windSpeed
  def getWaterFlow: Double = waterFlow

  // Using Timer to update weather data on a regular basis
  private val timer = new Timer()
  timer.schedule(new TimerTask {
    override def run(): Unit = updateWeatherData()
  }, 0, 60 * 1000) // Updated every minute

  def updateWeather(): Unit = {
    sunlightIntensity = calculateSunlightIntensity(LocalDateTime.now())
    windSpeed = calculateWindSpeed(LocalDateTime.now())
    waterFlow = calculateWaterFlow(LocalDateTime.now())
  }
}
