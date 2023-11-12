package com.itis.friendsplanner

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.itis.friendsplanner.databinding.TaskItemBinding

class DayAdapter(
    private val day: Int
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemsList = mutableListOf<Task>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DayHolder(TaskItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false),
            dayNumber = day,
            context = parent.context
        )
    }

    override fun getItemCount(): Int {
        return WeekTasksRepository.getDayTaskList(day).size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DayHolder).bindItem(itemsList[position], this)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(list: List<Task>) {
        val diff = TaskDiffUtil(oldItemsList = itemsList, newItemsList = list)
        val diffResult = DiffUtil.calculateDiff(diff)
        itemsList.clear()
        itemsList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

}
