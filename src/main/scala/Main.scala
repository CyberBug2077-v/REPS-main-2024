import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.concurrent.duration.FiniteDuration
import scala.io.StdIn.readLine
import scala.sys.exit
import scala.io.Source
import breeze.linalg._
import breeze.plot._
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartFrame
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.plot.CategoryPlot


import scala.collection.mutable


object Main extends App {
  val plantManager = new PlantManager()
  val weatherData = new WeatherData()
  val alertSystem = new AlertSystem(plantManager)
  val analyzer = new DataAnalyzer("historydata.csv")
  val dataCollector = new DataCollector(plantManager, "data.csv", weatherData)
  val plantScheduler = new PlantScheduler(plantManager, alertSystem, weatherData)

  // Initialize PlantScheduler, start 60% of plants
  plantScheduler.initialize(0.6)

  // Load initial data from CSV
  dataCollector.loadData()

  val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  private def printMenu(): Unit = {
    println("\n--- Renewable Energy Plant System ---")
    println("1. Add a new plant")
    println("2. Remove a plant")
    println("3. Start a plant")
    println("4. Shutdown a plant")
    println("5. Create a view for energy output")
    println("6. Show all plants status")
    println("7. Simulate running in a given period")
    println("8. Analyze collected data")
    println("0. Exit")
    println("--------------------------------------")
  }

  private var running = true

  while(running) {
    printMenu()

    val option = readLine("Select an option: ").trim.toInt

    option match {
      case 1 =>
        // Add a new plant
        val plantType = readLine("Enter the type of the plant (solar, wind, hydro): ").trim.toLowerCase
        val plantId = FacilityUtils.generateId(plantType, 5)
        val maxOutput = readLine("Enter the maximum output of the plant (in MW): ").trim.toDouble
        val plant = plantType match {
          case "solar" => new SolarPanel(plantId, maxOutput, weatherData)
          case "wind" => new WindTurbine(plantId, maxOutput, weatherData)
          case "hydro" => new HydropowerPlant(plantId, maxOutput, weatherData)
          case _ => throw new IllegalArgumentException("Plant type not support")
        }
        plantManager.addPlant(plant)
        dataCollector.collectData() // update information after adding new plant
        println(s"Added a new $plantType plant with ID: $plantId")

      case 2 =>
        // Remove a plant
        val plantId = readLine("Enter the ID of the plant to remove: ").trim
        plantManager.removePlant(plantId)
        dataCollector.collectData() // update information after removing a plant
        println(s"Removed the plant with ID: $plantId")

      case 3 =>
        // Start a plant
        val plantId = readLine("Enter the ID of the plant to start: ").trim
        plantManager.startPlant(plantId)
        dataCollector.collectData() // update information after starting a plant
        println(s"Started the plant with ID: $plantId")

      case 4 =>
        // Shutdown a plant
        val plantId = readLine("Enter the ID of the plant to shutdown: ").trim
        plantManager.shutdownPlant(plantId)
        dataCollector.collectData() // update information after shutting down a plant
        println(s"Shutdown the plant with ID: $plantId")

      case 5 =>
        dataCollector.collectData()
        // Create a view based on energy type
        // Create a dataset to store the total output of each type of plant
        val dataset = new DefaultCategoryDataset()
        val outputByType = plantManager.getAllPlants.values.groupBy(plant => {
          val typeName = plant.getClass.getSimpleName.replace("Plant", "")
          println(s"Plant type: $typeName, Output: ${plant.getCurrentOutput}")
          typeName
        }).map {
          case (typeName, plants) => typeName -> plants.map(_.getCurrentOutput).sum
        }

        outputByType.foreach { case (typeOfPlant, output) =>
          println(s"Type: $typeOfPlant, Output: $output MW")
          if (output != 0) {
            dataset.addValue(output, "Output", typeOfPlant)
          }
        }

        // Creating a Bar Chart
        val chart = ChartFactory.createBarChart(
          "Energy Output by Type",
          "Type of Plant",
          "Energy Output (MW)",
          dataset,
          PlotOrientation.VERTICAL,
          true, // Whether to display the legend
          true, // Whether to generate tools
          false
        )

        val plot = chart.getPlot.asInstanceOf[CategoryPlot]
        val rangeAxis = plot.getRangeAxis
        rangeAxis.setAutoRange(true) // Enables automatic ranging

        // Creating and setting up a chart window
        val frame = new ChartFrame("Energy Output Distribution", chart)
        frame.pack()
        frame.setVisible(true)

      case 6 =>
        // Show all facilities
        // Update the data first to ensure that the latest information is displayed
        dataCollector.collectData()
        // Get all power plant information
        val allPlants = plantManager.getAllPlants
        // Check for power plant data
        if (allPlants.nonEmpty) {
          println("All Facilities:")
          // Iterate through all power plants and print detailed information for each plant
          allPlants.foreach { case (id, plant) =>
            val typeOfPlant = plant.getClass.getSimpleName.replaceAll("Plant$", "")
            val status = plant.getStatus
            val output = plant.getCurrentOutput
            println(s"ID: $id, Type: $typeOfPlant, Status: $status, Output: $output MW")
          }
        } else {
          println("No facilities found.")
        }

      case 7 =>
        dataCollector.collectData()
        // Simulate running
        println("Enter the number of days to simulate:")
        val days = readLine().trim.toInt

        if (days <= 0) {
          throw new IllegalArgumentException("Number of days must be greater than zero.")
        }

        val source = Source.fromFile("historydata.csv")
        val lines = source.getLines().toList
        source.close()
        val nonEmptyLines = lines.filter(_.trim.nonEmpty)
        val startDate = if (nonEmptyLines.isEmpty) {
          LocalDate.of(2023, 5, 1)
        } else {
          val lastDate = LocalDate.parse(nonEmptyLines.last.split(",")(0).trim)
          lastDate.plusDays(1)
        }

        val simulationRunner = new SimulationRunner(startDate, plantManager, plantScheduler)
        simulationRunner.runSimulation(days)
        println(s"Simulation of $days days completed.")

      case 8 =>
        // Analyze data
        // Getting and validating date ranges
        println("Please enter the start date (format: yyyy-mm-dd): ")
        val startDate = LocalDate.parse(scala.io.StdIn.readLine(), formatter)
        println("Please enter the end date (format: yyyy-mm-dd): ")
        val endDate = LocalDate.parse(scala.io.StdIn.readLine(), formatter)

        if (startDate.isAfter(endDate)) {
          println("Start date must be before or the same as end date.")
        } else {
          // Analyze data on a daily, weekly and monthly basis
          analyzer.analyze("day", startDate, endDate)
          analyzer.analyze("week", startDate, endDate)
          analyzer.analyze("month", startDate, endDate)
        }

      case 0 =>
        println("Exiting the program...")
        running = false
        exit(0)

      case _ =>
        println("Invalid option. Please check the menu and give a valid option.")
    }
  }



}