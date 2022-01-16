package demo.mygame.tictactoe

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

/**
 * [ViewModel] class for holding Observable field(s) and event handling.
 */
class MainViewModel : ViewModel() {

    val message = mutableStateOf<String?>(null)
    val matrixSize = mutableStateOf(3)
    val highlight = mutableStateListOf<Int>()
    var matrixArray = mutableStateListOf<Player>()
    val currentPlayer = mutableStateOf(Player.NONE)
    val showSettingsDialog = mutableStateOf(true)
    val totalPlayers = mutableStateOf(2)

    fun saveSettings(size: String, players: String) {
        size.toInt().let {
            matrixSize.value = when {
                it < 3 -> 3
                it > 12 -> 12
                else -> it
            }
        }
        players.toInt().let {
            totalPlayers.value = when {
                it < 2 -> 2
                it > 4 -> 4
                else -> it
            }
        }
        showSettingsDialog.value = false
        reloadGameAction()
    }

    fun reloadGameAction() {
        val tempMatrixArray = mutableListOf<Player>()
        repeat(matrixSize.value * matrixSize.value) { tempMatrixArray.add(Player.NONE) }
        highlight.clear()
        matrixArray.clear()
        matrixArray.addAll(tempMatrixArray)
        currentPlayer.value = Player.PLAYER_1
    }

    fun currentPlayerAction(position: Int) {
        if (currentPlayer.value == Player.NONE) return
        else if (matrixArray[position] != Player.NONE) return
        matrixArray[position] = currentPlayer.value
        if (didPlayerWon(position = position)) gameWon()
        else nextPlayer()
    }

    private fun didPlayerWon(position: Int): Boolean {

        val size: Int = matrixSize.value
        val Arr: List<Player> = matrixArray.toList()
        val x: Int = position / matrixSize.value
        val y: Int = position % matrixSize.value
        val player: Player = currentPlayer.value
        var h = true
        var v = true
        var d1 = true
        var d2 = true

        for (i in 0 until size) {
            if (!h && !v && !d1 && !d2) return false
            if (Arr[(i * size) + y].code != player.code) h = false
            if (Arr[(x * size) + i].code != player.code) v = false
            if (x == y && d1) {
                if (Arr[(i * size) + i].code != player.code) d1 = false
            } else d1 = false
            if ((x + y) == (size - 1) && d2) {
                if (Arr[(size - 1) * i].code != player.code) d2 = false
            } else d2 = false
        }

        if (h) {
            for (i in 0 until size)
                highlight.add((i * size) + y)
        }
        if (v) {
            for (i in 0 until size)
                highlight.add((x * size) + i)
        }
        if (d1) {
            for (i in 0 until size)
                highlight.add((i * size) + i)
        }
        if (d2) {
            for (i in 0 until size)
                highlight.add((size - 1) * i)
        }

        return h || v || d1 || d2
    }

    private fun gameWon() {
        message.value = "${currentPlayer.value.label} Won"
        currentPlayer.value = Player.NONE
    }

    private fun nextPlayer() {
        when (currentPlayer.value) {
            Player.PLAYER_1 -> currentPlayer.value = Player.PLAYER_2
            Player.PLAYER_2 -> {
                if (totalPlayers.value > 2) currentPlayer.value = Player.PLAYER_3
                else currentPlayer.value = Player.PLAYER_1
            }
            Player.PLAYER_3 -> {
                if (totalPlayers.value > 3) currentPlayer.value = Player.PLAYER_4
                else currentPlayer.value = Player.PLAYER_1
            }
            Player.PLAYER_4 -> currentPlayer.value = Player.PLAYER_1
        }
    }

}

enum class Player(var label: String?, val code: Int) {
    NONE(label = null, code = -1),
    PLAYER_1(label = "Player 1", code = 1), // Mark = 0
    PLAYER_2(label = "Player 2", code = 2), // Mark = x
    PLAYER_3(label = "Player 3", code = 3), // Mark = $
    PLAYER_4(label = "Player 4", code = 4)  // Mark = V (Tick)
}
