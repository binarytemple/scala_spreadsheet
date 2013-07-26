package object spreadsheet {

  val Cols = 8
  val ColLetters = Range(65, 65 + Cols).map(_.toChar)

  val I2c = 65 /* add this to an integer to get it's character representation*/
  /**
   * String representation of a cell location, i.e A1 or E6
   */
  type CellId = String

  /**
   * row/column offset.
   */
  case class RCOff(row: Int, col: Int) {
    override def toString: String = s"${(col + 65).toChar}${row + 1}"
  }

  object RCOff {
    def apply(rc: (Int, Int)) = {
      new RCOff(rc._1, rc._2)
    }

    def apply(cid: CellId): RCOff = {
      cid.toCharArray.toList match {
        case c :: r :: Nil =>
          val ret = RCOff( r.toString.toInt - 1,c.toUpper - 65)
          ret
        case other => throw new Exception(s"Couldn't get offset from $cid, it split to '$other'")
      }
    }
  }
}
