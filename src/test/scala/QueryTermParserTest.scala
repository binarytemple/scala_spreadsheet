import spreadsheet.{Spreadsheet, QueryTermParser}
import QueryTermParser.{Formula, Op}
import Spreadsheet.CellRange
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

class QueryTermParserTest extends Specification with Mockito {
  "The query term parser" should {
    "parse a SUM formula" in {
      QueryTermParser.parseFormula( """=SUM(A1:B3)""") must_== Right(Formula(Op.Sum, CellRange((0, 0), (1, 2))))
    }
    "parse a MAX formula" in {
      QueryTermParser.parseFormula( """=MAX(A1:B3)""") must_== Right(Formula(Op.Max, CellRange((0, 0), (1, 2))))
    }
    "parse a MIN formula" in {
      QueryTermParser.parseFormula( """=MIN(A1:B3)""") must_== Right(Formula(Op.Min, CellRange((0, 0), (1, 2))))
    }
    "parse a COUNT formula" in {
      QueryTermParser.parseFormula( """=COUNT(A1:B3)""") must_== Right(Formula(Op.Count, CellRange((0, 0), (1, 2))))
    }

    "correctly parse a PRINT command" in {
      QueryTermParser.parseCommand("PRINT") must beRight
    }

    "correctly parse a GET command" in {
      QueryTermParser.parseCommand("GET A1") must beRight
    }

    "correctly parse a SET (string) command" in {
      QueryTermParser.parseCommand("SET A1 foooo") must beRight
    }

    "correctly parse a SET (number) command" in {
      QueryTermParser.parseCommand("SET A1 100000.0000") must beRight
    }

    "correctly parse a SET (formula) command" in {
      QueryTermParser.parseCommand("SET A1 =SUM(A2:A5)") must beRight
    }
  }
}