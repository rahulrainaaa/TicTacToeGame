package demo.mygame.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.lifecycle.ViewModelProvider
import demo.mygame.tictactoe.ui.theme.TicTacToeTheme

/**
 * [ComponentActivity] class to handle UI.
 */
@ExperimentalMaterialApi
@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContent {
            TicTacToeTheme {
                Surface(color = MaterialTheme.colors.background) {
                    DrawScreen(viewModel)
                }
            }
        }
        viewModel.reloadGameAction()
    }
}