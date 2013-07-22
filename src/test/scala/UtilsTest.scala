import spreadsheet.Utils
import org.specs2.mutable.Specification

class UtilsTest extends Specification {

  "Utils " should {
    "correctly strip string overlaps in the usual case" in {
      Utils.stripOverlapFromToken("woodo", "dog") must_== "g"
      Utils.stripOverlapFromToken("wood", "dog") must_== "og"
    }

    "correctly strip string overlaps where the existing string is empty" in {
      Utils.stripOverlapFromToken("", "dog") must_== "dog"

    }


  }
}