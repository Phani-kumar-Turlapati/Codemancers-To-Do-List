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

class editTask : AppCompatActivity() { // ✅ Fixed class declaration
    private var selectedDateTextView: TextView? = null
    private var selectedTimeTextView: TextView? = null
    private var categorySpinner: Spinner? = null
    private var prioritySpinner: Spinner? = null
    private var notesEditText: EditText? = null
    private var textViewTask: TextView? = null
    private var dbHelper: TaskDBHelper? = null

    private var calendar: Calendar? = null
    private var task: String? = null

    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var mHour = 0
    private var mMinute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        // ✅ Store intent in a local variable to avoid smart cast error
        val localIntent = intent
        task = localIntent.getStringExtra("task")

        textViewTask = findViewById(R.id.text_view_task)
        textViewTask?.text = task

        selectedDateTextView = findViewById(R.id.selected_date_text_view)
        selectedTimeTextView = findViewById(R.id.selected_time_text_view)
        categorySpinner = findViewById(R.id.category_spinner)
        prioritySpinner = findViewById(R.id.priority_spinner)
        notesEditText = findViewById(R.id.notes_edit_text)

        val selectDateButton = findViewById<Button>(R.id.button_select_due_date)
        val selectTimeButton = findViewById<Button>(R.id.button_select_due_time)
        val editTaskButton = findViewById<Button>(R.id.button_add_task) // ✅ Renamed from "addTaskButton"

        calendar = Calendar.getInstance()
        mYear = calendar?.get(Calendar.YEAR) ?: 0
        mMonth = calendar?.get(Calendar.MONTH) ?: 0
        mDay = calendar?.get(Calendar.DAY_OF_MONTH) ?: 0
        mHour = calendar?.get(Calendar.HOUR_OF_DAY) ?: 0
        mMinute = calendar?.get(Calendar.MINUTE) ?: 0

        val categoryAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.categories_array,
            android.R.layout.simple_spinner_item
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner?.adapter = categoryAdapter // ✅ Safe usage

        val priorityAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.priorities_array,
            android.R.layout.simple_spinner_item
        )
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        prioritySpinner?.adapter = priorityAdapter // ✅ Safe usage

        updateDateAndTimeTextViews()

        selectDateButton.setOnClickListener { showDatePickerDialog() }
        selectTimeButton.setOnClickListener { showTimePickerDialog() }

        editTaskButton.setOnClickListener {
            editTaskInDatabase(task) // ✅ Fixed function call
            Toast.makeText(this@editTask, "Task edited", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@editTask, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        dbHelper = TaskDBHelper(this)
    }

    private fun updateDateAndTimeTextViews() {
        val dateString = String.format(Locale.getDefault(), "%02d/%02d/%d", mDay, mMonth + 1, mYear)
        selectedDateTextView?.text = dateString

        val timeString = String.format(Locale.getDefault(), "%02d:%02d", mHour, mMinute)
        selectedTimeTextView?.text = timeString
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
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
            { _, hourOfDay, minute ->
                mHour = hourOfDay
                mMinute = minute
                updateDateAndTimeTextViews()
            },
            mHour, mMinute, true
        )
        timePickerDialog.show()
    }

    // ✅ Fixed Function: Edit Task in Database
    private fun editTaskInDatabase(task: String?) {
        if (task == null) return // ✅ Prevent null issues

        val category = categorySpinner?.selectedItem?.toString() ?: ""
        val priority = prioritySpinner?.selectedItem?.toString() ?: ""
        val notes = notesEditText?.text?.toString()?.trim() ?: ""
        val dueDate = selectedDateTextView?.text?.toString()?.trim() ?: ""
        val dueTime = selectedTimeTextView?.text?.toString()?.trim() ?: ""

        dbHelper?.let { dbHelper ->
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(TaskContract.TaskEntry.COLUMN_TASK, task)
                put(TaskContract.TaskEntry.COLUMN_CATEGORY, category)
                put(TaskContract.TaskEntry.COLUMN_PRIORITY, priority)
                put(TaskContract.TaskEntry.COLUMN_NOTES, notes)
                put(TaskContract.TaskEntry.COLUMN_DUE_DATE, dueDate)
                put(TaskContract.TaskEntry.COLUMN_DUE_TIME, dueTime)
                put(TaskContract.TaskEntry.COLUMN_COMPLETED, 0)
            }

            db.update(
                TaskContract.TaskEntry.TABLE_NAME,
                values,
                "${TaskContract.TaskEntry.COLUMN_TASK} = ?",
                arrayOf(task)
            )
            db.close()
        }
    }
}
