package spatutorial.shared

import java.time.LocalDateTime

import boopickle.Default._

sealed trait InvoicePriority

case object InvoiceLow extends InvoicePriority

case object InvoiceNormal extends InvoicePriority

case object InvoiceHigh extends InvoicePriority

case class InvoiceItem(id: String, timeStamp: Int, content: String, priority: InvoicePriority, completed: Boolean)


case class InvoiceItemNew(number: InvoiceNumber,
                          ref: ReferenceNumber,
                          to: Customer = None,
                          date: LocalDateTime,
                          dueDate: LocalDateTime,
                          paid: Boolean = false,
                          due: Amount = BigDecimal(0),
                          status: Status,
                          sent: Boolean = false)

object InvoicePriority {
  implicit val invoicePriorityPickler: Pickler[InvoicePriority] = generatePickler[InvoicePriority]
}
