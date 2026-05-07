import org.example.GameConfig
import org.example.GameState
import org.example.GameStatus
import org.example.Player
import org.example.Position
import org.example.dropPiece
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class GameTest {
    @Test
    fun pieceFallsToBottomOfColumn() {
        val game = GameState(GameConfig(6, 7, 4))
        val update = game.dropPiece(0)

        assertEquals(Player.Red, update.board[5][0])
    }

    @Test
    fun changePlayerAfterMove() {
        val game = GameState()
        val update = game.dropPiece(0)

        assertEquals(Player.Yellow, update.activePlayer)
        assertNotEquals(game.activePlayer, update.activePlayer)

    }

    @Test
    fun collisionBetweenPieces() {
        var game = GameState(GameConfig(6, 7, 4))
        game = game.dropPiece(0)
        game = game.dropPiece(0)
        assertEquals(Player.Red, game.board[5][0])
        assertEquals(Player.Yellow, game.board[4][0])
    }

    @Test
    fun detectWin() {
        var game = GameState()
        game = game.dropPiece(0)
        game = game.dropPiece(1)
        game = game.dropPiece(0)
        game = game.dropPiece(1)
        game = game.dropPiece(0)
        game = game.dropPiece(1)
        game = game.dropPiece(0)

        assertEquals(GameStatus.RedWin, game.gameStatus)
        assertTrue { Position(5,0) in game.winResult!! }
        assertTrue { Position(4,0) in game.winResult!! }
        assertTrue { Position(3,0) in game.winResult!! }
        assertTrue { Position(2,0) in game.winResult!! }

        val gameAfterExtraMove = game.dropPiece(1)

        assertEquals(game, gameAfterExtraMove)
    }

    @Test
    fun detectDraw() {
        var game = GameState(config = GameConfig(2, 3, 3))
        game = game.dropPiece(0)
        game = game.dropPiece(1)
        game = game.dropPiece(2)
        game = game.dropPiece(0)
        game = game.dropPiece(1)
        game = game.dropPiece(2)

        assertEquals(GameStatus.Draw, game.gameStatus)
        val gameAfterExtraMove = game.dropPiece(1)

        assertEquals(game, gameAfterExtraMove)
    }

    @Test
    fun rejectsInvalidConfigs() {
        assertFailsWith<IllegalArgumentException> {
            GameConfig(rows = 6, columns = 7, winLength = 10)
        }
        assertFailsWith<IllegalArgumentException> {
            GameConfig(rows = 0, columns = 7, winLength = 1)
        }
        assertFailsWith<IllegalArgumentException> {
            GameConfig(rows = 6, columns = 0, winLength = 1)
        }
        assertFailsWith<IllegalArgumentException> {
            GameConfig(rows = 21, columns = 7, winLength = 1)
        }
        assertFailsWith<IllegalArgumentException> {
            GameConfig(rows = 6, columns = 21, winLength = 1)
        }
    }
}