package turlapati.phani.to_dolist

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import java.util.Locale

class AddTaskActivity : AppCompatActivity() {
    private var selectedDateTextView: TextView? = null
    private var selectedTimeTextView: TextView? = null
    private var taskEditText: EditText? = null
    private var categorySpinner: Spinner? = null
    private var prioritySpinner: Spinner? = null
    private var notesEditText: EditText? = null

    private var dbHelper: TaskDBHelper? = null

    private var calendar: Calendar? = null
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var mHour = 0
    private var mMinute = 0
    val localCalendar = this.calendar ?: Calendar.getInstance() //
    val selectedDate = localCalendar.time //


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        selectedDateTextView = findViewById(R.id.selected_date_text_view)
        selectedTimeTextView = findViewById(R.id.selected_time_text_view)
        taskEditText = findViewById(R.id.task_edit_text)
        categorySpinner = findViewById(R.id.category_spinner)
        prioritySpinner = findViewById(R.id.priority_spinner)
        notesEditText = findViewById(R.id.notes_edit_text)
        val selectDateButton = findViewById<Button>(R.id.button_select_due_date)
        val selectTimeButton = findViewById<Button>(R.id.button_select_due_time)
        val addTaskButton = findViewById<Button>(R.id.button_add_task)

        calendar = Calendar.getInstance()
        mYear = localCalendar.get(Calendar.YEAR)
        mMonth = localCalendar.get(Calendar.MONTH)
        mDay = localCalendar.get(Calendar.DAY_OF_MONTH)
        mHour = localCalendar.get(Calendar.HOUR_OF_DAY)
        mMinute = localCalendar.get(Calendar.MINUTE)
        val categoryAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.categories_array,
            android.R.layout.simple_spinner_item
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner?.adapter = categoryAdapter

        val priorityAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.priorities_array,
            android.R.layout.simple_spinner_item
        )
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        prioritySpinner?.adapter = priorityAdapter

        updateDateAndTimeTextViews()

        selectDateButton.setOnClickListener { showDatePickerDialog() }

        selectTimeButton.setOnClickListener { showTimePickerDialog() }

        addTaskButton.setOnClickListener {
            addTask()
            val intent = Intent(this@AddTaskActivity, MainActivity::class.java)
            startActivity(intent)
        }

        dbHelper = TaskDBHelper(this)
    }

    private fun updateDateAndTimeTextViews() {
        val dateString = String.format(Locale.getDefault(), "%02d/%02d/%d", mDay, mMonth + 1, mYear)
        selectedDateTextView!!.text = dateString

        val timeString = String.format(Locale.getDefault(), "%02d:%02d", mHour, mMinute)
        selectedTimeTextView!!.text = timeString
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            { view, year, month, dayOfMonth ->
                mYear = year
                mMonth = month
                mDay = dayOfMonth
                updateDateAndTimeTextViews()
            },
            mYear, mMonth, mDay
        )
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(
            this,
            { view, hourOfDay, minute ->
                mHour = hourOfDay
                mMinute = minute
                updateDateAndTimeTextViews()
            },
            mHour, mMinute, true
        )
        timePickerDialog.show()
    }

    private fun addTask() {
        val task = taskEditText!!.text.toString().trim { it <= ' ' }
        val category = categorySpinner!!.selectedItem.toString()
        val priority = prioritySpinner!!.selectedItem.toString()
        val notes = notesEditText!!.text.toString().trim { it <= ' ' }
        val dueDate = selectedDateTextView!!.text.toString().trim { it <= ' ' }
        val dueTime = selectedTimeTextView!!.text.toString().trim { it <= ' ' }

        val db = dbHelper!!.writableDatabase
        val values = ContentValues()
        values.put(TaskContract.TaskEntry.COLUMN_TASK, task)
        values.put(TaskContract.TaskEntry.COLUMN_CATEGORY, category)
        values.put(TaskContract.TaskEntry.COLUMN_PRIORITY, priority)
        values.put(TaskContract.TaskEntry.COLUMN_NOTES, notes)
        values.put(TaskContract.TaskEntry.COLUMN_DUE_DATE, dueDate)
        values.put(TaskContract.TaskEntry.COLUMN_DUE_TIME, dueTime)
        values.put(TaskContract.TaskEntry.COLUMN_COMPLETED, 0)

        val newRowId = db.insert(TaskContract.TaskEntry.TABLE_NAME, null, values)
        db.close()
        if (newRowId == -1L) {
            Toast.makeText(this, "Failed to add task", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show()
        }
    }
}


