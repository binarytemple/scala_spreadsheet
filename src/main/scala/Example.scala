import java.io.PrintWriter
import java.nio.charset.Charset
import java.nio.file._
import java.util
import jline.console.completer.Completer
import jline.console.ConsoleReader
import spreadsheet.QueryTermParser._
import spreadsheet.QueryTermParser.Formula
import spreadsheet.QueryTermParser.SetCommand
import spreadsheet.{Spreadsheet, QueryTermParser}

object Example {

  type Token = String

  case class Lnode(t: Token, leafs: Lnode*)


  def main(args: Array[String]) {
    val color = true

    val path = FileSystems.getDefault().getPath("/tmp/access.log")
    val debug = Files.newBufferedWriter(path, Charset.defaultCharset(), StandardOpenOption.SYNC, StandardOpenOption.APPEND, StandardOpenOption.CREATE)

    val s = new Spreadsheet

    try {
      val reader: ConsoleReader = new ConsoleReader
      reader.setPrompt("prompt> ")

      val c = new Completer {

        def complete(buffer: String, cursor: Int, candidates: util.List[CharSequence]): Int = {
          QueryTermParser.parseCommand(buffer) match {
            case Left(fpf) =>
              fpf.options.foreach {
                candidates.add(_)
              }
              if(buffer.length == 0){
                candidates.add("exit")
              }
              if (fpf.options.length > 0)
                cursor
              else
                -1
            case Right(b) =>
              cursor
          }
        }
      }
      reader.addCompleter(c)
      reader.setHistoryEnabled(true)
      var line: String = null
      val out = new PrintWriter(reader.getOutput)
      while ( {
        line = reader.readLine
        line
      } != null) {
        if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
          sys.exit()
        }
        if (color) {

          QueryTermParser.parseCommand(line) match {
            case Left(err) => out.println(s"Error parsing '$line'")
            case Right(cmd: Command) => cmd match {
              case PrintCommand =>
                s.render.foreach {
                  l => out.println(l)
                }
                out.flush()
              case SetCommand(id: (Int, Int), value: Either[String, Formula]) =>
                s.assign(id, value)
              case GetCommand(id: (Int, Int)) =>
                out.println(s.get(id))
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
    } finally {
      debug.close()
    }
  }
}