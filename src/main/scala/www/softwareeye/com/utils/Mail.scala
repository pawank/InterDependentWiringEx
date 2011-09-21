/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package www.softwareeye.com.utils
import net.liftweb.util.Mailer
import Mailer._ 
import scala.xml._
import javax.mail._
import javax.mail.internet._
import java.util.Properties._

class Mail {
  
  def sendEmail(from:String, to:String,sub:String,body:String) {
    sendMail(From(from),
         Subject(sub),
         To(to),
         PlainMailBodyType(body))
  }
  
  def sendXhtmlEmail(from:String, to:String,sub:String,body:NodeSeq) {
    sendMail(From(from),
         Subject(sub),
         To(to),
         XHTMLPlusImages(body))
  }

}
