package spatutorial.client.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import spatutorial.client.components.Bootstrap.{Button, CommonStyle}
import spatutorial.shared._

import scalacss.ScalaCssReact._

object FunctionList {
  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class FunctionFunctionProps(
                                    items: Seq[FunctionParameters],
                                    stateChange: FunctionParameters => Callback,
                                    editItem: FunctionParameters => Callback,
                                    deleteItem: FunctionParameters => Callback
  )

  private val FunctionFunction = ReactComponentB[FunctionFunctionProps]("FunctionFunction")
    .render_P(p => {
      val style = bss.listGroup
      def renderItem(item: FunctionParameters) = {
        // convert priority into Bootstrap style
        val itemStyle = item.priority match {
          case FunctionLow => style.itemOpt(CommonStyle.info)
          case FunctionNormal => style.item
          case FunctionHigh => style.itemOpt(CommonStyle.danger)
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

  def apply(items: Seq[FunctionParameters], stateChange: FunctionParameters => Callback, editItem: FunctionParameters => Callback, deleteItem: FunctionParameters => Callback) =
    FunctionFunction(FunctionFunctionProps(items, stateChange, editItem, deleteItem))
}
