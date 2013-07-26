import java.io.PrintWriter
import java.nio.charset.Charset
import java.nio.file._
import jline.console.ConsoleReader
import spreadsheet.QueryTermParser._
import spreadsheet.QueryTermParser.Formula
import spreadsheet.QueryTermParser.SetCommand
import spreadsheet.{RCOff, Spreadsheet, QueryTermParser}
import scalaz._
import Scalaz._

object TerminalMain {

  type Token = String

  case class Lnode(t: Token, leafs: Lnode*)

  def main(args: Array[String]) {
    val color = true
    val s = new Spreadsheet

    try {
      val reader: ConsoleReader = new ConsoleReader
      reader.setPrompt("prompt> ")

      val c = if (args.lift(1) == "debug".some) {
        new SpreadsheetCompleter with TmpLogger
    }
      else{
        new SpreadsheetCompleter with NoopLogger
    }
      reader.addCompleter(c)
      reader.setHistoryEnabled(true)

      var line: String = null
      val out = new PrintWriter(reader.getOutput)
      while ( {
        line = reader.readLine
        line
      } != null) {

        if (color) {

          QueryTermParser.parseCommand(line) match {
            case Left(err) => out.println(s"Error parsing '$line'")
            case Right(cmd: Command) => cmd match {
              case PrintCommand =>
                s.render.foreach {
                  l => out.println(l)
                }
                out.flush()

              case PrintRawCommand =>
                s.renderRaw.foreach {
                  l => out.println(l)
                }
                out.flush()

              case SetCommand(id: RCOff, value: Either[String, Formula]) =>
                s.assign(id, value)

              case GetCommand(id: RCOff) =>
                out.println(s.get(id))

              case ExitCommand =>
                sys.exit()

            }
          }
          out.println("\u001B[33m======>\u001B[0m\"" + line + "\"")
        }
        else {
          out.println("======>\"" + line + "\"")
        }
        out.flush()
      }
    }
    catch {
      case t: Throwable => {
        t.printStackTrace()
      }
    }
  }
}