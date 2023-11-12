package com.itis.friendsplanner


data class Task(
    var description: String,
    var hour: Int,
    var minute: Int,
    var id: Int = 0,
    var priority: Int = 1,
    val dayNumber: Int
) : Comparable<Task> {
    override fun compareTo(other: Task): Int {
        return this.priority - other.priority
    }
}

