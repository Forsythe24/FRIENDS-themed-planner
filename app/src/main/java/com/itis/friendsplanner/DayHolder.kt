package com.itis.friendsplanner

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.itis.friendsplanner.databinding.TaskItemBinding
import java.util.Calendar

class DayHolder(
    private val viewBinding: TaskItemBinding,
    private val dayNumber: Int,
    private val context: Context
    ) : RecyclerView.ViewHolder(viewBinding.root), TimePickerDialog.OnTimeSetListener {

    private var minute = 0
    private var hour = 0

    private var savedMinute = 0
    private var savedHour = 0

    private lateinit var adapter: DayAdapter

    private lateinit var newTaskDescription: String
    private val pref = context.getSharedPreferences("TASKS_PREF", Context.MODE_PRIVATE)
    private val editor = pref.edit()

    private var taskSet = pref.getStringSet("tasks", HashSet<String>()) as HashSet<String>

    private val converter = TaskInfoConverter()

    fun bindItem(item: Task, adapter: DayAdapter) {
        viewBinding.tvTask.text = item.description
        viewBinding.tvTime.text = getFormattedTime(item.minute, item.hour)

        viewBinding.root.setOnClickListener {
            showTaskSettingDialog()
        }

        viewBinding.ivTime.setOnClickListener {
            getTimeCalendar()
            showTimeSettingDialog()
        }

        this.adapter = adapter
    }

    private fun getTimeCalendar() {
        val cal = Calendar.getInstance()
        hour = cal.get(Calendar.HOUR)
        hour = cal.get(Calendar.MINUTE)

    }

    private fun showTimeSettingDialog() {
        TimePickerDialog(context,this, hour, minute, true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute

        WeekTasksRepository.changeTaskTime(dayNumber, adapterPosition, savedHour, savedMinute)

        taskSet = converter.convertObjectListToStringSet(WeekTasksRepository.getWeekTaskList())

        updateSharedPref()

        adapter.notifyItemChanged(adapterPosition)
    }

    private fun updateSharedPref() {
        editor.clear()
        editor.putStringSet("tasks", taskSet)
        editor.apply()
    }

    private fun showTaskSettingDialog() {
        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog.setCancelable(true)

        dialog.setContentView(R.layout.task_adding_dialog)

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

        val submitTaskDescriptionChangesButton = dialog.findViewById<MaterialButton>(R.id.btn_set_time)
        val newTaskDescriptionET = dialog.findViewById<EditText>(R.id.et_task_description)

        viewBinding.apply {
            newTaskDescriptionET.setText(tvTask.text)

            newTaskDescription = tvTask.text as String
        }
        newTaskDescriptionET.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                newTaskDescription = p0.toString()
            }

        })

        submitTaskDescriptionChangesButton.text = submitChangesBtnText

        submitTaskDescriptionChangesButton.setOnClickListener {
            if (newTaskDescription.isNotEmpty()) {
                dialog.hide()

                WeekTasksRepository.changeTaskDescription(dayNumber, adapterPosition, newTaskDescription)
                adapter.notifyItemChanged(adapterPosition)

                taskSet = converter.convertObjectListToStringSet(WeekTasksRepository.getWeekTaskList())

                updateSharedPref()
            } else {
                Toast.makeText(context, R.string.empty_task_warning, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getFormattedTime(minute: Int, hour: Int): String {
        if (minute == 0) {
            return "${hour}:0${minute}"
        }

        return "${hour}:${minute}"
    }

    companion object {
        private const val submitChangesBtnText = "SUBMIT"
    }
}
