package com.example.bingo


import android.content.Context
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
import com.example.bingo.ui.theme.BingoTheme
import com.example.bingo.ui.theme.TaskTextStyle
import com.example.bingo.domain.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.MutableState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.text.input.ImeAction

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

    // НОВЫЕ СОСТОЯНИЯ ДЛЯ БИНГО
    val showBingoScreen = remember { mutableStateOf(false) }
    val currentBingoTask = remember { mutableStateOf<BingoTask?>(null) }
    // -------------------------

    SetBackgroundColor(colorBackground)

    if (showBingoScreen.value && currentBingoTask.value != null) {
        // Отображаем экран Бинго поверх всего
        BingoScreen(
            bingoTask = currentBingoTask.value!!,
            onClose = {
                showBingoScreen.value = false
                currentBingoTask.value = null
            }
        )
    } else {
        // Основной экран со списком задач
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

            // ... (Floating Action Buttons - остаются без изменений)

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
    // Оборачиваем список задач в state, чтобы Compose реагировал на изменения
    val tasksState = remember { mutableStateListOf<DisplayableTask>().apply { addAll(displayTasks) } }

    val mainTaskText = remember { mutableStateOf("") }
    val isSimpleTask = remember { mutableStateOf(true) }
    val subTasks = remember { mutableStateListOf("") }
    val colorTaskBlock = colorResource(id = R.color.task_block_option1)

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
                mainTaskText.value = ""
                subTasks.clear()
            },
            containerColor = colorTaskBlock,
            title = { Text("Новая задача") },
            text = {
                Column {
                    // Тип задачи
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSimpleTask.value,
                            onClick = { isSimpleTask.value = true }
                        )
                        Text(
                            text = "Простая задача",
                            modifier = Modifier.clickable { isSimpleTask.value = true }
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        RadioButton(
                            selected = !isSimpleTask.value,
                            onClick = { isSimpleTask.value = false }
                        )
                        Text(
                            text = "Продвинутая задача",
                            modifier = Modifier.clickable { isSimpleTask.value = false }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Основная задача
                    TextField(
                        value = mainTaskText.value,
                        onValueChange = { mainTaskText.value = it },
                        label = { Text("Основная задача") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Подзадачи
                    if (!isSimpleTask.value) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Подзадачи:")

                        subTasks.forEachIndexed { index, subTaskText ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextField(
                                    value = subTasks[index],
                                    onValueChange = { subTasks[index] = it },
                                    label = { Text("Подзадача ${index + 1}") },
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    modifier = Modifier.weight(1f)
                                )
                                if (index > 0) {
                                    IconButton(onClick = { subTasks.removeAt(index) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Удалить подзадачу"
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { subTasks.add("") }) {
                            Text("Добавить подзадачу")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Кнопка для тестового создания Бинго-сетки
                    Button(
                        onClick = {
                            val bingoManager = BingoManager()
                            for (i in 1..9) {
                                bingoManager.addTask("Цель Бинго №$i")
                            }
                            try {
                                val bingoGrid = bingoManager.generateBingoGrid()
                                val bingoTask = BingoTask(bingoGrid)
                                tasksState.add(DisplayableTask.Bingo(bingoTask))
                                displayTasks.clear()
                                displayTasks.addAll(tasksState)
                            } catch (e: IllegalStateException) {
                                println("Ошибка при создании Бинго: ${e.message}")
                            }

                            showDialog.value = false
                            mainTaskText.value = ""
                            subTasks.clear()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Создать Бинго-сетку (Тест)")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (mainTaskText.value.isNotBlank()) {
                        val newId = tasksState.size + 1
                        if (isSimpleTask.value) {
                            val task = SimpleTask(Task(id = newId, text = mainTaskText.value))
                            tasksState.add(DisplayableTask.Simple(task))
                        } else {
                            val advancedTask = AdvancedTask()
                            advancedTask.addTask(Task(id = newId, text = mainTaskText.value))
                            subTasks.filter { it.isNotBlank() }.forEachIndexed { index, it ->
                                advancedTask.addTask(Task(id = tasksState.size + index + 1, text = it))
                            }
                            tasksState.add(DisplayableTask.Advanced(advancedTask))
                        }

                        displayTasks.clear()
                        displayTasks.addAll(tasksState)

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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BingoBlockTemplate(
    color: Color,
    radius: Int = 12,
    payload: BingoTask,
    onOpenBingo: (BingoTask) -> Unit, // Callback для открытия экрана Бинго
    onDelete: (BingoTask) -> Unit // Callback для удаления блока
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Surface(
        color = color,
        shape = RoundedCornerShape(radius.dp),
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .wrapContentHeight()
            .animateContentSize()
            .combinedClickable(
                onClick = { onOpenBingo(payload) }, // Открываем экран Бинго
                onLongClick = { showDeleteDialog = true }
            ),
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "БИНГО! (3x3)", // Имя блока
                style = MaterialTheme.typography.titleLarge // или другой подходящий стиль
            )
        }
    }

    // Диалог подтверждения удаления (аналогично SimpleTask)
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = color,
            title = { Text("Удалить Бинго-сетку?") },
            text = { Text("Вы уверены, что хотите удалить эту Бинго-сетку?") },
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

@Composable
fun BingoScreen(
    bingoTask: BingoTask,
    onClose: () -> Unit // Callback для закрытия экрана
) {
    val colorBackground = colorResource(id = R.color.background_option1)
    val colorTaskBlock = colorResource(id = R.color.task_block_option1)
    val grid = bingoTask.stateGrid // Используем stateGrid для реактивности

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
            // Заголовок и кнопка закрытия
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "БИНГО Сетка",
                    style = MaterialTheme.typography.headlineMedium
                )
                Button(onClick = onClose) {
                    Text("Закрыть")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Сетка Бинго
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // 1:1 соотношение сторон для сетки
            ) {
                for (row in 0..2) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        for (col in 0..2) {
                            val task = grid[row][col]
                            // Обновляем состояние в Compose, чтобы при нажатии менялся вид
                            var isCompleted by remember { mutableStateOf(task.isCompleted) }

                            BingoTaskCell(
                                task = task,
                                isCompleted = isCompleted,
                                color = colorTaskBlock,
                                onClick = {
                                    // Обновляем модель и состояние Compose
                                    val newState = !isCompleted
                                    isCompleted = newState
                                    task.isCompleted = newState // Обновляем саму Task в модели
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // TODO: Можно добавить кнопку для сброса или проверки Бинго
            Button(onClick = { bingoTask.resetGrid() }) {
                Text("Сбросить сетку")
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
        color = if (isCompleted) Color(0xFF4CAF50) else color, // Зеленый, если выполнено
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .padding(4.dp)
            .clickable(onClick = onClick),
        shadowElevation = 2.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isCompleted) "✓" else "",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White
                )
                Text(
                    text = task.text,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall.copy(
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (isCompleted) Color.White else Color.Black
                )
            }
        }
    }
}