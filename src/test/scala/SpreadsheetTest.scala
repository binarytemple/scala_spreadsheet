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



  "set numeric values, given a cell address 0:0" in new SimpleScope {
    s.assign("A1", 5)
    s.m.data(0)(0).numericalValue() must_== 5
  }

  "set numeric values, given a cell address A2 (row 1 col 0)" in new SimpleScope {
    s.assign("A2", 3)
    s.m.data(1)(0).numericalValue() must_== 3
  }

  "set numeric values, given a cell address B1 (row 0 col 1)" in new SimpleScope {
    s.assign("B1", 3)
    s.m.data(0)(1).numericalValue() must_== 3
  }

  "support extracting ranges for rows" in new SimpleScope {
    s.assign("A1", 5)
    s.m.getRowCol(RCOff(0,0)).numericalValue() must_== 5
    s.assign("B1", 3)
    s.m.getRowCol(RCOff(0,1)).numericalValue() must_== 3
    s.extractRange(CellRange(RCOff(0, 0), RCOff(0, 1))).map(_.numericalValue()) === List(5.0, 3.0)
  }

  "support extracting ranges for columns" in new SimpleScope {
    s.assign("A1", 5)
    s.m.data(0)(0).numericalValue() must_== 5
    s.assign("A2", 3)
    s.m.data(1)(0).numericalValue() must_== 3
    s.extractRange(CellRange(RCOff(0, 0), RCOff(1, 0))).map(_.numericalValue()) === List(5, 3)
  }

  "support extracting block ranges using tupple notation" in new SimpleScope {
    s.assign("A1", 1)
    s.assign("A2", 2)
    s.assign("B1", 3)
    s.assign("B2", 4)
    s.extractRange(CellRange(RCOff(0, 0), RCOff(1, 1))).map(_.numericalValue()) must containTheSameElementsAs(List(1.0d, 2.0d, 3.0d, 4.0d))
  }

  "support extracting block ranges using A1:B2 notation" in new SimpleScope {
    s.assign("A1", 1)
    s.assign("A2", 2)
    s.assign("B1", 3)
    s.assign("B2", 4)
    s.extractRange("A1:B2").map(_.numericalValue()) must containTheSameElementsAs(List(1.0d, 2.0d, 3.0d, 4.0d))
  }

  "render into a list of Strings" in new SimpleScope {
    s.assign("A1", 1)
    s.assign("A2", 2)
    s.assign("A3", 3)
    s.assign("A4", 4)

    s.m.getRowCol(RCOff(0,0)).numericalValue() must_== 1
    s.m.getRowCol(RCOff(1,0)).numericalValue() must_== 2
    s.m.getRowCol(RCOff(2,0)).numericalValue() must_== 3
    s.m.getRowCol(RCOff(3,0)).numericalValue() must_== 4

    s.render(0).replace(" ","") must_== "     |A         |B     |C         |D         |E         |F         |G         |H         |".replace(" ","")
    s.render(1).replace(" ","") must_== "1    |1         |       |          |          |          |          |          |          |".replace(" ","")
    s.render(2).replace(" ","") must_== "2    |2         |       |          |          |          |          |          |          |".replace(" ","")
    s.render(3).replace(" ","") must_== "3    |3         |       |          |          |          |          |          |          |".replace(" ","")
    s.render(4).replace(" ","") must_== "4    |4         |       |          |          |          |          |          |          |".replace(" ","")
  }


//    "render into a list of Strings" in new SimpleScope {
//      s.assign("A1", 1)
//      s.assign("A2", 2)
//      s.assign("A3", 3)
//      s.assign("A4", 4)
//      s.render(0).replace(" ","") must_== "     |A         |B     |C         |D         |E         |F         |G         |H         |".replace(" ","")
//      s.render(1).replace(" ","") must_== "1    |1         |3       |          |          |          |          |          |          |".replace(" ","")
//      s.render(2).replace(" ","") must_== "2    |2         |4       |          |          |          |          |          |          |".replace(" ","")
//    }
  }
}