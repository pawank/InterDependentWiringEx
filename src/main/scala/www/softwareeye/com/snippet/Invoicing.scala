package www.softwareeye.com.snippet

import net.liftweb._
import common._
import http._
import util._
import Helpers._
import js.JsCmds._
import js.jquery._
import scala.xml.{ NodeSeq, Text }
import net.liftweb.widgets.autocomplete.AutoComplete
import www.softwareeye.com.model._
import http._
import js._
import JsCmds._
import JE._
import net.liftweb.http.S._
import net.liftweb.http.SHtml._
import scala.xml._

import www.softwareeye.com.utils._

case class ProductItem(guid: String, code: ValueCell[String], name: String, brand: String, unit_price: Cell[Double], qty: Integer, price: Double)

class Invoicing {
  //NOTE:Change it later on with db call
  private def codesWithPrices = Map("P01" -> 10.0d, "P02" -> 20.5d, "C01" -> 30.d)

  private object Info {
    val invoices = ValueCell(List(newProductItem))
    val subtotal = invoices.lift(_.foldLeft(0d)(_ + _.price))
    //tax is always fixed
    val tax = Cell(10.0d)
    val total = subtotal.lift(tax) { _ + _ }
  }

  def calculateAmount(ll: List[ProductItem]): Double = {
    var v = 0.0d
    ll.foreach(a => {
      v = v + (a.qty * a.price)
    })
    v
  }

  def subtotal(in: NodeSeq) = WiringUI.asText(in, Info.subtotal)

  def tax(in: NodeSeq) = WiringUI.asText(in, Info.tax, JqWiringSupport.fade)

  def total(in: NodeSeq) = WiringUI.asText(in, Info.total, JqWiringSupport.fade)

  def showProductItems = "* *" #> (Info.invoices.get.flatMap(renderProductItem): NodeSeq)

  def addProductItem(ns: NodeSeq): NodeSeq = {
    val div = S.attr("div") openOr "where"
    SHtml.ajaxButton(ns, () => {
      val theProductItem = appendProductItem
      val guid = theProductItem.guid
      JqJsCmds.AppendHtml(div, renderProductItem(theProductItem))
    })
  }

  private def acCallback(a: String): Unit = {
    println("I am called with " + a)
  }

  private def acCallbackOnBlur(a: String): JsCmd = {
        println("I am called onblue with " + a)
        Noop
  }

  private def getUnitPrice(code: String): Double = {
    println("Find price for code:" + code)
    codesWithPrices.get(code) match {
      case Some(v) => v
      case _ => 0.0d
    }
  }
  
  private def renderProductItem(theProductItem: ProductItem): NodeSeq =
    {
	var v = ValueCell("")
      <div id={ theProductItem.guid }>
        {
          SHtml.ajaxText(theProductItem.code,
            s => {
              mutateProductItem(theProductItem.guid) {
                l =>
                  println("Entered value is:" + s + "\n\n")
                  v = ValueCell(s)
                  ProductItem(l.guid, v, l.name, l.brand, v.lift{getUnitPrice}, l.qty, l.price)
              }
              Noop
            })
        }
        {
          SHtml.ajaxText(theProductItem.name,
            s => {
              mutateProductItem(theProductItem.guid) {
                l =>
                  println("Entered value is:" + s + "\n\n")
                  ProductItem(l.guid, l.code, s, l.brand, l.unit_price, l.qty, l.price)
              }
              Noop
            })
        }
        {
          new AutoSuggest().renderWithBlur(theProductItem.name, (current, limit) => codesWithPrices.keys.toList.filter(_.toLowerCase.startsWith(current.toLowerCase)), acCallback(_), acCallbackOnBlur(_), Nil)
        }
        {
           SHtml.text(v.get,s=>{Noop})
          //SHtml.text(theProductItem.unit_price.toString(),s=>{Noop})
        }
        {
          SHtml.ajaxText(theProductItem.qty.toString,
            s => {
              var qty = s
              if (s.length() > 0)
                qty = "0"
              Helpers.asInt(qty).foreach {
                d =>
                  mutateProductItem(theProductItem.guid) {
                    l => ProductItem(l.guid, l.code, l.name, l.brand, l.unit_price, d, l.price)
                  }
              }
              Noop
            })
        }
        {
          SHtml.ajaxText(theProductItem.price.toString,
            s => {
              var price = s
              if (s.length() == 0)
                price = "0"
              Helpers.asDouble(price).foreach {
                d =>
                  mutateProductItem(theProductItem.guid) {
                    l => ProductItem(l.guid, l.code, l.name, l.brand, l.unit_price, l.qty, d)
                  }
              }
              Noop
            })
        }
      </div>
    }

  private def newProductItem = ProductItem(nextFuncName, ValueCell(""), "", "", Cell(0.0d), 0, 0.0)

  private def appendProductItem: ProductItem = {
    val ret = newProductItem
    Info.invoices.set(ret :: Info.invoices.get)
    ret
  }

  private def mutateProductItem(guid: String)(f: ProductItem => ProductItem) {
    val all = Info.invoices.get
    val head = all.filter(_.guid == guid).map(f)
    val rest = all.filter(_.guid != guid)
    Info.invoices.set(head ::: rest)
  }

}

