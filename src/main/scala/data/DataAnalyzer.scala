import scala.io.Source
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class DataAnalyzer(fileName: String) {
  private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  // Get data within a specified time period, grouped according to different time units
  private def getData(period: String, startDate: LocalDate, endDate: LocalDate): Map[LocalDate, List[Double]] = {
    val source = Source.fromFile(fileName)
    try {
      val groupedData = source.getLines().drop(1).map { line =>
        val fields = line.split(",").map(_.trim)
        val date = LocalDate.parse(fields(0), dateFormat)
        val output = fields(1).toDouble
        (date, output)
      }.toList.filter { case (date, _) =>
        !date.isBefore(startDate) && !date.isAfter(endDate)
      }.groupBy { case (date, _) =>
        period match {
          case "day" => date
          case "week" => date.`with`(java.time.DayOfWeek.MONDAY)
          case "month" => date.`with`(java.time.temporal.TemporalAdjusters.firstDayOfMonth())
        }
      }.mapValues(_.map(_._2)).toMap

      groupedData
    } finally {
      source.close()
    }
  }

  def analyze(period: String, startDate: LocalDate, endDate: LocalDate): Unit = {
    val data = getData(period, startDate, endDate)

    if (data.nonEmpty) {
      // Sorting the data by the period start date
      val sortedData = data.toSeq.sortBy(_._1)

      sortedData.foreach { case (periodStart, values) =>
        val mean = values.sum / values.size
        val sortedValues = values.sorted
        val median = if (values.size % 2 == 1) sortedValues(values.size / 2) else (sortedValues(values.size / 2 - 1) + sortedValues(values.size / 2)) / 2.0
        val mode = values.groupBy(identity).mapValues(_.size).maxBy(_._2)._1
        val range = values.max - values.min
        val midRange = (values.max + values.min) / 2

        println(s"Analysis for $period starting $periodStart:")
        println(f"Average output (Mean): $mean%.2f")
        println(f"Median output: $median%.2f")
        println(f"Most common output (Mode): $mode%.2f")
        println(f"Output range: $range%.2f")
        println(f"Midrange of output: $midRange%.2f")
      }
    } else {
      println(s"No data found from $startDate to $endDate.")
    }
  }
}

