package spreadsheet

import org.parboiled.scala._
import org.parboiled.scala.RecoveringParseRunner
import org.parboiled.errors.InvalidInputError
import org.parboiled.buffers.InputBuffer
import org.parboiled.support.MatcherPath
import org.parboiled.matchers._
import spreadsheet.Spreadsheet.CellRange

object QueryTermParser {

  lazy val qtp = new QueryTermParser {
    override val buildParseTree = true
  }

  /**
   * If parsing fails, this class will reveal where, and how.
   */
  case class CommandParseFailure(start: Int, end: Int, buffer: InputBuffer, delta: Int, options: List[String])

  sealed trait Command

  case object PrintRawCommand extends Command

  case object PrintCommand extends Command

  case object ExitCommand extends Command

  case class SetCommand(id: RCOff, value: Either[String, Formula]) extends Command

  case class GetCommand(id: RCOff) extends Command

  object Op extends Enumeration {
    type Op = Value
    val Sum = Value("SUM")
    val Count = Value("COUNT")
    val Max = Value("MAX")
    val Min = Value("MIN")

    def fromString(s: String) = {
      s match {
        case "SUM" => Op.Sum
        case "COUNT" => Op.Count
        case "MAX" => Op.Max
        case "MIN" => Op.Min
        case other => throw new UnsupportedOperationException(s"I don't understand $other")
      }
    }
  }

  case class Formula(op: Op.Value, cr: CellRange) {
    def evaluate()(implicit m: Model):Double = {
      /*filter(_.isNumeric).*/
      val input = Spreadsheet.extractRange(cr)(m).map(_.numericalValue())//.filter(_ < 0)
       op match {
        case Op.Sum => input.sum
        case Op.Count => input.count(_ > 0).toDouble
        case Op.Max => input.max
        case Op.Min => input.min
      }
    }
    override  def toString = {
      s"$op=(${cr.toString})"
    }
  }


  def parseFormula(input: String): Either[Throwable, Formula] = {
    try {
      val runner = RecoveringParseRunner(qtp.FormulaExtractor, 1000l)
      Right(runner.run(input).result.get)
    }
    catch {
      case t: Throwable => Left(t)
    }
  }

  /**
   *
   * @param input for example {{{=SUM(A1:A3)}}}
   * @return
   */
  def parseCommand(input: String): Either[CommandParseFailure, Command] = {

    val runner = ReportingParseRunner(qtp.CmdExtractor)
    try {
      Right(runner.run(input).result.get)
    }
    catch {
      case t: Throwable =>
        import scala.collection.JavaConversions._

        //On invalid input we try to resolve the logest posible sucessive token
        val collect = runner.inner.getParseErrors.toList.collectFirst {
          case i: InvalidInputError =>
            val options = i.getFailedMatchers.toList.map {
              mp: MatcherPath =>
                mp.element.matcher match {

                  case crm: CharRangeMatcher => Range(crm.cLow, crm.cHigh).toList.map(_.toChar).map(_.toString)

                  case cm: CharMatcher => mp.parent.element.matcher match {
                    case fsm: FirstOfStringsMatcher => fsm.strings.toList.map(new String(_))

                    case sm: StringMatcher => List(new String(sm.characters))
                    case _ => List(cm.character.toString)
                  }
                  case any: AnyOfMatcher =>
                    if (any.characters != null) {
                      any.characters.getChars.toList.map(_.toString)
                    }
                    else {
                      List.empty[String]
                    }

                  case other => List()
                }
            }.flatten
            CommandParseFailure(i.getStartIndex, i.getEndIndex, i.getInputBuffer, i.getIndexDelta, options.map(o => Utils.stripOverlapFromToken(input, o) ))
        }
        Left(collect.get)
    }
  }
}

class QueryTermParser extends Parser {

  import QueryTermParser._

  def Col: Rule1[Int] = rule {
    anyOf(ColLetters.toArray)
    "A" - "H"

  } ~> (x => x.head - 65)

  def Num: Rule1[Int] = rule {
    "1" - "9"
  } ~> (_.toInt)

  def Operation: Rule1[Op.Value] = rule {
    //The list of operation matches.. we extract from the Op enumeration
    val ops = Op.values.toList
    var ret = str(ops.head.toString)
    ops.tail.foreach(v => ret = ret | str(v.toString))
    ret
  } ~> Op.fromString

  def Term: Rule1[RCOff] = rule {
    Col ~ Num
  } ~~> ((r:Int,c:Int ) => RCOff(c -1,r))

  def Pair: Rule2[RCOff, RCOff] = rule {
    Term ~ ":" ~ Term
  }

  def FormulaExtractor = rule {
    "=" ~ Operation ~ "(" ~ Pair ~ ")"
  } ~~> ((o: Op.Value, a:RCOff, b:RCOff) => Formula(o, CellRange(a, b)))

  def AnythingToEnd: Rule1[String] = rule {
    oneOrMore(noneOf(Array('\n')))
  } ~> identity

  def SetCmd = rule {
    "SET" ~ " " ~ Term ~ " " ~ (FormulaExtractor | (!"=" ~ AnythingToEnd)) ~ EOI
  } ~~> ((rc:RCOff, s: Any) => s match {
    case f: Formula => SetCommand(rc, Right(f))
    case s: String => SetCommand(rc, Left(s))
    case other => throw new UnsupportedOperationException(other.toString)
  })

  def GetCmd: Rule1[GetCommand] = rule {
    "GET" ~ " " ~ Term ~ EOI
  } ~~> ((rc:RCOff) => GetCommand(rc))

  def PrintCmd: Rule1[PrintCommand.type] = rule {
    str("PRINT")
  } ~> ((s: String) =>  PrintCommand)

  def PrintRawCmd: Rule1[PrintRawCommand.type] = rule {
    str("PRINTRAW")
  } ~> ((s: String) =>  PrintRawCommand)

  def ExitCmd: Rule1[ExitCommand.type] = rule {
    str("EXIT")
  } ~> (_ => ExitCommand)

  def CmdExtractor: Rule1[Command] = rule {
    SetCmd | GetCmd | PrintCmd  | PrintRawCmd  | ExitCmd
  }
}
