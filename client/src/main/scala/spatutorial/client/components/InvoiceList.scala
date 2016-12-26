package spatutorial.client.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import spatutorial.client.components.Bootstrap.{Button, CommonStyle}
import spatutorial.shared._

import scalacss.ScalaCssReact._

object PageXXList {
  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class PageXXListProps(
    items: Seq[PageXXItem],
    stateChange: PageXXItem => Callback,
    editItem: PageXXItem => Callback,
    deleteItem: PageXXItem => Callback
  )

  private val PageXXList = ReactComponentB[PageXXListProps]("PageXXList")
    .render_P(p => {
      val style = bss.listGroup
      def renderItem(item: PageXXItem) = {
        // convert priority into Bootstrap style
        val itemStyle = item.priority match {
          case PageXXLow => style.itemOpt(CommonStyle.info)
          case PageXXNormal => style.item
          case PageXXHigh => style.itemOpt(CommonStyle.danger)
        }
        <.li(itemStyle,
          <.input.checkbox(^.checked := item.completed, ^.onChange --> p.stateChange(item.copy(completed = !item.completed))),
          <.span(" "),
          if (item.completed) <.s(item.content) else <.span(item.content),
          Button(Button.Props(p.editItem(item), addStyles = Seq(bss.pullRight, bss.buttonXS)), "Edit"),
          Button(Button.Props(p.deleteItem(item), addStyles = Seq(bss.pullRight, bss.buttonXS)), "Delete")
        )
      }
      <.ul(style.listGroup)(p.items map renderItem)
    })
    .build

  def apply(items: Seq[PageXXItem], stateChange: PageXXItem => Callback, editItem: PageXXItem => Callback, deleteItem: PageXXItem => Callback) =
    PageXXList(PageXXListProps(items, stateChange, editItem, deleteItem))
}
