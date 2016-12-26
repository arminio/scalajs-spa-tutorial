package spatutorial.shared

trait Api {
  // message of the day
  def welcomeMsg(name: String): String

  // get Todo items
  def getAllTodos(): Seq[TodoItem]

  // update a Todo
  def updateTodo(item: TodoItem): Seq[TodoItem]

  // delete a Todo
  def deleteTodo(itemId: String): Seq[TodoItem]

  // get Invoice items
  def getAllInvoices(): Seq[InvoiceItem]

  // update a Invoice
  def updateInvoice(item: InvoiceItem): Seq[InvoiceItem]

  // delete a Invoice
  def deleteInvoice(itemId: String): Seq[InvoiceItem]
  
}
