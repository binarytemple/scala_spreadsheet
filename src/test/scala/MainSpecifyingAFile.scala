import java.io.File
import scala.io

object MainSpecifyingAFile {
  def main(args: Array[String]) {
    args.toList.lift(0) match {
      case Some(f) if new File(f).canRead =>
        println(io.Source.fromFile(new File(f), "utf-8").getLines().mkString("\n"))
      case Some(f) if !new File(f).canRead =>
        System.err.println(s"Unable to read file $f")
        System.exit(1)
      case None =>
        System.err.println(s"No file specified on command line")
        System.exit(1)
    }
  }
}