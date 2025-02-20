package turlapati.phani.to_dolist

object TaskContract {
    object TaskEntry {
        const val TABLE_NAME = "tasks"
        const val _ID = "_id" // ✅ Define _ID explicitly as "_id"
        const val COLUMN_TASK = "task"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_PRIORITY = "priority"
        const val COLUMN_NOTES = "notes"
        const val COLUMN_DUE_DATE = "due_date"
        const val COLUMN_DUE_TIME = "due_time"
        const val COLUMN_COMPLETED = "completed"
    }
}


