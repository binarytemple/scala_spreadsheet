package spreadsheet

import spreadsheet.QueryTermParser.Formula

/**
 * Model of a spreadsheet containing 8 columns and ten rows.
 * The columns are labeled A-H, and the rows as 1-10
 */
object Model {


  /**
   * A class to represent a spreadsheet cell.
   * It will require methods that allow it to be assigned a numerical value,
   * to be emptied, and to return both a string representation and a numerical representation. Write test code that tests
   * the behaviour of the cell class.
   */
  case class Cell(value: Either[String, Formula]) {

    val OnlyNum = """^-?[0-9]*(\.?[0-9]+)$""".r

    def isNumeric(s: String) = s match {
      case OnlyNum(_) => true
      case other => false
    }

    def displayable(): String = {
      value match {
        case Left(s) => s
        case Right(f) => f.toString
      }
    }

    def numericalValue()(implicit m: Model): Double = {
      try {
        value match {
          case Left(s) if isNumeric(s)=> s.toDouble
          case Left(s)  => 0
          case Right(f) => f.evaluate()
        }
      } catch {
        case t: Throwable =>
          System.err.println(s"$this ${ t.getMessage}")
          0
      }
    }

    def printable()(implicit m: Model, settings: Settings): String = {
      val n = numericalValue()
      val ret = if(n == 0) "" else n.toString
//      val ret = {
//
//        value match {
//          case Left(s: String) if isNumeric(s) => numericalValue()
//          case Right(f) => numericalValue()
//        }

//        if (isNumeric(value) || value.startsWith("=")) {
//          val ret: Double = numericalValue()
//          if (ret == 0)
//            ""
//          else
//            ret.toString
//        }
//        else {
//          value
//        }
//      }
      ret.substring(0, Math.min(settings.CellWidth, ret.length)).trim
    }
  }

  object Cell{
    def apply(value: String): Cell ={
      new Cell(Left(value))
    }

    def apply(value: Formula): Cell ={
      new Cell(Right(value))
    }
  }

}

class Model {

  import Model._

  def generateDefault() = Range(0, 10).toArray.map(c => Range(0, 8).toArray.map(r => Cell(Left("0"))  ))

  val data: Array[Array[Cell]] = generateDefault()

  def getRow(i: Int) = data(i)
}
