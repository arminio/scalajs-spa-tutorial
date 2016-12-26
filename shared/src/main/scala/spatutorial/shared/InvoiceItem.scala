package spatutorial.shared

import java.time.LocalDateTime

import boopickle.Default._

sealed trait PageXXPriority

case object PageXXLow extends PageXXPriority

case object PageXXNormal extends PageXXPriority

case object PageXXHigh extends PageXXPriority

case class PageXXItem(id: String, timeStamp: Int, content: String, priority: PageXXPriority, completed: Boolean)


case class PageXXItemNew(number: PageXXNumber,
                          ref: ReferenceNumber,
                          to: Customer = None,
                          date: LocalDateTime,
                          dueDate: LocalDateTime,
                          paid: Boolean = false,
                          due: Amount = BigDecimal(0),
                          status: Status,
                          sent: Boolean = false)

object PageXXPriority {
  implicit val pageXXPriorityPickler: Pickler[PageXXPriority] = generatePickler[PageXXPriority]
}
