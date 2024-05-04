class HydropowerPlant(id: String, maxOutput: Double, weatherData: WeatherData) extends EnergyPlant(id, maxOutput) {

  override def updateOutput(): Unit = {
    if (getStatus == "Running") {
      // The water flow rate is multiplied by an efficiency factor to simulate the power generation efficiency of a hydroelectric power plant.
      val efficiency = 0.8
      val potentialOutput = weatherData.getWaterFlow * maxOutput * efficiency
      currentOutput = Math.min(potentialOutput, maxOutput)
    } else {
      currentOutput = 0.0
    }
  }

}