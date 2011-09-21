/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package www.softwareeye.com {

  package model {
    import net.liftweb.mapper._
    import net.liftweb.util._
    import net.liftweb.common._
    

    class Login extends LongKeyedMapper[Login] {
      def getSingleton = Login

      def primaryKeyField = id

      object id extends MappedLongIndex(this)

      object name extends MappedString(this,100)
      object password extends MappedEmail(this,100)
      
      object message extends MappedString(this,2)
      
      object updatedBy extends MappedString(this,50) {
        override def defaultValue = "SYSTEM"
      }
      
      
      
    }
    
    object Login extends Login with LongKeyedMetaMapper[Login]                {
               override def dbTableName = "login" // define the DB table name
            }
  }
}
