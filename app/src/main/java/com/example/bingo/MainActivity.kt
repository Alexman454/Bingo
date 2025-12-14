package com.example.bingo


import android.os.Bundle
import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.DpOffset
import com.example.bingo.domain.SimpleTask
import com.example.bingo.ui.theme.BingoTheme
import com.example.bingo.ui.theme.TaskTextStyle
import com.example.bingo.domain.Task
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
                val task1 = SimpleTask(Task(1, "ОЧЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕНЬ ДЛИННЫЙ ТЕКСТ"))
                val task2 = SimpleTask(Task(2, "Вторая задача", isCompleted = true))
                var tasks = listOf(task1,task2)
                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = statusBarH,
                        start = 12.dp,
                        end = 12.dp
                    ),verticalArrangement = Arrangement.spacedBy(12.dp)){
                items(tasks){task ->
                    TaskBlockTemplate(color = colorTaskBlock, payload = task)
                }
            }}
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
    radius: Int = 12,
    payload: SimpleTask
){
    val configuration = Resources.getSystem().configuration
    val widthDp = configuration.screenWidthDp
    val isCompleted = payload.task.isCompleted
    //val heightDp = configuration.screenHeightDp
    Surface(color = color,shape = RoundedCornerShape(radius.dp),
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .wrapContentHeight(),
        shadowElevation = 4.dp
    ){
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ){
            Text(text = payload.task.text, style = TaskTextStyle.copy(
                textDecoration = if (isCompleted)
                TextDecoration.LineThrough
            else
                TextDecoration.None))
    }}}


@Composable
fun getStatusBarH(): Dp {
    val statusBarInsets = WindowInsets.statusBars.asPaddingValues()
    val statusBarHeightDp = statusBarInsets.calculateTopPadding()
    return statusBarHeightDp
}