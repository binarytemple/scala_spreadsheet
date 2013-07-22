import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import spreadsheet._
import Spreadsheet.CellRange

class SpreadsheetTest extends Specification with Mockito {

  trait SimpleScope extends Scope {
    val s = new Spreadsheet
    implicit val model = mock[Model]
  }

  "The spreadsheet" should {

    "convert cell addresses to offset tupples" in {
      Spreadsheet.c2t("A1") must_==(0, 0)
      Spreadsheet.c2t("B5") must_==(1, 4)
      Spreadsheet.c2t("ZZ") must throwA[NumberFormatException]
      Spreadsheet.c2t("Z1") should_==(25, 0)
    }
  }

  "set numeric values, given a cell address" in new SimpleScope {
    s.assign("A1", 5)
    s.m.data(0)(0).numericalValue() must_== 5
    s.assign("A2", 3)
    s.m.data(0)(1).numericalValue() must_== 3
  }

  "support extracting ranges for rows" in new SimpleScope {
    s.assign("A1", 5)
    s.m.data(0)(0).numericalValue() must_== 5
    s.assign("B1", 3)
    s.m.data(1)(0).numericalValue() must_== 3
    s.extractRange(CellRange((0, 0), (1, 0))).map(_.numericalValue()) === List(5, 3)
  }

  "support extracting ranges for columns" in new SimpleScope {
    s.assign("A1", 5)
    s.m.data(0)(0).numericalValue() must_== 5
    s.assign("A2", 3)
    s.m.data(0)(1).numericalValue() must_== 3
    s.extractRange(CellRange((0, 0), (0, 1))).map(_.numericalValue()) === List(5, 3)
  }

  "support extracting block ranges using tupple notation" in new SimpleScope {
    s.assign("A1", 1)
    s.assign("A2", 2)
    s.assign("B1", 3)
    s.assign("B2", 4)
    s.extractRange(CellRange((0, 0), (1, 1))).map(_.numericalValue()) === List(1, 2, 3, 4)
  }

  "support extracting block ranges using A1:B2 notation" in new SimpleScope {
    s.assign("A1", 1)
    s.assign("A2", 2)
    s.assign("B1", 3)
    s.assign("B2", 4)
    s.extractRange("A1:B2").map(_.numericalValue()) === List(1, 2, 3, 4)
  }

  "render into a list of Strings" in new SimpleScope {
    s.assign("A1", 1)
    s.assign("A2", 2)
    s.assign("B1", 3)
    s.assign("B2", 4)
    s.render(0) must_== "     |A         |B         |C         |D         |E         |F         |G         |H         |"
    s.render(1) must_== "1    |1.0       |3.0       |          |          |          |          |          |          |"
    s.render(2) must_== "2    |2.0       |4.0       |          |          |          |          |          |          |"
  }
}