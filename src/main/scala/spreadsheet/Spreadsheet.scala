package spreadsheet


import scala.collection.immutable.IndexedSeq

import spreadsheet.Model.Cell
import spreadsheet.QueryTermParser.Formula

object Spreadsheet {

  /**
   * Convert a CellId to an offset pair. The offsets are zero based.
   */
  def c2t(id: CellId): RCOff = {
    id.toCharArray.toList match {
      case c :: r :: Nil => (c.toUpper - 65, r.toString.toInt - 1)
      case other => throw new Exception(s"Couldn't get offset from $id, it split to '$other'")
    }
  }

  /**
   * Convert a String to a CellRange
   * @param s
   * @return
   */
  def s2cr(s: String): CellRange = {
    s.split(':').toList match {
      case start :: end :: Nil => CellRange(c2t(start), c2t(end))
      case other => throw new Exception(s"Couldn't extract range from $s, it split to '$other'")
    }
  }

  /**
   * Convert an Offset pair to a CellId
   * @param o
   * @return
   */
  def o2s(o:RCOff):CellId = {
    s"${65 + o._1}${o._2 + 1}} "
  }

  case class CellRange(start: RCOff, end: RCOff)  {

    override def toString = {
      s"${o2s(start)}:${o2s(end)}"
    }
  }

  def extractRange(s: String)(implicit m: Model): List[Cell] = {
    extractRange(s2cr(s))
  }

  def extractRange(c: CellRange)(implicit m: Model): List[Cell] = {
    def colfilter(x: (Array[Model.Cell], Int)): Boolean = {
      x._2 >= c.start._1 && x._2 <= c.end._1
    }
    def rowfilter(x: (Model.Cell, Int)): Boolean = {
      x._2 >= c.start._2 && x._2 <= c.end._2
    }
    m.data.toList.zipWithIndex.filter {
      colfilter
    }.map(_._1).map(_.toList.zipWithIndex.filter(rowfilter).map(_._1)).flatten
  }

}

/**
 * Write a class to represent the spreadsheet, using the class you have written that represents cells.

 * The spreadsheet class will also need a method that allows it to be printed out in a format similar to the example shown above.
 * Write test code that tests the behaviour of the spreadsheet class.
 * @param m The Spreadsheet data model,
 */
class Spreadsheet(implicit var m: Model = new Model, settings:Settings = new Settings) {

  import Spreadsheet._

//  def assign(id: CellId, value: Any): Unit = {
//    assign(id, value.toString)
//  }

  def get(id:RCOff):String = {
    this.m.data(id._1)(id._2).displayable()
  }

  def get(id:CellId):String = {
    val offset = c2t(id)
    this.m.data(offset._1)(offset._2).displayable()
  }

  def assign(id: RCOff, value: Number): Unit = {
    assign(id,Left(value.toString))
  }

  def assign(id: RCOff, value: String): Unit = {
    assign(id,Left(value))
  }

  def assign(id: CellId, value: Number): Unit = {
    assign(id,Left(value.toString))
  }

  def assign(id: CellId, value: String): Unit = {
    assign(id,Left(value))
  }

  def assign(id: RCOff, value: Either[String, Formula]): Unit = {
    try {
      this.m.data(id._1).update(id._2, Cell(value))
    }
    catch {
      case t: Throwable => System.err.println(t)
    }
  }

  /*The spreadsheet class will need methods that allow cells identified by their names (e.g. A1 or D4) to be assigned numerical values or emptied,
   as well as queried for the values they contain.*/
  def assign(id: CellId, value: Either[String, Formula]): Unit = {
    val offset = c2t(id)
    try {
      this.m.data(offset._1).update(offset._2, Cell(value))
    }
    catch {
      case t: Throwable => System.err.println(t)
    }
  }

  def extractRange(s: String): List[Cell] = {
    Spreadsheet.extractRange(s)
  }

  def extractRange(c: CellRange): List[Cell] = {
    Spreadsheet.extractRange(c)
  }

  def render  = {
    def longest(ll: List[List[_ <: Any]]) = {
      ll.map(_.length).max
    }
    val d = m.data.toList.map(_.toList.toArray.toList).toArray.toList

    val headers = d.zip(Range(0, longest(d))).map(
      ci => (ci._2 + I2c).toChar.formatted(settings.FormatString)
    ).mkString("     |", "|", "|")

    val rows: IndexedSeq[IndexedSeq[Cell]] = for {
      i <- Range(0, d(0).length)
    } yield
      for {
        j <- Range(0, d(0).length)
      } yield d(j)(i)

    List[String](headers) ::: rows.zipWithIndex.map{x => x._1.map(
      v => settings.FormatString.format(v.printable())).mkString("%-5s|".format(x._2 + 1), "|", "|")
  }.toList
  }
}