package spreadsheet



object Viewer {


  def render(m: Model): String = {

    val headers = m.data.zip(Range(0, m.data.length)).map(
      ci => (ci._2 + I2c).toChar.formatted("%-10s")
    ).mkString("|", "|", "|\n")

    val rows = m.data

    headers // + rows.mkString("|","|","|")
  }


}
