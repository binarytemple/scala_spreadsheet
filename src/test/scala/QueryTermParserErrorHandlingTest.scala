import spreadsheet.QueryTermParser
import QueryTermParser.CommandParseFailure
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import spreadsheet.QueryTermParser

class QueryTermParserErrorHandlingTest extends Specification with Mockito {

  "The query term parser, parse and report possible completions for " should {

    "GET command followed by a space" in {
      QueryTermParser.parseCommand( """GET """) must beLeft.like {
        case in@CommandParseFailure(a, b, c, d, options) =>
          options must containAllOf(Range('A', 'H').map(_.toChar.toString))
      }
    }

    "GET command with incomplete address" in {
      QueryTermParser.parseCommand( """GET A:""") must beLeft.like {
        case in@CommandParseFailure(a, b, c, d, options) =>
          options must containAllOf(Range('1', '9').map(_.toChar.toString))
      }
    }

    "SET command with function" in {
      QueryTermParser.parseCommand( """SET A9 =""") must beLeft.like {
        case in@CommandParseFailure(a, b, c, d, options) =>
          options must containAllOf(List(
            "SUM",
            "COUNT",
            "MAX",
            "MIN"
          ))
      }
    }

    "give correct options for a completely empty input" in {
      QueryTermParser.parseCommand("") must beLeft.like {
        case in@CommandParseFailure(a, b, c, d, options) =>
          options must containTheSameElementsAs(List(
            "GET", "SET", "PRINT"
          ))
      }
    }

    "give correct options for a partial command" in {
      val p = QueryTermParser.parseCommand("P")
      p must beLeft.like {
        case in@CommandParseFailure(start, end, c, d, options) =>
          options must containTheSameElementsAs(List(
            "RINT"
          ))
          start must_== 1
          end must_== 2
      }
    }

//    "give correct options for a corrupt command" in {
//      val p = QueryTermParser.parseCommand("P")
//      p must beLeft.like {
//        case in@CommandParseFailure(start, end, c, delta, options) =>
//          options must containTheSameElementsAs(List(
//
//          ))
//          start must_== 1
//          end must_== 2
//      }
// }

      "give correct options for a corrupt command" in {
        val p = QueryTermParser.parseCommand("P")
        p must beLeft.like {
          case in@CommandParseFailure(start, end, c, delta, options) =>
            options must containTheSameElementsAs(List(
              "RINT"
            ))
            start must_== 1
            end must_== 2
        }
      }
    }
  }
