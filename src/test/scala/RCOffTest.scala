import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import spreadsheet._

class RCOffTest extends Specification with Mockito {
  "RCOff should" should {
    "correctly store applied values" in {
      RCOff(9,8).row must_== 9
      RCOff(9,8).col must_== 8
    }
    "accept values, converting them to numbers" in {
      RCOff("A1").row must_== 0
      RCOff("A1").col must_== 0
    }
    "accept values, converting them to numbers" in {
      RCOff("B3").row must_== 2
      RCOff("C3").row must_== 2
      RCOff("B3").col must_== 1
      RCOff("A4").row must_== 3
      RCOff("ZZ") must throwA[NumberFormatException]
    }
  }
}
