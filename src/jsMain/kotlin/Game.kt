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

data class GameState(
    val config: GameConfig = GameConfig(),
    val board: List<List<Player?>> = List(config.rows) { List(config.columns) { null } },
    val activePlayer: Player = Player.Red,
    val gameStatus: GameStatus = GameStatus.InProgress,
    val freeSpaces: Int = config.rows * config.columns
)

fun GameState.dropPiece(column: Int): GameState {
    if (column < 0 || column >= config.columns) error("Column must be in [0, ${board.size-1}]")

    val rowNumber = (config.rows - 1 downTo 0).firstOrNull { row -> board[row][column] == null } ?: error("full row")

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
    if (isWin(newBoard, column, rowNumber, activePlayer, config.winLength)) {
        newGameStatus = GameStatus.setWinner(activePlayer)
    } else if (newFreeSpaces == 0){
        newGameStatus = GameStatus.Draw
    }

     return copy(
         board = newBoard,
         activePlayer = if (newGameStatus == GameStatus.InProgress) activePlayer.next() else activePlayer,
         gameStatus = newGameStatus,
         freeSpaces = newFreeSpaces
     )
}

private fun isWin(board: List<List<Player?>>, column: Int, row: Int, activePlayer: Player, winLength: Int): Boolean {
    val directions = listOf(Pair(0,1), Pair(1,0), Pair(1,-1), Pair(1, 1))
    for ((x, y) in directions) {
        var count = 1
        count += countOneDirection(board, column, row, x, y, activePlayer)
        count += countOneDirection(board, column, row, -x, -y, activePlayer)
        if (count >= winLength) {return true}
    }
    return false

}

private fun countOneDirection(board: List<List<Player?>>, column: Int, row: Int,
                              x : Int, y : Int, activePlayer: Player): Int {
    var i = row + x
    var j = column + y
    val maxColumns = board[0].size
    val maxRows = board.size
    var count = 0
    while ( i >= 0 && j >= 0 && i < maxRows && j < maxColumns && board[i][j] == activePlayer) {
        count++
        i += x; j += y
    }
    return count
}