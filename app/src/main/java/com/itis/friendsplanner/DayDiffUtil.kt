package com.itis.friendsplanner

import androidx.recyclerview.widget.DiffUtil

class TaskDiffUtil(
    private val oldItemsList: List<Task>,
    private val newItemsList: List<Task>,
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldItemsList.size

    override fun getNewListSize(): Int = newItemsList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItemsList[oldItemPosition]
        val newItem = newItemsList[newItemPosition]
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItemsList[oldItemPosition]
        val newItem = newItemsList[newItemPosition]

        return (oldItem.description == newItem.description) &&
                (oldItem.hour == newItem.hour) &&
                (oldItem.minute == newItem.minute)
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        if (oldItemPosition >= oldItemsList.size) {
            return null
        }
        val oldItem = oldItemsList[oldItemPosition]
        val newItem = newItemsList[newItemPosition]

        return if (oldItem.priority != newItem.priority) {
            newItem.priority
        } else {
            super.getChangePayload(oldItemPosition, newItemPosition)
        }
    }
}
