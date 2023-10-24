package com.cursokotlin.todoapp.addtasks.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.cursokotlin.todoapp.addtasks.ui.model.TaskModel
import com.cursokotlin.todoapp.navigation.AppsScreens

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FirstScreen(tasksViewModel: TasksViewModel, navController: NavHostController){
    Scaffold(
        topBar = {
            TopAppBar() {
                Text(text = "Task List",
                    modifier = Modifier.padding(start = 16.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) {
        TasksScreen(tasksViewModel = tasksViewModel, navController = navController)
    }
}

@Composable
fun TasksScreen(tasksViewModel: TasksViewModel, navController: NavHostController) {
    val showDialog: Boolean by tasksViewModel.showDialog.observeAsState(false)
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val uiState by produceState<TasksUiState>(
        initialValue = TasksUiState.Loading,
        key1 = lifecycle,
        key2 = tasksViewModel
    ) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            tasksViewModel.uiState.collect { value = it }
        }
    }

    when (uiState) {
        is TasksUiState.Error -> {}
        TasksUiState.Loading -> {
            Column() {
                Spacer(modifier = Modifier.weight(1f))
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(150.dp)
                        .weight(1f)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        is TasksUiState.Success -> {
            Box(modifier = Modifier.fillMaxSize()) {
                AddTasksDialog(
                    showDialog,
                    onDismiss = { tasksViewModel.onDialogClose() },
                    onTaskAdded = { tasksViewModel.onTasksCreated(it) })
                FabDialog(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp), tasksViewModel
                )
                TasksList((uiState as TasksUiState.Success).tasks, tasksViewModel)
                Button(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    onClick = {
                        navController.navigate(route = AppsScreens.SecondScreen.route)
                    }) {
                    Text(text = "Naviggation")
                }
                ConfirmDeleteTask(
                    show = tasksViewModel.showConfirmDeleteTask.observeAsState(false).value,
                    tasksViewModel = tasksViewModel,
                    onDismiss = { tasksViewModel.onConfirmDeleteDialogClose() },
                    taskModel = tasksViewModel.taskSelected.value
                )
            }
        }
    }

}

@Composable
fun TasksList(tasks: List<TaskModel>, tasksViewModel: TasksViewModel) {
    LazyColumn {
        items(tasks, key = { it.id }) { task ->
            ItemTask(task, tasksViewModel)
        }
    }
}

@Composable
fun ItemTask(taskModel: TaskModel, tasksViewModel: TasksViewModel) {
    Card(
        Modifier
            .height(80.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    tasksViewModel.onShowConfirmDeleteDialogClick(taskModel)
                })
            },
        elevation = 8.dp
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = taskModel.taskTitle.replaceFirstChar { it.uppercase() },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .weight(1f),
                    fontWeight = FontWeight.Bold

                )
                Text(
                    text = taskModel.task, modifier = Modifier
                        .padding(bottom = 8.dp)
                        .weight(1f)
                )
            }
            Checkbox(
                checked = taskModel.selected,
                onCheckedChange = { tasksViewModel.onCheckBoxSelected(taskModel) })
        }
    }
}

@Composable
fun FabDialog(modifier: Modifier, tasksViewModel: TasksViewModel) {
    FloatingActionButton(onClick = {
        tasksViewModel.onShowDialogClick()
    }, modifier = modifier) {
        Icon(Icons.Filled.Add, contentDescription = "")
    }
}

@Composable
fun ConfirmDeleteTask(
    show: Boolean,
    tasksViewModel: TasksViewModel,
    onDismiss: () -> Unit,
    taskModel: TaskModel?
) {
    if (show) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text(
                    text = "DELETE TASK",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = "Are you sure to delete this task?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.size(16.dp))
                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            onDismiss()
                        }, modifier = Modifier.weight(1f).padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                    ) {
                        Text(text = "NO", color = Color.White)
                    }
                    Button(
                        onClick = {
                            taskModel?.let { tasksViewModel.onItemRemove(it) }
                        }, modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green)

                    ) {
                        Text(text = "YES", color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun AddTasksDialog(show: Boolean, onDismiss: () -> Unit, onTaskAdded: (TaskModel) -> Unit) {
    var myTask by remember { mutableStateOf("") }
    var myTitleTask by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(false) } // 1
    var taskError by remember { mutableStateOf(false) } // 1
    if (show) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                TextField(
                    value = myTitleTask,
                    onValueChange = {
                        myTitleTask = it
                        titleError = false // 2
                    },
                    singleLine = true,
                    label = { Text("Title") },
                    maxLines = 1,
                    isError = titleError // 3
                )
                ErrorText(isError = titleError)
                Spacer(modifier = Modifier.size(8.dp))
                TextField(
                    value = myTask,
                    onValueChange = {
                        myTask = it
                        taskError = false // 2
                    },
                    singleLine = true,
                    label = { Text("Write here your task") },
                    maxLines = 1,
                    isError = taskError // 3
                )
                ErrorText(isError = taskError)
                Spacer(modifier = Modifier.size(16.dp))
                Button(
                    onClick = {
                        taskError = myTask.isEmpty()
                        titleError = myTitleTask.isEmpty()
                        if (!taskError && !titleError) {
                            onTaskAdded(TaskModel(taskTitle = myTitleTask, task = myTask))
                            myTitleTask = ""
                            myTask = ""
                            taskError = false
                            titleError = false
                        }
                    }, modifier = Modifier
                        .width(100.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Add task")
                }
            }
        }
    }
}

@Composable
fun ErrorText(isError: Boolean) {
    val assistiveElementText = if (isError) "Dato Obligatorio" else "*Obligatorio" // 4
    val assistiveElementColor = if (isError) { // 5
        MaterialTheme.colors.error
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    }
    Text(// 6
        text = assistiveElementText,
        color = assistiveElementColor,
        style = MaterialTheme.typography.caption,
        modifier = Modifier.padding(start = 16.dp)
    )
}
