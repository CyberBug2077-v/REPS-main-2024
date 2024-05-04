class SolarPanel(id: String, maxOutput: Double, weatherData: WeatherData) extends EnergyPlant(id, maxOutput) {
  override def updateOutput(): Unit = {
    if (getStatus == "Running") {
      // The effect of sunlight intensity on power generation is adjusted by an adaptation factor
      val efficiency = 0.75 // Assuming 75% efficiency based on sunlight intensity
      val potentialOutput = weatherData.getSunlightIntensity * maxOutput * efficiency
      currentOutput = Math.min(potentialOutput, maxOutput)
    } else {
      currentOutput = 0.0
    }
  }

}