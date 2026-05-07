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