package com.cursokotlin.todoapp.addtasks.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursokotlin.todoapp.addtasks.domain.AddTaskUseCase
import com.cursokotlin.todoapp.addtasks.domain.DeleteTaskUseCase
import com.cursokotlin.todoapp.addtasks.domain.GetTasksUseCase
import com.cursokotlin.todoapp.addtasks.domain.UpdateTaskUseCase
import com.cursokotlin.todoapp.addtasks.ui.TasksUiState.Error
import com.cursokotlin.todoapp.addtasks.ui.TasksUiState.Loading
import com.cursokotlin.todoapp.addtasks.ui.TasksUiState.Success
import com.cursokotlin.todoapp.addtasks.ui.model.TaskModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    getTasksUseCase: GetTasksUseCase
):ViewModel() {

    val uiState:StateFlow<TasksUiState> = getTasksUseCase().map(::Success)
        .catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog:LiveData<Boolean> = _showDialog

    private val _taskSelected = MutableLiveData<TaskModel?>()
    val taskSelected:LiveData<TaskModel?> = _taskSelected

    private val _showConfirmDeleteTask = MutableLiveData<Boolean>()
    val showConfirmDeleteTask:LiveData<Boolean> = _showConfirmDeleteTask

    fun onConfirmDeleteDialogClose() {
        _showConfirmDeleteTask.value = false
    }

    fun onShowConfirmDeleteDialogClick(task: TaskModel) {
        _showConfirmDeleteTask.value = true
        _taskSelected.value = task
    }

    fun onDialogClose() {
        _showDialog.value = false
    }

    fun onTasksCreated(taskModel: TaskModel) {
        _showDialog.value = false
        viewModelScope.launch {
            addTaskUseCase(taskModel)
        }
    }

    fun onShowDialogClick() {
        _showDialog.value = true
    }

    fun onCheckBoxSelected(taskModel: TaskModel) {
        viewModelScope.launch {
            updateTaskUseCase(taskModel.copy(selected = !taskModel.selected))
        }
    }

    fun onItemRemove(taskModel: TaskModel) {
        _showConfirmDeleteTask.value = false
        viewModelScope.launch {
            deleteTaskUseCase(taskModel)
        }
    }

}