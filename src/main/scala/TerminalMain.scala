
import spreadsheet.Spreadsheet


object TerminalMain {
  def main(args: Array[String]) {


    val s = new Spreadsheet()

    val cmds = List(
      "A1" -> "2",
      "A2" -> "foobar",
      "A2" -> "1",
      "A3" -> "foobar",
      "A4" -> "1",
      "A5" -> "=MAX(A1:A4)",
      "B1" -> "2",
      "C1" -> "2",
      "E7" -> "SUM COSTS",
      "F7" -> "=SUM(A1:A5)"
    )

    println("Executing cmds:")
    cmds.foreach {
      c =>
        println(c)
        s.assign(c._1, Left(c._2))
    }
    s.render.foreach(println)



  }

}
