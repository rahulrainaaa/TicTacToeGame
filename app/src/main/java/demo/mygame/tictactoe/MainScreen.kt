package demo.mygame.tictactoe

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

fun getPlayerIcon(player: Player): Int {
    return when (player) {
        Player.PLAYER_1 -> R.drawable.ic_zero
        Player.PLAYER_2 -> R.drawable.ic_cross
        Player.PLAYER_3 -> R.drawable.ic_dollar
        Player.PLAYER_4 -> R.drawable.ic_tick
        else -> R.drawable.ic_launcher_foreground
    }
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun DrawScreen(viewModel: MainViewModel) {

    val scaffoldState = rememberScaffoldState(rememberDrawerState(initialValue = DrawerValue.Closed))

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { },
        bottomBar = { DrawBottomAppBar(viewModel = viewModel) },
        content = { DrawContentBody(viewModel = viewModel) },
        floatingActionButton = { DrawFloatingActionButton(viewModel = viewModel) },
        floatingActionButtonPosition = FabPosition.End,
        isFloatingActionButtonDocked = true,
        drawerContent = null
    )

    if (viewModel.showSettingsDialog.value)
        Dialog(onDismissRequest = { }) {
            DrawSettingsDialog(viewModel = viewModel)
        }
}

@Composable
fun DrawFloatingActionButton(viewModel: MainViewModel) {
    FloatingActionButton(onClick = { viewModel.reloadGameAction() }) {
        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reload")
    }
}

@Composable
fun DrawBottomAppBar(viewModel: MainViewModel) {
    val currentPlayer by viewModel.currentPlayer
    BottomAppBar(
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.primary,
        cutoutShape = CutCornerShape(size = 12.dp)
    ) {
        Row {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.Settings, contentDescription = "Settings", Modifier.clickable {
                viewModel.showSettingsDialog.value = true
            })
            Spacer(modifier = Modifier.width(8.dp))
            if (currentPlayer == Player.NONE) viewModel.message.value?.let { Text(text = it) }
            else Text(text = "Turn for ${currentPlayer.label}")
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun DrawContentBody(viewModel: MainViewModel) {

    LazyVerticalGrid(
        cells = GridCells.Fixed(viewModel.matrixSize.value),
        modifier = Modifier
            .background(color = Color.LightGray, shape = RoundedCornerShape(0.dp))
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        items(viewModel.matrixArray.count()) { position: Int ->

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                DrawSingleCell(viewModel = viewModel, position = position) {
                    viewModel.currentPlayerAction(position)
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun DrawSingleCell(viewModel: MainViewModel, position: Int, onClick: () -> Unit) {

    val player = viewModel.matrixArray[position]
    val highlight = viewModel.highlight.contains(position)
    Card(
        elevation = 20.dp,
        backgroundColor = if (highlight) Color.Yellow else Color.White,
        onClick = onClick,
        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 12.dp, horizontal = 12.dp)
    ) {
        Icon(
            painter = painterResource(id = getPlayerIcon(player = player)),
            contentDescription = "",
            modifier = Modifier
                .height(100.dp)
                .width(100.dp)
                .alpha(if (player == Player.NONE) 0F else 1F)
        )
        player.label?.let {
            Text(
                text = it,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Light,
                fontSize = 10.sp,
                fontFamily = FontFamily.Cursive,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun DrawSettingsDialog(viewModel: MainViewModel) {

    ConstraintLayout(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
    ) {

        val (cLabel, cTextPlayer, cTextBlockSize, cBtnSave, cBtnDismiss) = createRefs()

        Text(
            text = "Game Level",
            modifier = Modifier.constrainAs(cLabel) {
                top.linkTo(anchor = parent.top, margin = 16.dp)
                start.linkTo(anchor = parent.start, margin = 16.dp)
                height = Dimension.wrapContent
                width = Dimension.wrapContent
            },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif
        )

        var totalPlayers by remember { mutableStateOf(viewModel.totalPlayers.value.toString()) }
        OutlinedTextField(
            value = totalPlayers,
            onValueChange = { totalPlayers = it },
            label = { Text("Total Players (Min:2, Max:4)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.constrainAs(cTextPlayer) {
                top.linkTo(anchor = cLabel.bottom, margin = 16.dp)
                start.linkTo(anchor = parent.start, margin = 8.dp)
                end.linkTo(anchor = parent.end, margin = 8.dp)
            })

        var totalBlockSize by remember { mutableStateOf(viewModel.matrixSize.value.toString()) }
        OutlinedTextField(
            value = totalBlockSize,
            onValueChange = { totalBlockSize = it },
            label = { Text("Total Blocks (Min:3, Max:12)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.constrainAs(cTextBlockSize) {
                top.linkTo(anchor = cTextPlayer.bottom, margin = 8.dp)
                start.linkTo(anchor = parent.start, margin = 8.dp)
                end.linkTo(anchor = parent.end, margin = 8.dp)
            })

        Button(onClick = { viewModel.saveSettings(totalBlockSize, totalPlayers) },
            modifier = Modifier.constrainAs(cBtnSave) {
                end.linkTo(anchor = parent.end, margin = 8.dp)
                top.linkTo(anchor = cTextBlockSize.bottom, margin = 20.dp)
                bottom.linkTo(anchor = parent.bottom, margin = 8.dp)
            }) {
            Text(text = "Save & Reload")
        }

        OutlinedButton(onClick = { viewModel.showSettingsDialog.value = false },
            modifier = Modifier.constrainAs(cBtnDismiss) {
                end.linkTo(anchor = cBtnSave.start, margin = 8.dp)
                top.linkTo(anchor = cBtnSave.top)
            }) {
            Text(text = "Dismiss")
        }
    }
}