package spatutorial.shared

import java.io.File
import java.util.UUID


case class Service(id: String, artifactPath: String, functions: Seq[Function]) {
//  def apply(artifactPath: String, fs: Seq[Function]): Service = Service(UUID.randomUUID().toString, artifactPath, fs)
}

case class Function(id:String, name: String, handler: String, events: Seq[Event])

trait Event
case class HttpEvent(path: String,
                     method: String, //!@ Enum this
                     cors: Boolean) extends Event

//!@ Enum this
trait PageItemType

trait PageItemTypeAndData {
  def getType: PageItemType

  def getData(): (PageIdentifier) => Seq[String]

}

case class PageItem(name: String,
                    label: String,
                    typeAndData: PageItemTypeAndData) {


  //!@

}

class Page(val title: String, val items: Seq[PageItem], val saveFunction: SaveFunction)

