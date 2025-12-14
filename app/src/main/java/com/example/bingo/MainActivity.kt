package com.example.bingo


import android.os.Bundle
import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import com.example.bingo.ui.theme.BingoTheme
import com.example.bingo.ui.theme.TaskTextStyle
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BingoTheme {
                val statusBarH = getStatusBarH()
                val colorTaskBlock = colorResource(id = R.color.task_block_option1)
                val colorBackground = colorResource(id = R.color.background_option1)
                SetBackgroundColor(colorBackground)
                TaskBlockTemplate(color = colorTaskBlock, radius = 12, offsetX = 12, offsetY = statusBarH)
                TaskBlockTemplate(color = colorTaskBlock, radius = 12, offsetX = 12, offsetY = statusBarH+112.dp)
                TaskBlockTemplate(color = colorTaskBlock, radius = 12, offsetX = 12, offsetY = statusBarH+224.dp)
                TaskBlockTemplate(color = colorTaskBlock, radius = 12, offsetX = 12, offsetY = statusBarH+336.dp)
            }
        }
    }
}
@Composable
fun SetBackgroundColor(color: Color){
    Surface(color = color, modifier = Modifier.fillMaxSize()){
    }
}

@Composable
fun TaskBlockTemplate(
    color: Color,
    radius: Int = 1,
    offsetX: Int = 0,
    offsetY: Dp = 0.dp,
    payload: String = "Placeholder Text"
){
    val configuration = Resources.getSystem().configuration
    val widthDp = configuration.screenWidthDp
    //val heightDp = configuration.screenHeightDp
    Surface(color = color,shape = RoundedCornerShape(radius.dp),
        modifier = Modifier
            .size(width = (widthDp.dp - (offsetX*2).dp),height = 100.dp)
            .absoluteOffset(x = offsetX.dp,y = offsetY),
        shadowElevation = 4.dp
    ){
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ){
            Text(text = payload, style = TaskTextStyle)
    }}}

@Composable
fun getStatusBarH(): Dp {
    val statusBarInsets = WindowInsets.statusBars.asPaddingValues()
    val statusBarHeightDp = statusBarInsets.calculateTopPadding()
    return statusBarHeightDp
}