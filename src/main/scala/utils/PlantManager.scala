import scala.collection.mutable

class PlantManager {
  // Use mutable.Map to store information about energy power plants
  private val plants: mutable.Map[String, EnergyPlant] = mutable.Map()

  /**
   * Adds a new power plant.
   *
   * @param plant The plant to be added.
   */
  def addPlant(plant: EnergyPlant): Unit = {
    plants.get(plant.id) match {
      case Some(_) => throw new IllegalArgumentException("A plant with the same ID already exists.")
      case None => plants += (plant.id -> plant)
    }
  }

  /**
   * Deletes the power plant with the specified ID.
   *
   * @param id The ID of the power plant to be deleted.
   */
  def removePlant(id: String): Unit = {
    plants.get(id) match {
      case Some(_) => plants -= id
      case None => throw new IllegalArgumentException("The plant does not exist.")
    }
  }

  /**
   * Get the power plant by ID.
   *
   * @param id The ID of the power plant.
   * @return Returns the corresponding power plant, if it exists.
   */
  def getPlant(id: String): Option[EnergyPlant] = plants.get(id)

  /**
   * Get information about all power plants.
   *
   * @return Returns a mapping of all power plants.
   */
  def getAllPlants: Map[String, EnergyPlant] = plants.toMap

  /**
   * Shut down the power plant with the specified ID.
   *
   * @param id The ID of the power plant to be shut down.
   */
  def shutdownPlant(id: String): Unit = {
    getPlant(id).fold(
      throw new IllegalArgumentException("The plant does not exist.")
    )(_.shutdown())
  }

  /**
   * Starts the power plant with the specified ID.
   *
   * @param id The ID of the power plant to be started.
   */
  def startPlant(id: String): Unit = {
    getPlant(id).fold(
      throw new IllegalArgumentException("The plant does not exist.")
    )(_.start())
  }

  /**
   * Calculates the total output of all power plants.
   *
   * @return Returns the current total output of all power plants.
   */
  def getTotalOutput: Double = plants.values.map(_.getCurrentOutput).sum
}