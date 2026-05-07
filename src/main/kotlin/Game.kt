package org.example

enum class Player {
    Red,
    Yellow;

    fun next(): Player = if (this == Red) Yellow else Red
}

data class GameConfig (
    val rows: Int = 6,
    val columns: Int = 7,
    val winLength: Int = 4
){
    init {
        require(rows > 0) { "Rows must be > 0" }
        require(columns > 0) { "Columns must be > 0" }
        require(winLength > 0) { "Win length must be > 0" }
        require(winLength <= maxOf(rows, columns)) { "Win length must be <= maxOf(rows, columns)" }
    }
}


data class GameState(
    val config: GameConfig = GameConfig(),
    val board: List<List<Player?>> = List(config.rows) { List(config.columns) { null } },
    val currentPlayer: Player = Player.Red
)

fun GameState.dropPiece(column: Int): GameState {
    if (column < 0 || column >= board.size) error("Column must be in [0, ${board.size-1}]")

    val rowNumber = (config.rows - 1 downTo 0).firstOrNull { row -> board[row][column] == null } ?: error("full row")

    val newBoard = board.mapIndexed { i, row ->
        if (i == rowNumber)
            row.mapIndexed { j, cell ->
                if (j == column) currentPlayer else cell
            }
        else
            row
    }
    //place for winning condition

     return copy(
         board = newBoard,
         currentPlayer = currentPlayer.next()
     )
}

