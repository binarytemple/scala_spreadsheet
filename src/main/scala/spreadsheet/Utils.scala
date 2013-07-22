package spreadsheet

object Utils {

  /**
   * Example behavior
   * stripOverlapFromToken(woodo,dog) == g
   * stripOverlapFromToken(woodo,dog) == og
   * @param existing
   * @param token
   */
  def stripOverlapFromToken(existing: String, token: String) =
    if (existing == null)
      token
    else {
      val li = existing.lastIndexOf(token.head)
      if (li != -1) {
        val existingoverlap = existing.substring(li, existing.length)
        token.stripPrefix(existingoverlap)
      }
      else
        token
    }
}