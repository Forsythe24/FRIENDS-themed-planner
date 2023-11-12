package com.itis.friendsplanner


object WeekTasksRepository {
    private val weekTasks = mutableListOf<MutableList<Task>>()

    private var idCounter = 0

    init {
        val localList = listOf(
            mutableListOf<Task>(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf()
        )
        weekTasks.addAll(localList)
    }

    fun addTask(day: Int, task: Task): Task {
        weekTasks[day].add(task)
        return task
    }

    fun addTaskWithAssignment(day: Int, task: Task): Task {
        task.id = idCounter
        idCounter++

        task.priority = weekTasks[day].size + 1

        weekTasks[day].add(task)

        return task
    }

    fun getDayTaskList(day: Int): MutableList<Task> {
        return weekTasks[day]
    }

    fun getTaskById(id: Int): Task? {
        weekTasks.forEach {list ->
            list.forEach {task ->
                if (task.id == id) {
                    return task
                }
            }
        }
        return null
    }

    fun swapPriorities(dayNumber: Int, fromPosition: Int, toPosition: Int) {
        val taskList = weekTasks[dayNumber]
        taskList[toPosition].priority -= fromPosition - toPosition
        if (fromPosition < toPosition) {
            for (index in fromPosition until toPosition) {
                taskList[index].priority --
            }
        } else {
            for (index in toPosition + 1 .. fromPosition) {
                taskList[index].priority ++
            }
        }
    }

    fun removeTask(dayNumber: Int, position: Int): Task {
        val taskList = weekTasks[dayNumber]

        val task = taskList.removeAt(position)

        for (index in position until taskList.size) {
            taskList[index].priority--
        }
        println(task.id)
        return task
    }

    fun changeTaskTime(dayNumber: Int, position: Int, newHour: Int, newMinute: Int) {
        weekTasks[dayNumber][position].hour = newHour
        weekTasks[dayNumber][position].minute = newMinute
    }

    fun changeTaskDescription(dayNumber: Int, position: Int, newDescription: String) {
        weekTasks[dayNumber][position].description = newDescription
    }

    fun getWeekTaskList(): MutableList<MutableList<Task>> = weekTasks

    fun sort() {
        weekTasks.forEach { dayTaskList ->
            dayTaskList.sort()
            println(dayTaskList)
        }
    }
}
