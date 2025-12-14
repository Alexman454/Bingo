package com.example.bingo


import android.os.Bundle
import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.example.bingo.domain.AdvancedTask
import com.example.bingo.domain.Task
import com.example.bingo.domain.DisplayableTask
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.MutableState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.combinedClickable
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BingoTheme {
                MainScreen()
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
fun MainScreen(){
    val showDialog = remember { mutableStateOf(false) }
    val newTaskText = remember { mutableStateOf("") }
    val statusBarH = getStatusBarH()
    val colorTaskBlock = colorResource(id = R.color.task_block_option1)
    val colorBackground = colorResource(id = R.color.background_option1)
    val task1 = SimpleTask(Task(1, "ОЧЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕНЬ ДЛИННЫЙ ТЕКСТ"))
    val task2 = SimpleTask(Task(2, "Вторая задача", isCompleted = true))
    var tasks = listOf(task1, task2)
    val advancedTask = AdvancedTask()
    advancedTask.addTask(Task(1, "Задача 1"))
    advancedTask.addTask(Task(2, "Задача 2"))
    val displayTasks = remember { mutableStateListOf<DisplayableTask>() }
    LaunchedEffect(Unit) {
        displayTasks.add(DisplayableTask.Simple(task1))
        displayTasks.add(DisplayableTask.Advanced(advancedTask))
        displayTasks.add(DisplayableTask.Simple(task2))
    }
    SetBackgroundColor(colorBackground)
    Box(modifier = Modifier.fillMaxSize()){
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(
                top = statusBarH,
                start = 12.dp,
                end = 12.dp
            ),verticalArrangement = Arrangement.spacedBy(12.dp)){
            items(displayTasks){item ->
                when (item){
                    is DisplayableTask.Simple -> TaskBlockTemplate(color = colorTaskBlock, payload = item.simpleTask,onDelete = { task ->
                        displayTasks.removeIf { it is DisplayableTask.Simple && it.simpleTask == task } })
                    is DisplayableTask.Advanced -> AdvancedTaskBlockTemplate(color = colorTaskBlock, payload = item.advancedTask,displayTasks = displayTasks)
                }
            }
        }
        FloatingActionButton(onClick = {showDialog.value = true}, modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp)){
            Icon(imageVector = Icons.Default.Add, contentDescription = "Добавить задачу.")
        }
        FloatingActionButton(
            onClick = { /* TODO: открыть меню настроек */ },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = "Настройки")
        }
        AddTaskDialog(showDialog = showDialog, displayTasks = displayTasks)
        }}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskBlockTemplate(
    color: Color,
    radius: Int = 12,
    payload: SimpleTask,
    onDelete: (SimpleTask) -> Unit
){
    var isCompleted by remember { mutableStateOf(payload.task.isCompleted) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Сначала сам блок задачи
    Surface(
        color = color,
        shape = RoundedCornerShape(radius.dp),
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .wrapContentHeight()
            .animateContentSize()
            .combinedClickable(
                onClick = {
                    isCompleted = !isCompleted
                    payload.task.isCompleted = isCompleted
                },
                onLongClick = {
                    showDeleteDialog = true
                }
            ),
        shadowElevation = 4.dp
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = payload.task.text,
                style = TaskTextStyle.copy(
                    textDecoration = if (isCompleted)
                        TextDecoration.LineThrough
                    else
                        TextDecoration.None
                )
            )
        }
    }

    // Диалог подтверждения удаления
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = color, // цвет блока задачи
            title = { Text("Удалить задачу?") },
            text = { Text("Вы уверены, что хотите удалить эту задачу?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(payload)
                    showDeleteDialog = false
                }) {
                    Text("Да")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Нет")
                }
            }
        )
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdvancedTaskBlockTemplate(
    color: Color,
    payload: AdvancedTask,
    displayTasks: MutableList<DisplayableTask>,
    radius: Int = 12
) {
    Surface(
        color = color,
        shape = RoundedCornerShape(radius.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .animateContentSize(),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            val tasks = remember { mutableStateListOf<Task>() }
            LaunchedEffect(payload) {
                tasks.clear()
                tasks.addAll(payload.getAllTasks())
            }

            tasks.forEach { task ->
                var isCompleted by remember { mutableStateOf(task.isCompleted) }
                var showDeleteDialog by remember { mutableStateOf(false) }

                Text(
                    text = task.text,
                    style = TaskTextStyle.copy(
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .combinedClickable(
                            onClick = {
                                isCompleted = !isCompleted
                                task.isCompleted = isCompleted
                            },
                            onLongClick = { showDeleteDialog = true }
                        )
                )

                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        containerColor = color, // цвет блока задачи
                        title = { Text("Удалить подзадачу?") },
                        text = { Text("Вы уверены, что хотите удалить эту подзадачу?") },
                        confirmButton = {
                            TextButton(onClick = {
                                payload.removeTask(task.id)
                                tasks.remove(task)
                                if (payload.getAllTasks().isEmpty()) {
                                    displayTasks.removeIf { it is DisplayableTask.Advanced && it.advancedTask == payload }
                                } else {
                                    val index = displayTasks.indexOfFirst { it is DisplayableTask.Advanced && it.advancedTask == payload }
                                    if (index != -1) {
                                        displayTasks[index] = DisplayableTask.Advanced(payload)
                                    }
                                }
                                showDeleteDialog = false
                            }) {
                                Text("Да")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) {
                                Text("Нет")
                            }
                        }
                    )
                }
            }
        }
    }
}




@Composable
fun getStatusBarH(): Dp {
    val statusBarInsets = WindowInsets.statusBars.asPaddingValues()
    val statusBarHeightDp = statusBarInsets.calculateTopPadding()
    return statusBarHeightDp
}

@Composable
fun AddTaskDialog(
    showDialog: MutableState<Boolean>,
    displayTasks: MutableList<DisplayableTask>
) {
    val mainTaskText = remember { mutableStateOf("") }
    val isSimpleTask = remember { mutableStateOf(true) }
    val subTasks = remember { mutableStateListOf("") }

    if (showDialog.value) {
        val colorTaskBlock = colorResource(id = R.color.task_block_option1)

        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
                mainTaskText.value = ""
                subTasks.clear()
            },
            containerColor = colorTaskBlock, // цвет блока задачи
            title = { Text("Новая задача") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = mainTaskText.value,
                        onValueChange = { mainTaskText.value = it },
                        placeholder = { Text("Введите текст задачи") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isSimpleTask.value,
                                onClick = { isSimpleTask.value = true }
                            )
                            Text("Простая задача", modifier = Modifier.padding(start = 8.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = !isSimpleTask.value,
                                onClick = { isSimpleTask.value = false }
                            )
                            Text("Сложная задача", modifier = Modifier.padding(start = 8.dp))
                        }
                    }

                    if (!isSimpleTask.value) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Подзадачи:", style = MaterialTheme.typography.bodyMedium)
                        subTasks.forEachIndexed { index, subTask ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                TextField(
                                    value = subTasks[index],
                                    onValueChange = { subTasks[index] = it },
                                    placeholder = { Text("Текст подзадачи") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Done
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(onClick = { subTasks.removeAt(index) }) {
                                    Text("Удалить")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { subTasks.add("") }) {
                            Text("Добавить подзадачу")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (mainTaskText.value.isNotBlank()) {
                        if (isSimpleTask.value) {
                            val task = SimpleTask(Task(id = displayTasks.size + 1, text = mainTaskText.value))
                            displayTasks.add(DisplayableTask.Simple(task))
                        } else {
                            val advancedTask = AdvancedTask()
                            advancedTask.addTask(Task(id = displayTasks.size + 1, text = mainTaskText.value))
                            subTasks.filter { it.isNotBlank() }.forEach { text -> advancedTask.addTask(Task(id = displayTasks.size + 1, text = text)) }
                            displayTasks.add(DisplayableTask.Advanced(advancedTask))
                        }
                        mainTaskText.value = ""
                        subTasks.clear()
                        showDialog.value = false
                    }
                }) {
                    Text("Добавить")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog.value = false
                    mainTaskText.value = ""
                    subTasks.clear()
                }) {
                    Text("Отмена")
                }
            }
        )
    }
}



