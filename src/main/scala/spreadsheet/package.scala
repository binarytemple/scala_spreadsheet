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
  type RCOff = (Int,Int)



}
