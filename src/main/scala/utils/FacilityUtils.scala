object FacilityUtils {

  def generateId(prefix: String, length: Int): String = {
    val random = new scala.util.Random()
    val characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val id = (1 to length).map(_ => characters(random.nextInt(characters.length))).mkString
    prefix + id
  }

}
