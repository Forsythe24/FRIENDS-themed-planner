package com.itis.friendsplanner

class TaskPriorityComparator : Comparator<Task> {
    override fun compare(task1: Task?, task2: Task?): Int {
        return task1!!.priority - task2!!.priority
    }
}
