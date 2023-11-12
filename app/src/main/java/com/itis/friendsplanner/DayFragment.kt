package com.itis.friendsplanner

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.button.MaterialButton
import com.itis.friendsplanner.databinding.FragmentDayBinding
import java.util.Calendar
import java.util.Collections


class DayFragment : Fragment(R.layout.fragment_day), TimePickerDialog.OnTimeSetListener {

    private val viewBinding: FragmentDayBinding by viewBinding(FragmentDayBinding::bind)

    private var adapter: DayAdapter? = null
    private lateinit var items: ArrayList<Task>

    private var dayNumber: Int = 0

    private var minute = 0
    private var hour = 0

    private var savedMinute = 0
    private var savedHour = 0

    private lateinit var taskDescription: String

    private lateinit var pref: SharedPreferences
    private lateinit var editor: Editor

    private var taskSet = HashSet<String>()

    private val converter = TaskInfoConverter()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    @SuppressLint("CommitPrefEdits")
    private fun init() {
        pref = requireActivity().getSharedPreferences("TASKS_PREF", Context.MODE_PRIVATE)
        editor = pref.edit()

        taskSet = pref.getStringSet("tasks", HashSet<String>()) as HashSet<String>

        if (isFirstInit) {
            extractSharedPreferences()
            isFirstInit = false
        }

        with (viewBinding) {
            val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            rvTaskList.layoutManager = layoutManager

            dayNumber = requireArguments().getInt(ParamsKeys.DAY_NUMBER)

            viewBinding.root.setBackgroundResource(listOfFriendsPictures[dayNumber])

            items = WeekTasksRepository.getDayTaskList(dayNumber) as ArrayList<Task>

            adapter = DayAdapter(dayNumber)
            adapter!!.setItems(items)
            rvTaskList.adapter = adapter

            val offset = 18.getValueInPx(resources.displayMetrics)
            rvTaskList.addItemDecoration(SimpleDividerItemDecoration(offset, requireContext()))


            btnAddTask.setOnClickListener {
                showTaskSettingDialog()
            }


            val itemTouchHelperCallback =
                object :
                    SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        val fromPosition = viewHolder.adapterPosition
                        val toPosition = target.adapterPosition
                        
                        adapter!!.notifyItemMoved(fromPosition, toPosition)

                        return false
                    }

                    override fun onMoved(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        fromPos: Int,
                        target: RecyclerView.ViewHolder,
                        toPos: Int,
                        x: Int,
                        y: Int
                    ) {
                        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
                        
                        Collections.swap(items, fromPos, toPos)

                        WeekTasksRepository.swapPriorities(dayNumber, fromPos, toPos)
                        taskSet = converter.convertObjectListToStringSet(WeekTasksRepository.getWeekTaskList())

                        updateSharedPref()
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.adapterPosition

                        val task = WeekTasksRepository.removeTask(dayNumber, position)

                        items.remove(task)



                        taskSet = converter.convertObjectListToStringSet(WeekTasksRepository.getWeekTaskList())

                        updateSharedPref()

                        adapter!!.setItems(items)

                    }
                }

            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(rvTaskList)
        }
    }

    private fun extractSharedPreferences() {
        println(taskSet)

        taskSet.toList().stream().
        map { jsonTask ->
            converter.convertString(jsonTask)
        }?.
        forEach { task ->
            WeekTasksRepository.addTask(task.dayNumber, task)
        }

        WeekTasksRepository.sort()
    }



    private fun showTaskSettingDialog() {
        val dialog = Dialog(requireContext())

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog.setCancelable(true)

        dialog.setContentView(R.layout.task_adding_dialog)

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

        val setTimeButton = dialog.findViewById<MaterialButton>(R.id.btn_set_time)
        val taskDescriptionET = dialog.findViewById<EditText>(R.id.et_task_description)

        taskDescription = taskDescriptionET.text.toString()

        taskDescriptionET.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                taskDescription = p0.toString()
            }

        })


        setTimeButton.setOnClickListener{
            if (taskDescription.isNotEmpty()) {
                dialog.hide()
                getTimeCalendar()
                showTimeSettingDialog()
            } else {
                Toast.makeText(requireContext(), R.string.empty_task_warning, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getTimeCalendar() {
        val cal = Calendar.getInstance()
        hour = cal.get(Calendar.HOUR)
        hour = cal.get(Calendar.MINUTE)
    }

    private fun showTimeSettingDialog() {
        TimePickerDialog(requireContext(), this, hour, minute, true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute

        val task = WeekTasksRepository.addTaskWithAssignment(dayNumber, Task(taskDescription, savedHour, savedMinute, dayNumber = dayNumber))
        taskSet.add(converter.convertObject(task))

        updateSharedPref()

        adapter!!.setItems(WeekTasksRepository.getDayTaskList(dayNumber))
    }

    private fun updateSharedPref() {
        editor.clear()
        editor.putStringSet("tasks", taskSet)
        editor.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    companion object {
        fun newInstance(day: Int) = DayFragment().apply{
            arguments = bundleOf(ParamsKeys.DAY_NUMBER to day)
        }

        private var isFirstInit = true

        private val listOfFriendsPictures = listOf(R.drawable.monica, R.drawable.ross, R.drawable.joey, R.drawable.phoebe, R.drawable.rachel, R.drawable.chandler, R.drawable.central_perk)
    }
}
