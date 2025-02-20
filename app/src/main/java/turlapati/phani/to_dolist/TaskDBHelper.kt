package turlapati.phani.to_dolist

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDBHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TASK_TABLE)
    }

    val allTasks: Cursor
        get() {
            val db = this.readableDatabase
            return db.query(
                TaskContract.TaskEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
            )
        }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME)
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "tasklist.db"
        private const val DATABASE_VERSION = 1

        private val SQL_CREATE_TASK_TABLE =
            "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME + " (" +
                    TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TaskContract.TaskEntry.COLUMN_TASK + " TEXT NOT NULL, " +
                    TaskContract.TaskEntry.COLUMN_CATEGORY + " TEXT, " +
                    TaskContract.TaskEntry.COLUMN_PRIORITY + " TEXT, " +
                    TaskContract.TaskEntry.COLUMN_NOTES + " TEXT, " +
                    TaskContract.TaskEntry.COLUMN_DUE_DATE + " TEXT, " +
                    TaskContract.TaskEntry.COLUMN_DUE_TIME + " TEXT, " +
                    TaskContract.TaskEntry.COLUMN_COMPLETED + " INTEGER DEFAULT 0);"
    }
}
