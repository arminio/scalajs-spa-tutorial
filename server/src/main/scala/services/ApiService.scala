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
  var pageXXs = Seq(
    PageXXItem("41424344-4546-4748-494a-4b4c4d4e4f50", 0x61626364, "Wear shirt that says “Life”. Hand out lemons on street corner.", PageXXLow, completed = false),
    PageXXItem("2", 0x61626364, "Make vanilla pudding. Put in mayo jar. Eat in public.", PageXXNormal, completed = false),
    PageXXItem("3", 0x61626364, "Walk away slowly from an explosion without looking back.", PageXXHigh, completed = false),
    PageXXItem("4", 0x61626364, "Sneeze in front of the pope. Get blessed.", PageXXNormal, completed = true)
  )
  
  var services =   Seq(
    Service(id = Identifier("user1", "dev",  "SERVICE from server", "Suuid1"),
      serviceName = "service 1",
      provider = Provider("aws", "java10000"),
      `package` = "sort me out",
      functions = Seq(
        //        Function(Identifier("user1", "dev",  "FUNCTION", "Fuuid1"),"function 1", "handler 1", Nil)
      )
    )
    //    ,
    //    Service(id = Identifier("user1", "dev",  "SERVICE", "Suuid2"),
    //      serviceName = "service 2",
    //      provider = Provider("aws", "java8"),
    //      `package` = "target/scala-2.11/hello.jar",
    //      functions = Seq(
    //        Function(Identifier("user1", "dev",  "FUNCTION", "Fuuid0"),"armin function", "handler 1", Nil),
    //        Function(Identifier("user1", "dev",  "FUNCTION", "Fuuid2"),"aydin function 2", "handler 1", Nil),
    //        Function(Identifier("user1", "dev",  "FUNCTION", "Fuuid3"),"naz function 3", "handler 1", Nil),
    //        Function(Identifier("user1", "dev",  "FUNCTION", "Fuuid4"),"Lara function 4", "handler 1", Nil),
    //        Function(Identifier("user1", "dev",  "FUNCTION", "Fuuid5"),"Lara function 5", "handler 1", Nil)
    //      )
    //    )
  )



  override def welcomeMsg(name: String): String =
    s"Welcome to SPA, $name! Time is now ${new Date}"

  override def getAllTodos(): Seq[TodoItem] = {
    // provide some fake Todos
    Thread.sleep(300)
    //!@println(s"Sending ${todos.size} Todo items")
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
      //!@println(s"Todo item was updated: $item")
    } else {
      // add a new item
      val newItem = item.copy(id = UUID.randomUUID().toString)
      todos :+= newItem
      //!@println(s"Todo item was added: $newItem")
    }
    Thread.sleep(300)
    todos
  }


  // delete a Todo
  override def deleteTodo(itemId: String): Seq[TodoItem] = {
    //!@println(s"Deleting item with id = $itemId")
    Thread.sleep(300)
    todos = todos.filterNot(_.id == itemId)
    todos
  }
  
  override def getAllPageXXs(): Seq[PageXXItem] = {
    // provide some fake PageXXs
    Thread.sleep(300)
    //!@println(s"Sending ${pageXXs.size} PageXX items")
    pageXXs
  }

  // update a PageXX
  override def updatePageXX(item: PageXXItem): Seq[PageXXItem] = {
    // PageXX, update database etc :)
    if(pageXXs.exists(_.id == item.id)) {
      pageXXs = pageXXs.collect {
        case i if i.id == item.id => item
        case i => i
      }
      //!@println(s"PageXX item was updated: $item")
    } else {
      // add a new item
      val newItem = item.copy(id = UUID.randomUUID().toString)
      pageXXs :+= newItem
      //!@println(s"PageXX item was added: $newItem")
    }
    Thread.sleep(300)
    pageXXs
  }

  // delete a PageXX
  override def deletePageXX(itemId: String): Seq[PageXXItem] = {
    //!@println(s"Deleting item with id = $itemId")
    Thread.sleep(300)
    pageXXs = pageXXs.filterNot(_.id == itemId)
    pageXXs
  }

//  override def getPage(pageIdentifier: PageIdentifier): Page =
//    Page("My Chickens Page", Seq.empty[PageItem], Unit => //!@println("Saved"))
  override def saveService(service: Service): Seq[Service] = {

  if(services.exists(_.id == service.id)) {
    services = services.collect {
      case i if i.id == service.id => service
      case i => i
    }
    println(s"service item was updated: $service")
  } else {
    // add a new item
    val newItem = service
    services :+= newItem
    println(s"service was added: $newItem")
  }

  Thread.sleep(2000)

  services
}

  override def getAllServices(): Seq[Service] = {
    Thread.sleep(2000)

    services
  }
}
