package org.example


import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
@Composable
fun App() {
    Style(AppStyles)
    var game by remember { mutableStateOf(GameState()) }
    Div({ classes(AppStyles.page) }) {
        H1 {
            Text("Connect Four")
        }
        P({ classes(AppStyles.status) }) {
            Text(statusText(game))
        }

        Board(game = game,
        onColumnClick = { column ->
            game = game.dropPiece(column)
        })
    }
}

private fun statusText(game: GameState): String =
    when (game.gameStatus) {
        GameStatus.InProgress -> "Current player: ${game.activePlayer}"
        GameStatus.RedWin -> "Red wins!"
        GameStatus.YellowWin -> "Yellow wins!"
        GameStatus.Draw -> "Draw!"
    }
@Composable
private fun Board(
    game: GameState,
    onColumnClick: (Int) -> Unit
) {
    Div({
        classes(AppStyles.board)
        style {
            property("grid-template-columns", "repeat(${game.config.columns}, 1fr)")
        }
    }) {
        game.board.forEach { row ->
            row.forEachIndexed { columnIndex, cell ->
                Div({
                    classes(AppStyles.slot)
                    onClick { if (game.gameStatus == GameStatus.InProgress) onColumnClick(columnIndex) }
                }) {
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

        property("cursor", "pointer")
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

    val status by style {
        fontSize(20.px)
        fontWeight("600")
        marginBottom(16.px)
    }


}