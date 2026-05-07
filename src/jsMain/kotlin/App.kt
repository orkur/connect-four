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
    onResetGame: () -> Unit,
    onClearSavedGame: () -> Unit,
){
    Div({ classes(AppStyles.controls)}) {
        Label {
            Text("Rows")
            Input(InputType.Number) {
                value(rowsInput)
                min("1")
                max("20")
                onInput { onRowsChange(it.value.toString()) }
            }
        }

        Label {
            Text("Columns")
            Input(InputType.Number) {
                value(columnsInput)
                min("1")
                max("20")
                onInput { onColumnsChange(it.value.toString()) }
            }
        }

        Label {
            Text("Win")
            Input(InputType.Number) {
                value(winLengthInput)
                min("1")
                max("10")
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
        Button(attrs = {
            onClick {
                onClearSavedGame()
            }
        }) {
            Text("Clear saved game")
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

    var game by remember { mutableStateOf(loadGameState() ?: GameState()) }

    var rowsInput by remember { mutableStateOf(game.config.rows.toString()) }
    var columnsInput by remember { mutableStateOf(game.config.columns.toString()) }
    var winLengthInput by remember { mutableStateOf(game.config.winLength.toString()) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Div({ classes(AppStyles.page) }) {
        H1 ({ classes(AppStyles.title) }) {
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
                    saveGameState(game)
                    errorMessage = null
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Invalid configuration"
                }
            },
            onResetGame = {
                game = GameState(config = game.config)
                saveGameState(game)
                errorMessage = null

            },
            onClearSavedGame = {
                clearSavedGameState()

                game = GameState()

                rowsInput = game.config.rows.toString()
                columnsInput = game.config.columns.toString()
                winLengthInput = game.config.winLength.toString()

                errorMessage = null
            }
        )
        P({ classes(AppStyles.status) }) {
            Text(statusText(game))
        }

        Board(game = game,
        onColumnClick = { column ->
            game = game.dropPiece(column)
            saveGameState(game)
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
    val boardMaxWidth = game.config.columns * 72
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
        }
    }) {
        Div({
            classes(AppStyles.pieceLayer)
            style {
                property("grid-template-columns", "repeat(${game.config.columns}, 1fr)")
                gap(gapSize.px)
            }
        }) {
            game.board.forEachIndexed { rowIndex, row ->
                row.forEachIndexed { columnIndex, cell ->
                    val pos = Position(rowIndex, columnIndex)
                    val isLastMove = game.lastMove == pos
                    val isWinningPiece = game.winResult != null && pos in game.winResult

                    Div({ classes(AppStyles.pieceCell) }) {
                        if (cell != null) {
                            Div({ classes(
                                    when (cell) {
                                        Player.Red -> if(isWinningPiece) AppStyles.redWinningPiece else AppStyles.redPiece
                                        Player.Yellow -> if(isWinningPiece) AppStyles.yellowWinningPiece else AppStyles.yellowPiece
                                    }
                                )
                                if (isLastMove) {
                                    val fallDistance = -(rowIndex + 1) * 115
                                    style {
                                        property("--fall-distance", "${fallDistance}%")
                                        property("animation", "fall-from-top 300ms ease-in")
                                    }
                                }
                            })
                        }
                    }
                }
            }
        }
        Div({
            classes(AppStyles.boardLayer)
            style {
                property("grid-template-columns", "repeat(${game.config.columns}, 1fr)")
                gap(gapSize.px)
            }
        }) {
            game.board.forEach { row ->
                row.forEachIndexed { columnIndex, _ ->
                    Div({
                        classes(AppStyles.slot)

                        onClick {
                            if (game.gameStatus == GameStatus.InProgress) {
                                onColumnClick(columnIndex)
                            }
                        }
                    })
                }
            }
        }
    }
}

object AppStyles : StyleSheet() {
    val page by style {
        minHeight(100.vh)
        boxSizing("border-box")
        fontFamily("system-ui", "Arial", "sans-serif")
        textAlign("center")
        backgroundColor(rgb(245, 247, 250))
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Column)
        justifyContent(JustifyContent.Center)
        alignItems(AlignItems.Center)
    }

    val board by style {
        property("position", "relative")
        property("margin", "0 auto")
    }

    val pieceLayer by style {
        display(DisplayStyle.Grid)

        property("position", "absolute")
        property("inset", "0")
        property("z-index", "1")
        property("pointer-events", "none")

        padding(8.px)
        boxSizing("border-box")
    }

    val boardLayer by style {
        display(DisplayStyle.Grid)

        property("position", "relative")
        property("z-index", "2")

        padding(8.px)
        borderRadius(16.px)
        boxSizing("border-box")
    }

    val pieceCell by style {
        property("aspect-ratio", "1 / 1")

        display(DisplayStyle.Flex)
        alignItems(AlignItems.Center)
        justifyContent(JustifyContent.Center)
    }

    val slot by style {
        property("aspect-ratio", "1 / 1")
        borderRadius(50.percent)

        property(
            "background",
            "radial-gradient(circle, transparent 50%, rgb(30, 140, 255) 51%)"
        )

        property("cursor", "pointer")
    }

    val redPiece by style {
        width(85.percent)
        height(85.percent)
        borderRadius(50.percent)
        backgroundColor(Color.red)
        property("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.25)")
    }

    val yellowPiece by style {
        width(85.percent)
        height(85.percent)
        borderRadius(50.percent)
        backgroundColor(Color.yellow)
        property("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.25)")
    }

    val redWinningPiece by style {
        width(82.percent)
        height(82.percent)
        borderRadius(50.percent)
        backgroundColor(Color.darkred)
        property("box-shadow", "0 0 12px rgba(255, 80, 80, 0.9)")
    }

    val yellowWinningPiece by style {
        width(82.percent)
        height(82.percent)
        borderRadius(50.percent)
        backgroundColor(Color.greenyellow)
        property("box-shadow", "0 0 12px rgba(255, 220, 60, 0.9)")
    }

    val title by style {
        fontSize(36.px)
        fontWeight("800")
        marginBottom(20.px)
    }

    val status by style {
        fontSize(22.px)
        fontWeight("700")
        marginBottom(18.px)
    }

    val controls by style {
        display(DisplayStyle.Flex)
        gap(14.px)
        flexWrap(FlexWrap.Wrap)
        justifyContent(JustifyContent.Center)
        alignItems(AlignItems.End)
        marginBottom(18.px)

        self + " label" style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            gap(6.px)
            textAlign("left")
            fontSize(16.px)
            fontWeight("600")
        }

        self + " input" style {
            width(92.px)
            padding(8.px)
            fontSize(18.px)
        }

        self + " button" style {
            padding(16.px, 12.px)
            fontSize(18.px)
            fontWeight("600")
            property("cursor", "pointer")
        }
    }

    val error by style {
        color(rgb(180, 40, 40))
        fontWeight("600")
    }
}