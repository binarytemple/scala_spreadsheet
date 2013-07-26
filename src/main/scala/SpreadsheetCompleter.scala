import java.nio.charset.Charset
import java.nio.file.{StandardOpenOption, Files, FileSystems}
import java.util
import jline.console.completer.Completer
import spreadsheet.QueryTermParser


trait HasOutputter {
  def logIt(buffer:String,cursor:Int,candidates:util.List[CharSequence],ret:Int):Unit
}


trait NoopLogger extends HasOutputter {
  def logIt(buffer: String, cursor: Int, candidates: util.List[CharSequence], ret: Int) = ()
}

trait TmpLogger extends HasOutputter {
  lazy val path = FileSystems.getDefault().getPath("/tmp/access.log")
  lazy val debug = Files.newBufferedWriter(path, Charset.defaultCharset(), StandardOpenOption.SYNC, StandardOpenOption.APPEND)
  import scala.collection.JavaConversions._
  def logIt(buffer:String,cursor:Int,candidates:util.List[CharSequence],ret:Int) = {
    debug.write(s"buffer='$buffer', cursor=$cursor, ret=$ret, candidates=${candidates.toList}")
    debug.newLine()
    debug.flush()
    ret
  }

}


class SpreadsheetCompleter extends Completer {
  self:HasOutputter=>

   def complete(buffer: String, cursor: Int, candidates: util.List[CharSequence]): Int = synchronized{
    val ret: Int = QueryTermParser.parseCommand(buffer) match {
      case Left(fpf) =>
        fpf.options.sortBy(_.length) foreach {
          candidates.add(_)
        }
        if (fpf.options.length > 0)
          cursor
        else
          -1
      case Right(b) =>
        cursor
    }
     logIt(buffer,cursor,candidates,ret)
     ret

  }

}
