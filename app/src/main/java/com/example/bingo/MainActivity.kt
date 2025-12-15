package com.example.bingo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.Button
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.text.style.TextDecoration
import com.example.bingo.ui.theme.BingoTheme
import com.example.bingo.ui.theme.TaskTextStyle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.MutableState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import com.example.bingo.domain.AdvancedTask
import com.example.bingo.domain.BingoManager
import com.example.bingo.domain.BingoTask
import com.example.bingo.domain.DisplayableTask
import com.example.bingo.domain.SimpleTask
import com.example.bingo.domain.Task
import com.example.bingo.domain.TaskType

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
fun MainScreen() {
    val colorTaskBlock = colorResource(id = R.color.task_block_option1)
    val colorBackground = colorResource(id = R.color.background_option1)
    val statusBarH = getStatusBarH()
    val displayTasks = remember { mutableStateListOf<DisplayableTask>() }
    val showDialog = remember { mutableStateOf(false) }
    val showBingoScreen = remember { mutableStateOf(false) }
    val currentBingoTask = remember { mutableStateOf<BingoTask?>(null) }

    SetBackgroundColor(colorBackground)

    if (showBingoScreen.value && currentBingoTask.value != null) {
        BingoScreen(
            bingoTask = currentBingoTask.value!!,
            onClose = {
                showBingoScreen.value = false
                currentBingoTask.value = null
            }
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = statusBarH, start = 12.dp, end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayTasks) { item ->
                    when (item) {
                        is DisplayableTask.Simple -> TaskBlockTemplate(
                            color = colorTaskBlock,
                            payload = item.simpleTask,
                            onDelete = { task ->
                                displayTasks.removeIf { it is DisplayableTask.Simple && it.simpleTask == task }
                            }
                        )
                        is DisplayableTask.Advanced -> AdvancedTaskBlockTemplate(
                            color = colorTaskBlock,
                            payload = item.advancedTask,
                            displayTasks = displayTasks
                        )
                        is DisplayableTask.Bingo -> BingoBlockTemplate( // <-- ОБРАБОТКА БИНГО БЛОКА
                            color = colorTaskBlock,
                            payload = item.bingoTask,
                            onOpenBingo = { bingoTask ->
                                currentBingoTask.value = bingoTask
                                showBingoScreen.value = true
                            },
                            onDelete = { bingoTask ->
                                displayTasks.removeIf { it is DisplayableTask.Bingo && it.bingoTask == bingoTask }
                            }
                        )
                    }
                }
            }

            FloatingActionButton(
                onClick = { showDialog.value = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Добавить задачу")
            }

            FloatingActionButton(
                onClick = { /* TODO: открыть меню настроек */ },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Настройки")
            }

            AddTaskDialog(
                showDialog = showDialog,
                displayTasks = displayTasks
            )
        }
    }
}

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
    if (!showDialog.value) return

    val taskType = remember { mutableStateOf(TaskType.SIMPLE) }
    val inputText = remember { mutableStateOf("") }

    val advancedSubTasks = remember { mutableStateListOf<String>() }
    val bingoTasks = remember { mutableStateListOf<String>() }

    val colorTaskBlock = colorResource(id = R.color.task_block_option1)

    fun resetState() {
        inputText.value = ""
        advancedSubTasks.clear()
        bingoTasks.clear()
        showDialog.value = false
    }

    AlertDialog(
        onDismissRequest = { resetState() },
        containerColor = colorTaskBlock,
        title = { Text("Новая задача") },

        text = {
            Column {

                /* ---------- Выбор типа задачи ---------- */
                Column {
                    TaskType.values().forEach { type ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { taskType.value = type }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = taskType.value == type,
                                onClick = { taskType.value = type }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = when (type) {
                                    TaskType.SIMPLE -> "Простая задача"
                                    TaskType.ADVANCED -> "Продвинутая задача"
                                    TaskType.BINGO -> "Bingo"
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                /* ---------- Поле ввода ---------- */
                TextField(
                    value = inputText.value,
                    onValueChange = { inputText.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            when (taskType.value) {
                                TaskType.SIMPLE -> "Текст задачи"
                                TaskType.ADVANCED -> "Подзадача"
                                TaskType.BINGO -> "Цель для Bingo"
                            }
                        )
                    }
                )

                Spacer(Modifier.height(8.dp))

                /* ---------- Кнопка добавления ---------- */
                if (taskType.value != TaskType.SIMPLE) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            if (inputText.value.isNotBlank()) {
                                when (taskType.value) {
                                    TaskType.ADVANCED ->
                                        advancedSubTasks.add(inputText.value)

                                    TaskType.BINGO ->
                                        bingoTasks.add(inputText.value)

                                    else -> {}
                                }
                                inputText.value = ""
                            }
                        }
                    ) {
                        Text("Добавить")
                    }
                }

                Spacer(Modifier.height(8.dp))

                /* ---------- Списки ---------- */
                when (taskType.value) {
                    TaskType.ADVANCED -> TaskPreviewList(advancedSubTasks)
                    TaskType.BINGO -> TaskPreviewList(bingoTasks)
                    else -> {}
                }

                /* ---------- Валидация Bingo ---------- */
                if (taskType.value == TaskType.BINGO) {
                    Spacer(Modifier.height(8.dp))

                    val count = bingoTasks.size
                    val size = kotlin.math.sqrt(count.toDouble())
                        .toInt()
                        .coerceIn(2, 5)

                    Text(
                        text = when {
                            count < 4 ->
                                "Добавьте минимум 4 цели для Bingo"
                            else ->
                                "Будет создано Bingo ${size}×${size}"
                        },
                        color = if (count < 4) Color.Red else Color.Green,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },

        /* ---------- Создание задачи ---------- */
        confirmButton = {
            TextButton(onClick = {
                when (taskType.value) {

                    TaskType.SIMPLE -> {
                        if (inputText.value.isNotBlank()) {
                            displayTasks.add(
                                DisplayableTask.Simple(
                                    SimpleTask(Task(0, inputText.value))
                                )
                            )
                        }
                    }

                    TaskType.ADVANCED -> {
                        if (advancedSubTasks.isNotEmpty()) {
                            val advancedTask = AdvancedTask()
                            advancedSubTasks.forEachIndexed { index, text ->
                                advancedTask.addTask(Task(index, text))
                            }
                            displayTasks.add(
                                DisplayableTask.Advanced(advancedTask)
                            )
                        }
                    }

                    TaskType.BINGO -> {
                        if (bingoTasks.size >= 4) {
                            val manager = BingoManager()
                            bingoTasks.forEach { manager.addTask(it) }

                            displayTasks.add(
                                DisplayableTask.Bingo(
                                    manager.generateBingo()
                                )
                            )
                        }
                    }
                }

                resetState()
            }) {
                Text("Создать")
            }
        },

        dismissButton = {
            TextButton(onClick = { resetState() }) {
                Text("Отмена")
            }
        }
    )
}




@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BingoBlockTemplate(
    color: Color,
    radius: Int = 12,
    payload: BingoTask,
    onOpenBingo: (BingoTask) -> Unit,
    onDelete: (BingoTask) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Surface(
        color = color,
        shape = RoundedCornerShape(radius.dp),
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .combinedClickable(
                onClick = { onOpenBingo(payload) },
                onLongClick = { showDeleteDialog = true }
            ),
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "БИНГО! (${payload.size}×${payload.size})",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = color,
            title = { Text("Удалить Bingo?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(payload)
                    showDeleteDialog = false
                }) { Text("Да") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Нет")
                }
            }
        )
    }
}


@Composable
fun BingoScreen(
    bingoTask: BingoTask,
    onClose: () -> Unit
) {
    val colorBackground = colorResource(id = R.color.background_option1)
    val colorTaskBlock = colorResource(id = R.color.task_block_option1)
    val grid = bingoTask.stateGrid
    val size = bingoTask.size

    Surface(
        color = colorBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Bingo ${size}×${size}",
                    style = MaterialTheme.typography.headlineMedium
                )
                Button(onClick = onClose) {
                    Text("Закрыть")
                }
            }

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                for (row in 0 until size) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0 until size) {
                            val task = grid[row][col]
                            var isCompleted by remember {
                                mutableStateOf(task.isCompleted)
                            }

                            BingoTaskCell(
                                task = task,
                                isCompleted = isCompleted,
                                color = colorTaskBlock,
                                onClick = {
                                    val newState = !isCompleted
                                    isCompleted = newState
                                    task.isCompleted = newState
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(onClick = { bingoTask.resetGrid() }) {
                Text("Сбросить")
            }
        }
    }
}


@Composable
fun RowScope.BingoTaskCell(
    task: Task,
    isCompleted: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        color = if (isCompleted) Color(0xFF4CAF50) else color,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .padding(4.dp)
            .clickable(onClick = onClick),
        shadowElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = task.text,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = if (isCompleted) Color.White else Color.Black
            )
        }
    }
}


@Composable
fun TaskPreviewList(items: MutableList<String>) {
    Column {
        items.forEachIndexed { index, text ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("• $text", Modifier.weight(1f))
                IconButton(onClick = { items.removeAt(index) }) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                }
            }
        }
    }
}