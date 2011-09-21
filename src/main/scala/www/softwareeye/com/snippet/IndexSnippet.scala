package www.softwareeye.com.snippet
import net.liftweb.util._
import net.liftweb.common._
import www.softwareeye.com.lib._
import Helpers._
import net.liftweb.http.S._
import net.liftweb.http.SHtml._
import net.liftweb.util.Helpers._
import net.liftweb.http._
import net.liftweb.util._
import net.liftweb.http._
import net.liftweb.util._
import Helpers._
import scala.xml._
import www.softwareeye.com.model._
import net.liftweb.mapper._


class IndexSnippet extends DispatchSnippet with Logger {

	def dispatch: DispatchIt = {
		case "index" => index
	}
	
  def index(in: NodeSeq): NodeSeq = {
    if ( User.loggedIn_? )
    	S.redirectTo("/contact")
    else
    	S.redirectTo("/user_mgt/login")
  }	
}