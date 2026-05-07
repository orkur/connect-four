package org.example

import kotlinx.serialization.Serializable

@Serializable
enum class Player {
    Red,
    Yellow;

    fun next(): Player = if (this == Red) Yellow else Red
}

@Serializable
data class GameConfig (
    val rows: Int = 6,
    val columns: Int = 7,
    val winLength: Int = 4
){
    init {
        require(rows in 1..20) { "Rows must be between 1 and 20" }
        require(columns in 1..20) { "Columns must be between 1 and 20" }
        require(winLength in 4..10) { "Win length must be between 4 and 10" }
        require(winLength <= maxOf(rows, columns)) { "Win length must be <= maxOf(rows, columns)" }
        require(winLength <= 10) { "Win length must be <= maxOf(rows, columns)" }
    }
}

@Serializable
enum class GameStatus {
    InProgress,
    YellowWin,
    RedWin,
    Draw;

    companion object {
        fun setWinner(player: Player): GameStatus =
            if (player == Player.Red) RedWin else YellowWin
    }
}

@Serializable
data class Position(val row: Int, val column: Int)


@Serializable
data class GameState(
    val config: GameConfig = GameConfig(),
    val board: List<List<Player?>> = List(config.rows) { List(config.columns) { null } },
    val activePlayer: Player = Player.Red,
    val gameStatus: GameStatus = GameStatus.InProgress,
    val freeSpaces: Int = config.rows * config.columns,
    val lastMove: Position? = null,
    val winResult: Set<Position>? = null
)

fun GameState.dropPiece(column: Int): GameState {
    if (column < 0 || column >= config.columns) error("Column must be in [0, ${board.size-1}]")

    if (gameStatus != GameStatus.InProgress) return this

    val rowNumber = (config.rows - 1 downTo 0).firstOrNull { row -> board[row][column] == null } ?: return this

    val newBoard = board.mapIndexed { i, row ->
        if (i == rowNumber)
            row.mapIndexed { j, cell ->
                if (j == column) activePlayer else cell
            }
        else
            row
    }
    val newFreeSpaces = freeSpaces - 1
    var newGameStatus : GameStatus = GameStatus.InProgress
    val set = winSet(newBoard, column, rowNumber, activePlayer, config.winLength)
    if (set != null) {
        newGameStatus = GameStatus.setWinner(activePlayer)
    } else if (newFreeSpaces == 0){
        newGameStatus = GameStatus.Draw
    }

     return copy(
         board = newBoard,
         activePlayer = if (newGameStatus == GameStatus.InProgress) activePlayer.next() else activePlayer,
         gameStatus = newGameStatus,
         freeSpaces = newFreeSpaces,
         lastMove = Position(rowNumber, column),
         winResult = set
     )
}

private fun winSet(board: List<List<Player?>>, column: Int, row: Int, activePlayer: Player, winLength: Int): Set<Position>? {
    val directions = listOf(Pair(0,1), Pair(1,0), Pair(1,-1), Pair(1, 1))
    for ((x, y) in directions) {
        val positions = mutableSetOf(Position(row, column))
        positions +=  countOneDirection(board, column, row, x, y, activePlayer)
        positions += countOneDirection(board, column, row, -x, -y, activePlayer)
        if (positions.size >= winLength) {return positions}
    }
    return null

}

private fun countOneDirection(board: List<List<Player?>>, column: Int, row: Int,
                              x : Int, y : Int, activePlayer: Player): Set<Position> {
    var i = row + x
    var j = column + y
    val maxColumns = board[0].size
    val maxRows = board.size
    val positions = mutableSetOf<Position>()
    while ( i >= 0 && j >= 0 && i < maxRows && j < maxColumns && board[i][j] == activePlayer) {
        positions += (Position(i, j))
        i += x; j += y
    }
    return positions
}