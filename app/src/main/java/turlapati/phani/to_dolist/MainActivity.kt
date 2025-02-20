package turlapati.phani.to_dolist

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private val taskScrollView: ScrollView? = null
    private var taskAdapter: ArrayAdapter<String>? = null
    private var dbHelper: TaskDBHelper? = null
    private var taskData: MutableList<Data>? = null
    var fabAddTask: FloatingActionButton? = null
    var recyclerView: RecyclerView? = null
    private var adapter: TaskAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskData = mutableListOf() // ✅ Always initialize taskData
        taskAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        fabAddTask = findViewById(R.id.fab_add_task)
        dbHelper = TaskDBHelper(this)

        loadTasksFromSQLite(taskData!!) // ✅ Load tasks safely
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter(this, taskData!!) // ✅ No more smart cast issues
        recyclerView?.adapter = adapter

        adapter!!.setOnItemClickListener(object : TaskAdapter.OnItemClickListener {
            override fun onEditClick(position: Int) {
                val intent = Intent(this@MainActivity, editTask::class.java)
                intent.putExtra("task", taskData!![position].name)
                startActivity(intent)
            }

            override fun onDeleteClick(position: Int) {
                markTaskAsComplete(position)
                taskData!!.removeAt(position)
                Toast.makeText(this@MainActivity, "Task Deleted", Toast.LENGTH_SHORT).show()
                adapter!!.notifyItemRemoved(position)
            }

            override fun onCheckboxClick(position: Int) {
                markTaskAsComplete(position)
                taskData!!.removeAt(position)
                Toast.makeText(this@MainActivity, "Task Completed", Toast.LENGTH_SHORT).show()
                adapter!!.notifyItemRemoved(position)
            }
        })

        fabAddTask?.setOnClickListener {
            val intent = Intent(this@MainActivity, AddTaskActivity::class.java)
            startActivity(intent)
        }
    }

    fun loadTasksFromSQLite(data: MutableList<Data>) {
        // Assuming you have a SQLiteOpenHelper instance named dbHelper
        val db = dbHelper!!.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM " + TaskContract.TaskEntry.TABLE_NAME, null)

        while (cursor.moveToNext()) {
            @SuppressLint("Range") val taskName =
                cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK))
            @SuppressLint("Range") val taskDate =
                cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DUE_DATE))
            @SuppressLint("Range") val taskTime =
                cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DUE_TIME))
            @SuppressLint("Range") val category =
                cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_CATEGORY))
            @SuppressLint("Range") val priority =
                cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_PRIORITY))
            @SuppressLint("Range") val notes =
                cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_NOTES))

            data.add(Data(taskName, taskDate, taskTime, category, priority, notes))
            //Toast.makeText(this, "added", Toast.LENGTH_SHORT).show();
        }

        cursor.close()
        db.close()
    }

    fun markTaskAsComplete(position: Int) {
        val task = taskData!![position].name
        //Toast.makeText(this, task, Toast.LENGTH_SHORT).show();
        val db = dbHelper!!.writableDatabase
        val values = ContentValues()
        values.put(TaskContract.TaskEntry.COLUMN_COMPLETED, 1)
        db.delete(
            TaskContract.TaskEntry.TABLE_NAME,
            TaskContract.TaskEntry.COLUMN_TASK + " = ?", arrayOf(task)
        )
    }

    override fun onDestroy() {
        dbHelper!!.close()
        super.onDestroy()
    }

    inner class Data internal constructor(
        @JvmField val name: String,
        @JvmField val date: String,
        @JvmField val time: String,
        @JvmField val category: String,
        @JvmField val priority: String,
        @JvmField val notes: String
    ) {
        @JvmField
        var isCompleted: Boolean = false
    }
}