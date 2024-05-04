class WindTurbine(id: String, maxOutput: Double, weatherData: WeatherData) extends EnergyPlant(id, maxOutput) {
  override def updateOutput(): Unit = {
    if (getStatus == "Running") {
      val efficiency = 0.65 // Assuming 65% efficiency based on wind conditions
      // Assuming wind conditions with an efficiency of 65
      val potentialOutput = Math.min(weatherData.getWindSpeed, 25) * maxOutput * efficiency
      // Make sure the output does not exceed the max output
      currentOutput = Math.min(potentialOutput, maxOutput)
    } else {
      currentOutput = 0.0
    }
  }
}