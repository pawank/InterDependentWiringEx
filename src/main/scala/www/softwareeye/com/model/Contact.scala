/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package www.softwareeye.com {

  package model {
  import java.util.Date;
      import java.text.DateFormat;
          import java.text.SimpleDateFormat;
    import net.liftweb.mapper._
    import net.liftweb.util._
    import net.liftweb.common._
    

    class Contact extends LongKeyedMapper[Contact] {
      def getSingleton = Contact


      def primaryKeyField = id

      object id extends MappedLongIndex(this)

      object name extends MappedString(this,50)
      object email extends MappedEmail(this,50)
      object title extends MappedEmail(this,140)
      object phone extends MappedString(this,15)
      object business extends MappedString(this,50)
      object message extends MappedString(this,400)
      object description extends MappedString(this,500)
      object updatedBy extends MappedString(this,50) {
        override def defaultValue = "SYSTEM"
      }
      object is_active extends MappedBoolean(this) {
        override def defaultValue = false
      }
      object createdon extends MappedDateTime(this) {
        override def defaultValue = new Date()
      }
      object updatedon extends MappedDateTime(this)

      def getCurrentDateTime() = {
        val df:DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val dt:Date = new Date()
        df.format(dt)
      }
    }
    
    object Contact extends Contact with LongKeyedMetaMapper[Contact]       {
               override def dbTableName = "contact" // define the DB table name
    }
  }
}
