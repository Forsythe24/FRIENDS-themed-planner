package com.itis.friendsplanner

import org.json.JSONObject
import kotlin.math.min

class TaskInfoConverter {
    fun convertObject(task: Task): String {
        val json = JSONObject()

        json.put("id", task.id)
        json.put("description", task.description)
        json.put("dayNumber", task.dayNumber)
        json.put("hour", task.hour)
        json.put("minute", task.minute)
        json.put("priority", task.priority)
        return json.toString()
    }

    fun convertString(string: String): Task {
        val json = JSONObject(string)

        val id = json.getInt("id")
        val description = json.getString("description")
        val dayNumber = json.getInt("dayNumber")
        val hour = json.getInt("hour")
        val minute = json.getInt("minute")
        val priority = json.getInt("priority")

        return Task(description, hour, minute, id, priority, dayNumber)
    }

    fun convertObjectListToStringSet(list: List<List<Task>>): HashSet<String> {
        val stringSet: HashSet<String> = HashSet()

        list.forEach { innerList ->
            innerList.forEach { task ->
                stringSet.add(convertObject(task))
            }
        }

        return stringSet
    }

}
