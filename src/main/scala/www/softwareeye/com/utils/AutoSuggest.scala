package www.softwareeye.com.utils

import _root_.scala.xml.{NodeSeq, Node, Elem, PCData, Text, Unparsed}
import _root_.net.liftweb.common._
import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.js._
import JsCmds._
import JE._
import S._
import SHtml._
import Helpers._

object AutoSuggest {

  def apply(start: String,
            options: (String, Int) => Seq[String],
            onSubmit: String => Unit,
            isSuggestMode: Boolean,
            attrs: (String, String)*) = new AutoSuggest().render(start, options, onSubmit, isSuggestMode, attrs:_*)

  def apply(start: String,
            options: (String, Int) => Seq[String],
            onSubmit: String => Unit,
            jsonOptions: List[(String,String)],
            isSuggestMode: Boolean,
            attrs: (String, String)*) = new AutoSuggest().render(start, options, onSubmit, jsonOptions , isSuggestMode, attrs:_*)

  //TODO Pimp with additional Param isSuggestMode
  //In which case is this Form of initialisation used ?
  //OK for Backwards-Comatibility
  //see http://groups.google.com/group/liftweb/browse_thread/thread/45a210efcdb4b0ab/df0f8788c4a47c56?lnk=gst&q=autocompleteObj#df0f8788c4a47c56
  def autocompleteObj[T](options: Seq[(T, String)],
                         default: Box[T],
                         onSubmit: T => Unit): Elem = new AutoSuggest().autocompleteObj("",options, default, onSubmit)

                           def autocompleteObj[T](guid:String,options: Seq[(T, String)],
                         default: Box[T],
                         onSubmit: T => Unit): Elem = new AutoSuggest().autocompleteObj(guid,options, default, onSubmit)

                         
  def autocompleteObj[T](options: Seq[(T, String)],
                          default: Box[T],
                          jsonOptions: List[(String,String)],
                          onSubmit: T => Unit): Elem = new AutoSuggest().autocompleteObj("",options, default, jsonOptions, onSubmit)


                          def autocompleteObj[T](guid:String,options: Seq[(T, String)],
                          default: Box[T],
                          jsonOptions: List[(String,String)],
                          onSubmit: T => Unit): Elem = new AutoSuggest().autocompleteObj(guid,options,default, jsonOptions, onSubmit)

                          
   var guid:String = Helpers.nextFuncName
  /**
   * register the resources with lift (typically in boot)
   */
  def init() {
    import net.liftweb.http.ResourceServer

    ResourceServer.allow({
        case "autosuggest" :: _ => true
     })
  }
  
  def getGUID = guid

}

class AutoSuggest {

  def isAutoSubmitEnable = false
  /**
   * Create an autocomplete form based on a sequence.
   */
  def autocompleteObj[T](guid:String,options: Seq[(T, String)],
                         default: Box[T],
                         onSubmit: T => Unit): Elem = {
     val jsonOptions :List[(String,String)] = List()
     autocompleteObj(guid,options, default, jsonOptions, onSubmit)
  }

  /**
   * Create an autocomplete form based on a sequence.
   */
   def autocompleteObj[T](guid:String,options: Seq[(T, String)],
                          default: Box[T],
                          jsonOptions: List[(String,String)],
                          onSubmit: T => Unit): Elem = {
    val (nonces, defaultNonce, secureOnSubmit) = secureOptions(options, default, onSubmit)
    val defaultString = default.flatMap(d => options.find(_._1 == d).map(_._2))

    autocomplete_*(guid,nonces, defaultString, defaultNonce, secureOnSubmit, jsonOptions)
  }
   
   
  private def autocomplete_*(guid:String,options: Seq[(String, String)], default: Box[String],
                     defaultNonce: Box[String], onSubmit: AFuncHolder, jsonOptions: List[(String,String)]): Elem = {
    //val id = Helpers.nextFuncName
    val id = {
      if (guid.equals(""))
        Helpers.nextFuncName
        else
          guid
    }
    
    fmapFunc(onSubmit){hidden =>

      val data = JsArray(options.map {
        case (nonce, name) => JsObj("name" -> name, "nonce" -> nonce)
      } :_*)

    /* merge the options that the user wants */
      val jqOptions =  ("minChars","0") ::
                       ("matchContains","true") ::
                       ("formatItem","function(row, i, max) { return row.name; }") ::
                       Nil ::: jsonOptions
	  val json = jqOptions.map(t => t._1 + ":" + t._2).mkString("{", ",", "}")
      val autocompleteOptions = JsRaw(json)

      val onLoad = JsRaw("""
      jQuery(document).ready(function(){
        var data = """+data.toJsCmd+""";
        jQuery("#"""+id+"""").autocomplete(data, """+autocompleteOptions.toJsCmd+""").result(function(event, dt, formatted) {
          jQuery("#"""+hidden+"""").val(formatted);
        });
      });""")

      <span>
        <head>
          <link rel="stylesheet" href={"/" + LiftRules.resourceServerPath +"/autocomplete/jquery.autocomplete.css"} type="text/css" />
          <script type="text/javascript" src={"/" + LiftRules.resourceServerPath +"/autocomplete/jquery.autocomplete.js"} />
          <script type="text/javascript">{Unparsed(onLoad.toJsCmd)}</script>
        </head>
        <input type="text" id={id} value={default.openOr("")} />
        <input type="hidden" name={hidden} id={hidden} value={defaultNonce.openOr("")} />
      </span>
     }
  }

  private def secureOptions[T](options: Seq[(T, String)], default: Box[T],
                                     onSubmit: T => Unit): (Seq[(String, String)], Box[String], AFuncHolder) = {
    val secure = options.map{case (obj, txt) => (obj, randomString(20), txt)}
    val defaultNonce = default.flatMap(d => secure.find(_._1 == d).map(_._2))
    val nonces = secure.map{case (obj, nonce, txt) => (nonce, txt)}
    def process(nonce: String): Unit = secure.find(_._2 == nonce).map(x => onSubmit(x._1))
    (nonces, defaultNonce, SFuncHolder(process))
  }


  /**
   * Render a text field with Ajax autocomplete support
   *
   * @param start - the initial input string
   * @param option - the function to be called when user is typing text. The text and th options limit is provided to this functions
   * @param isSuggestMode - if set to true: the user can enter a value, even if no value is choosen from the dropdown
   * @param attrs - the attributes that can be added to the input text field
   */
  def render(start: String,
             options: (String, Int) => Seq[String],
             onSubmit: String => Unit,
             isSuggestMode: Boolean,
             attrs: (String, String)*): Elem = {

    val jsonOptions :List[(String,String)] = List()
    render(start, options, onSubmit, jsonOptions, isSuggestMode, attrs:_*)

  }

  /**
   * Render a text field with Ajax autocomplete support
   *
   * @param start - the initial input string
   * @param option - the function to be called when user is typing text. The text and th options limit is provided to this functions
   * @param attrs - the attributes that can be added to the input text field
   * @param isSuggestMode - if set to true: the user can enter a value, even if no value is choosen from the dropdown
   * @param jsonOptions - a list of pairs that will be send along to the jQuery().AutoComplete call (for customization purposes)
   */
   def render(start: String,
              options: (String, Int) => Seq[String],
              onSubmit: String => Unit,
              jsonOptions: List[(String,String)],
              isSuggestMode: Boolean,
              attrs: (String, String)*): Elem = {

    val f = (ignore: String) => {
      val q = S.param("q").openOr("")
      val limit = S.param("limit").flatMap(asInt).openOr(10)
      PlainTextResponse(options(q, limit).map(s => s+"|"+s).mkString("\n"))
    }


    fmapFunc(SFuncHolder(f)){ func =>
      val what: String = encodeURL(S.contextPath + "/" + LiftRules.ajaxPath+"?"+func+"=foo")

      val id = Helpers.nextFuncName
      fmapFunc(SFuncHolder(onSubmit)){hidden =>

     /* merge the options that the user wants */
      val jqOptions =  ("minChars","0") ::
                       ("matchContains","true") ::
                       Nil ::: jsonOptions
      val json = jqOptions.map(t => t._1 + ":" + t._2).mkString("{", ",", "}")
      val autocompleteOptions = JsRaw(json)

      val onLoad = JsRaw("""
      jQuery(document).ready(function(){
        var data = """+what.encJs+""";
        jQuery("#"""+id+"""").autocomplete(data, """+autocompleteOptions.toJsCmd+""").result(function(event, dt, formatted) {
          jQuery("#"""+hidden+"""").val(formatted);
        });
      });
      """)

      //This bit of code is the reason for this component "AutoSuggest"
      //Make sure that the hidden field gets the entered value, when the user chooses no value from the dropdown
      var onLoad2: JsRaw  = JsRaw("")
      if (isSuggestMode) {
      onLoad2 = JsRaw("""
      jQuery(document).ready(function(){
        jQuery("#"""+id+"""").bind("blur",function(){
        jQuery("#"""+hidden+"""").val(jQuery("#"""+id+"""").val());
        });
      });
      """)
      }
      
      
      var submitAC: JsRaw  = JsRaw("")
      if (isAutoSubmitEnable) {
      submitAC = JsRaw("""
jQuery(document).ready(function() {
        jQuery("#""" + id + """").autocomplete({
            source: ['Google', 'Yahoo', 'StackOverflow'],
            select: function(event, ui) {
                jQuery(event.target).val(ui.item.value);
                jQuery('#""" + id + "form" + """').submit();
                return false;
            },
            minLength: 1
        });
});""")}
      
      

      <span>
        <head>
          <link rel="stylesheet" href={"/" + LiftRules.resourceServerPath +"/autocomplete/jquery.autocomplete.css"} type="text/css" />
          <script type="text/javascript" src={"/" + LiftRules.resourceServerPath +"/autocomplete/jquery.autocomplete.js"} />
          <script type="text/javascript">{Unparsed(onLoad.toJsCmd)}</script>

         <script type="text/javascript">{Unparsed(onLoad2.toJsCmd)}</script>

         <script type="text/javascript">{Unparsed(submitAC.toJsCmd)}</script>
        </head>
        {
          attrs.foldLeft(<input type="text" id={id} value={start} />)(_ % _)
        }
        <input type="hidden" name={hidden} id={hidden} value={start} />
      </span>
    }
   }
  }
     def renderWithBlur(start: String,
    options: (String, Int) => Seq[String],
    onSubmit: String => Unit,
    onBlur: String => JsCmd,
    jsonOptions: List[(String, String)],
    attrs: (String, String)*): Elem = {

    val f = (ignore: String) => {
      val q = S.param("q").openOr("")
      val limit = S.param("limit").flatMap(asInt).openOr(10)
      PlainTextResponse(options(q, limit).map(s => s + "|" + s).mkString("\n"))
    }

    fmapFunc(SFuncHolder(f)) { func =>
      val what: String = encodeURL(S.contextPath + "/" +
LiftRules.ajaxPath + "?" + func + "=foo")

      val id = Helpers.nextFuncName
      fmapFunc(SFuncHolder(onSubmit)) { hidden =>
        fmapFunc(SFuncHolder(onBlur)) { onblur =>
          /* merge the options that the user wants */
          val jqOptions = ("minChars", "0") ::
            ("matchContains", "true") ::
            Nil ::: jsonOptions
          val json = jqOptions.map(t => t._1 + ":" +
t._2).mkString("{", ",", "}")
          val autocompleteOptions = JsRaw(json)

          val onLoad = JsRaw("""
      jQuery(document).ready(function(){
        var data = """ + what.encJs + """;
        jQuery("#""" + id + """").autocomplete(data, """ +
autocompleteOptions.toJsCmd + """).result(function(event, dt,
formatted) {
          jQuery("#""" + hidden + """").val(formatted);
        });
      });""")

          val raw = (funcName: String, value: String) => JsRaw("'" +
funcName + "=' + encodeURIComponent(" + value + ".value)")
          val onBlur: (String, String) = ("onblur",
        		  makeAjaxCall(raw(onblur, "this")).toJsCmd)

          <span>
            <head>
              <link rel="stylesheet" href={ "/" +
LiftRules.resourceServerPath + "/autocomplete/jquery.autocomplete.css"
} type="text/css"></link>
              <script type="text/javascript" src={ "/" +
LiftRules.resourceServerPath + "/autocomplete/jquery.autocomplete.js"
}></script>
              { Script(onLoad) }
            </head>
            {
              attrs.foldLeft(<input type="text" id={ id } value={
start }/>)(_ % _) % onBlur
            }
            <input type="hidden" name={ hidden } id={ hidden } value={ start }></input>
          </span>
        }
      }
    }
  }
}
