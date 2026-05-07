package org.example


import androidx.compose.runtime.*
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.max
import org.jetbrains.compose.web.attributes.min
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Composable
private fun GameControls(
    rowsInput: String,
    columnsInput: String,
    winLengthInput: String,
    errorMessage: String?,
    onRowsChange: (String) -> Unit,
    onColumnsChange: (String) -> Unit,
    onWinLengthChange: (String) -> Unit,
    onNewGame: () -> Unit,
    onResetGame: () -> Unit
){
    Div({ classes(AppStyles.controls)}) {
        Label {
            Text("Rows")
            Input(InputType.Number) {
                value(rowsInput)
                min("0")
                max("20")
                onInput { onRowsChange(it.value.toString()) }
            }
        }

        Label {
            Text("Columns")
            Input(InputType.Number) {
                value(columnsInput)
                min("0")
                max("20")
                onInput { onColumnsChange(it.value.toString()) }
            }
        }

        Label {
            Text("Win")
            Input(InputType.Number) {
                value(winLengthInput)
                min("0")
                max("20")
                onInput { onWinLengthChange(it.value.toString()) }
            }
        }

        Button(attrs = {
            onClick { onNewGame() }
        }){
            Text("New Game")
        }
        Button(attrs = {
            onClick {
                onResetGame()
            }
        }) {
            Text("Reset")
        }
    }

    if (errorMessage != null) {
        P({ classes(AppStyles.error) }) {
            Text(errorMessage)
        }
    }
}


@Composable
fun App() {
    Style(AppStyles)

    var game by remember { mutableStateOf(GameState()) }

    var rowsInput by remember { mutableStateOf(game.config.rows.toString()) }
    var columnsInput by remember { mutableStateOf(game.config.columns.toString()) }
    var winLengthInput by remember { mutableStateOf(game.config.winLength.toString()) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Div({ classes(AppStyles.page) }) {
        H1 {
            Text("Connect Four")
        }
        GameControls(rowsInput,columnsInput,winLengthInput,errorMessage, onRowsChange = { rowsInput = it },
            onColumnsChange = { columnsInput = it }, onWinLengthChange = { winLengthInput = it },
            onNewGame = {
                try {
                    val rows = rowsInput.toInt()
                    val columns = columnsInput.toInt()
                    val winLength = winLengthInput.toInt()
                    val config = GameConfig(rows, columns, winLength)

                    game = GameState(config)
                    errorMessage = null
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Invalid configuration"
                }
            },
            onResetGame = {
                game = GameState(config = game.config)
                errorMessage = null

            }
        )
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
    val boardMaxWidth = game.config.columns * 60
    val gapSize = when {
        game.config.columns >= 15 -> 3
        game.config.columns >= 10 -> 4
        else -> 6
    }
    Div({
        classes(AppStyles.board)
        style {
            property("grid-template-columns", "repeat(${game.config.columns}, 1fr)")
            property("width", "min(96vw, ${boardMaxWidth}px)")
            gap(gapSize.px)
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

    val controls by style {
        display(DisplayStyle.Flex)
        gap(12.px)
        flexWrap(FlexWrap.Wrap)
        justifyContent(JustifyContent.Center)
        alignItems(AlignItems.End)
        marginBottom(16.px)

        self + " label" style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            gap(4.px)
            textAlign("left")
            fontSize(14.px)
        }

        self + " input" style {
            width(80.px)
            padding(6.px)
            fontSize(16.px)
        }

        self + " button" style {
            padding(8.px, 14.px)
            fontSize(16.px)
            property("cursor", "pointer")
        }
    }

    val error by style {
        color(rgb(180, 40, 40))
        fontWeight("600")
    }
}