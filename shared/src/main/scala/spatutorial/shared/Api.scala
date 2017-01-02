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

  // get PageXX items
  def getAllPageXXs(): Seq[PageXXItem]

  // update a PageXX
  def updatePageXX(item: PageXXItem): Seq[PageXXItem]

  // delete a PageXX
  def deletePageXX(itemId: String): Seq[PageXXItem]

  def getAllServices(): Seq[Service]
  def saveService(service:Service) : Seq[Service]

//!@  def getPage(pageIdentifier: PageIdentifier): Page
}
