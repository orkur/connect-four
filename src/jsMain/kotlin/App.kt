package org.example


import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
@Composable
fun App() {
    Style(AppStyles)

    Div({ classes(AppStyles.page) }) {
        H1 {
            Text("Connect Four")
        }

        Board(GameState())
    }
}

@Composable
private fun Board(
    game: GameState
) {
    Div({
        classes(AppStyles.board)
        style {
            property("grid-template-columns", "repeat(${game.config.columns}, 1fr)")
        }
    }) {
        game.board.forEach { row ->
            row.forEach { cell ->
                Div({ classes(AppStyles.slot) }) {
                    Div({ classes(
                        when (cell) {
                            Player.Red -> AppStyles.redPiece
                            Player.Yellow -> AppStyles.yellowPiece
                            null -> AppStyles.emptyPiece
                        }
                    ) })
                }
            }
        }
    }
}

object AppStyles : StyleSheet() {
    val page by style {
        minHeight(100.vh)
        padding(24.px)
        boxSizing("border-box")
        fontFamily("system-ui", "Arial", "sans-serif")
        textAlign("center")
        backgroundColor(rgb(245, 247, 250))
    }

    val board by style {
        display(DisplayStyle.Grid)
        gap(6.px)

        width(92.vmin)
        maxWidth(720.px)

        property("margin", "0 auto")

        padding(8.px)
        borderRadius(16.px)
        backgroundColor(rgb(66, 135, 245))
        boxSizing("border-box")
    }

    val slot by style {
        property("aspect-ratio", "1 / 1")
        borderRadius(50.percent)
        backgroundColor(rgb(7, 16, 31))

        display(DisplayStyle.Flex)
        alignItems(AlignItems.Center)
        justifyContent(JustifyContent.Center)
    }

    val emptyPiece by style {
        width(85.percent)
        height(85.percent)
        borderRadius(50.percent)
        backgroundColor(Color.white)
    }

    val redPiece by style {
        width(85.percent)
        height(85.percent)
        borderRadius(50.percent)
        backgroundColor(Color.red)
    }

    val yellowPiece by style {
        width(85.percent)
        height(85.percent)
        borderRadius(50.percent)
        backgroundColor(Color.yellow)
    }


}