package www.softwareeye.com {
  package model {

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

    /**
     * The singleton that has methods for accessing the database
     */
    object User extends User with MetaMegaProtoUser[User] {
      override def dbTableName = "user" // define the DB table name
      override def screenWrap = Full(<lift:surround with="default" at="content">
                                       <lift:bind></lift:bind>
                                     </lift:surround>)
      // define the order fields will appear in forms and output
      override def fieldOrder = List(id, firstName, lastName, email, password, textArea)

      // comment this line out to require email validations
      override def skipEmailValidation = true
      /**
       * The menu item for creating the user/sign up (make this "Empty" to disable)
       */
      override def createUserMenuLoc: Box[Menu] =
        Full(Menu(Loc("CreateUser", signUpPath, S ? "Register", createUserMenuLocParams)))

      /**
       * The menu item for lost password (make this "Empty" to disable)
       */
      override def lostPasswordMenuLoc: Box[Menu] =
        Full(Menu(Loc("LostPassword", lostPasswordPath, S ? "Forgot Password", lostPasswordMenuLocParams)))

      override def loginXhtml = {
        (<section id="last_posts">
           <header class="section_tit">
             <h3>Welcome to RETAIL SHOP</h3>
           </header>
           <div id="center_section">
             <div class="full_row">
               <article>
                 <div class="full_row_section">
                   <lift:embed what="LoginFormTemplate"></lift:embed>
                 </div>
               </article>
             </div>
           </div>
         </section>)
      }

    }

    /**
     * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
     */
    class User extends MegaProtoUser[User] {
      def getSingleton = User // what's the "meta" server

      // define an additional field for a personal essay
      object textArea extends MappedTextarea(this, 2048) {
        override def textareaRows = 10
        override def textareaCols = 50
        override def displayName = "Personal Essay"
      }
    }

  }
}
