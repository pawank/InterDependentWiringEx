package bootstrap.liftweb
import net.liftweb.util.NamedPF
import net.liftweb.http.{LiftRules,NotFoundAsTemplate,ParsePath}

import net.liftweb.util._

import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.http.provider._
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._
import Helpers._
import net.liftweb.mapper._
import java.sql.{ Connection, DriverManager }
import www.softwareeye.com.model._
import javax.mail._
import javax.mail.internet._
import java.util.Properties._

 import net.liftweb.widgets.autocomplete.AutoComplete
import www.softwareeye.com.utils._
 
/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  object Log extends Logger

  Log.trace("Starting boot process for application")

  def boot {
    /*
    object DBVendor extends ConnectionManager {
      def newConnection(name: ConnectionIdentifier): Box[Connection] = {
        try {
          Class.forName("com.mysql.jdbc.Driver")
          val dm = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?user=root&password=test")
          Full(dm)
        } catch {
          case e: Exception => e.printStackTrace; Empty
        }
      }
      def releaseConnection(conn: Connection) { conn.close }
    }

    if (!DB.jndiJdbcConnAvailable_?) {

      DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)

    }
   

    Log.trace("Database connection has established.")
    */
    // where to search snippet
    LiftRules.addToPackages("www.softwareeye.com")
    /*
    Schemifier.schemify(true, Schemifier.infoF _, User)
    Schemifier.schemify(true, Schemifier.infoF _, Contact)
    Schemifier.schemify(true, Schemifier.infoF _, Login)
    Schemifier.schemify(true, Schemifier.infoF _, Register)
    */

    LiftRules.setSiteMap(SiteMap(MenuInfo.menu: _*))

    Log.trace("Schemas and menu are generated successfully")
    /*
     * Show the spinny image when an Ajax call starts
     */
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

      
    /*
     * Make the spinny image go away when it ends
     */
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    //S.addAround(DB.buildLoanWrapper)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))

    configMailer("smtp.gmail.com", "link2life.is.beautiful", "Guessme0987123", false)
    Log.trace("Email account used for gmail SMTP is link2life.is.beautiful")
 
     AutoComplete.init
    //AutoSuggest.init

     
     LiftRules.dispatch.append(NamedPF("API") { case Req("api" :: "product" :: id :: Nil, _, rt) 
    if rt == GetRequest || rt == PostRequest || rt == DeleteRequest 
=> 
() => Full(getProductInfo(id)) 
})  
}

  def getProductInfo(id:String):LiftResponse = {
    XmlResponse(<test>{id}</test>)
  }
  
  def configureLocalMail = {
  }

  def configMailer(host: String, user: String, password: String, localmail: Boolean) {
    if (!localmail) {
      // Enable TLS support
      System.setProperty("mail.smtp.starttls.enable", "true");
      // Set the host name
      System.setProperty("mail.smtp.host", host) // Enable authentication
      System.setProperty("mail.smtp.auth", "true") // Provide a means for authentication. Pass it a Can, which can either be Full or Empty
      Mailer.authenticator = Full(new Authenticator {
        override def getPasswordAuthentication = new PasswordAuthentication(user, password)
      })
    }

  }
}

object MenuInfo {
  import Loc._
  import net.liftweb.sitemap.**

  // Define a simple test clause that we can use for multiple menu items
  val IfLoggedIn = If(() => User.currentUser.isDefined, "You must be logged in")

  //NOTE: Use in production
   def menu = List(Menu("Home") / "index" >> LocGroup("public"), Menu("Invoicing") / "invoice" / "index" >> LocGroup("public")) :::
    User.menus
}
