package services

import java.util.{UUID, Date}

import spatutorial.shared._

class ApiService extends Api {
  var todos = Seq(
    TodoItem("41424344-4546-4748-494a-4b4c4d4e4f50", 0x61626364, "Wear shirt that says “Life”. Hand out lemons on street corner.", TodoLow, completed = false),
    TodoItem("2", 0x61626364, "Make vanilla pudding. Put in mayo jar. Eat in public.", TodoNormal, completed = false),
    TodoItem("3", 0x61626364, "Walk away slowly from an explosion without looking back.", TodoHigh, completed = false),
    TodoItem("4", 0x61626364, "Sneeze in front of the pope. Get blessed.", TodoNormal, completed = true)
  )
  var invoices = Seq(
    InvoiceItem("41424344-4546-4748-494a-4b4c4d4e4f50", 0x61626364, "Wear shirt that says “Life”. Hand out lemons on street corner.", InvoiceLow, completed = false),
    InvoiceItem("2", 0x61626364, "Make vanilla pudding. Put in mayo jar. Eat in public.", InvoiceNormal, completed = false),
    InvoiceItem("3", 0x61626364, "Walk away slowly from an explosion without looking back.", InvoiceHigh, completed = false),
    InvoiceItem("4", 0x61626364, "Sneeze in front of the pope. Get blessed.", InvoiceNormal, completed = true)
  )

  override def welcomeMsg(name: String): String =
    s"Welcome to SPA, $name! Time is now ${new Date}"

  override def getAllTodos(): Seq[TodoItem] = {
    // provide some fake Todos
    Thread.sleep(300)
    println(s"Sending ${todos.size} Todo items")
    todos
  }

  // update a Todo
  override def updateTodo(item: TodoItem): Seq[TodoItem] = {
    // TODO, update database etc :)
    if(todos.exists(_.id == item.id)) {
      todos = todos.collect {
        case i if i.id == item.id => item
        case i => i
      }
      println(s"Todo item was updated: $item")
    } else {
      // add a new item
      val newItem = item.copy(id = UUID.randomUUID().toString)
      todos :+= newItem
      println(s"Todo item was added: $newItem")
    }
    Thread.sleep(300)
    todos
  }

  // delete a Todo
  override def deleteTodo(itemId: String): Seq[TodoItem] = {
    println(s"Deleting item with id = $itemId")
    Thread.sleep(300)
    todos = todos.filterNot(_.id == itemId)
    todos
  }
  
  override def getAllInvoices(): Seq[InvoiceItem] = {
    // provide some fake Invoices
    Thread.sleep(300)
    println(s"Sending ${invoices.size} Invoice items")
    invoices
  }

  // update a Invoice
  override def updateInvoice(item: InvoiceItem): Seq[InvoiceItem] = {
    // INVOICE, update database etc :)
    if(invoices.exists(_.id == item.id)) {
      invoices = invoices.collect {
        case i if i.id == item.id => item
        case i => i
      }
      println(s"Invoice item was updated: $item")
    } else {
      // add a new item
      val newItem = item.copy(id = UUID.randomUUID().toString)
      invoices :+= newItem
      println(s"Invoice item was added: $newItem")
    }
    Thread.sleep(300)
    invoices
  }

  // delete a Invoice
  override def deleteInvoice(itemId: String): Seq[InvoiceItem] = {
    println(s"Deleting item with id = $itemId")
    Thread.sleep(300)
    invoices = invoices.filterNot(_.id == itemId)
    invoices
  }
  
  
}
