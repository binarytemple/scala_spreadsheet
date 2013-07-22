
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import spreadsheet.Model.Cell
import spreadsheet.QueryTermParser.Formula
import spreadsheet.{QueryTermParser, Spreadsheet, Settings, Model}

class CellTest extends Specification with Mockito {

  trait CellScope extends Scope {
    implicit val s = new Settings
    implicit val m = new Model {
      override def generateDefault(): Array[Array[Cell]] = Array(
        Array(
          Cell(Left("50")), Cell(Left("5"))
        ),
        Array(
          Cell(Left("9")),  Cell(Left("8"))
        )
      )
    }
  }

  "An individual cell" should {
    "accept values, converting them to numbers" in new CellScope {
      val c = Cell(Left("1.4"))
      c.value must_== Left("1.4")
      c.numericalValue() must_== 1.4
    }

    "Evaluate functions" in new CellScope {
      //A quick sanity check
      Spreadsheet.extractRange("A1:B2").map(_.numericalValue) must containAllOf(Seq(50, 5, 9, 8))

      QueryTermParser.parseFormula("=MIN(A1:B2)") must beRight.like  {
        case f:Formula => Cell(Right(f)).numericalValue() must_== 5
      }

      QueryTermParser.parseFormula("=MIN(A1:B2)") must beRight.like  {
        case f:Formula =>
          Cell(Right(f)).numericalValue() must_== 5
          Cell(Right(f)).printable() must_== "5.0"
      }

      QueryTermParser.parseFormula("=MAX(A1:B2)") must beRight.like  {
        case f:Formula =>
          Cell(Right(f)).numericalValue() must_== 50
          Cell(Right(f)).printable() must_== "50.0"
      }

      QueryTermParser.parseFormula("=COUNT(A1:B2)") must beRight.like  {
        case f:Formula =>
          Cell(Right(f)).numericalValue() must_== 4

      }
      QueryTermParser.parseFormula("=SUM(A1:B2)") must beRight.like  {
        case f:Formula =>
          Cell(Right(f)).numericalValue() must_== 72
      }
    }
  }
}