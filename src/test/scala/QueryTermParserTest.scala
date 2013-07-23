import spreadsheet.{RCOff, Spreadsheet, QueryTermParser}
import QueryTermParser.{Formula, Op}
import Spreadsheet.CellRange
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

class QueryTermParserTest extends Specification with Mockito {
  "The query term parser" should {
    "parse a SUM formula" in {
      val formula: Either[Throwable, QueryTermParser.Formula] = QueryTermParser.parseFormula( """=SUM(A1:B3)""")
      formula must_== Right(Formula(Op.Sum, CellRange(RCOff("A1"), RCOff("B3"))))
    }
        "parse a MAX formula" in {
          QueryTermParser.parseFormula( """=MAX(A1:B3)""") must_== Right(Formula(Op.Max, CellRange(RCOff("A1"), RCOff("B3"))))
        }
        "parse a MIN formula" in {
          QueryTermParser.parseFormula( """=MIN(A1:B3)""") must_== Right(Formula(Op.Min, CellRange(RCOff("A1"), RCOff("B3"))))
        }
        "parse a COUNT formula" in {
          QueryTermParser.parseFormula( """=COUNT(A1:B3)""") must_== Right(Formula(Op.Count, CellRange(RCOff("A1"), RCOff("B3"))))
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