import java.util
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class SpreadsheetCompleterTest extends Specification with Mockito {

  import scala.collection.JavaConversions._
  trait CompleterScope extends Scope {



    val sc = new SpreadsheetCompleter with NoopLogger
    def newlist = new util.ArrayList[CharSequence]()
  }

  "The spreadsheet completer" should {

    "give correct values for 'EX'" in new CompleterScope {
      val cps = newlist
      sc.complete("EX",2,cps)
      cps.toList must containAllOf(List("IT"))
      success
    }

    "give correct values for 'EXIT'" in new CompleterScope {
      val cps = newlist
      sc.complete("EXIT",4,cps)
      cps.toList.length must_== 0
      success
    }
  }
}