package spreadsheet

class Settings {

  /**
   * Cells will be truncated to this width
   */
  def CellWidth = 10

  /**
   * Format string used on cells, determines rendering
   */
  def FormatString: String = "%-10s"
}
