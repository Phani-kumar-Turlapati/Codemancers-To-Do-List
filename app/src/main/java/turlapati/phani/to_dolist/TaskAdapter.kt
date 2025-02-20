package turlapati.phani.to_dolist

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import turlapati.phani.to_dolist.TaskAdapter.TaskViewHolder

class TaskAdapter(private val mContext: Context, private val mTaskList: List<MainActivity.Data>) :
    RecyclerView.Adapter<TaskViewHolder>() {
    private var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onEditClick(position: Int)
        fun onDeleteClick(position: Int)
        fun onCheckboxClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.task_card_layout, parent, false)
        return TaskViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentItem = mTaskList[position]

        holder.mTextViewName.text = currentItem.name
        holder.mTextViewDate.text = "Date: " + currentItem.date
        holder.mTextViewTime.text = "Time: " + currentItem.time
        holder.text_category.text = "Category: " + currentItem.category
        holder.text_priority.text = "Priority: " + currentItem.priority
        holder.text_notes.text = "Note: " + currentItem.notes

        // ✅ Remove previous listener to avoid multiple triggers
        holder.mCheckBox.setOnCheckedChangeListener(null)

        // ✅ Set checkbox state from the data model
        holder.mCheckBox.isChecked = currentItem.isCompleted

        // ✅ Mark as Done Confirmation Dialog
        holder.mCheckBox.setOnClickListener { v: View? ->
            AlertDialog.Builder(mContext)
                .setTitle("Mark as Done")
                .setMessage("Are you sure you want to mark this task as done?")
                .setPositiveButton("Yes") { dialog: DialogInterface?, which: Int ->
                    currentItem.isCompleted = true // ✅ Update state
                    notifyItemChanged(position) // ✅ Refresh UI
                }
                .setNegativeButton(
                    "No"
                ) { dialog: DialogInterface?, which: Int ->
                    holder.mCheckBox.isChecked =
                        false // ✅ Uncheck if canceled
                }
                .show()
        }
    }

    override fun getItemCount(): Int {
        return mTaskList.size
    }

    class TaskViewHolder(itemView: View, listener: OnItemClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        var mTextViewName: TextView = itemView.findViewById(R.id.text_name)
        var mTextViewDate: TextView = itemView.findViewById(R.id.text_date)
        var mTextViewTime: TextView = itemView.findViewById(R.id.text_time)
        var text_category: TextView = itemView.findViewById(R.id.text_category)
        var text_priority: TextView = itemView.findViewById(R.id.text_priority)
        var text_notes: TextView = itemView.findViewById(R.id.text_notes)
        var mCheckBox: CheckBox = itemView.findViewById(R.id.check_box)
        var mButtonEdit: Button = itemView.findViewById(R.id.btn_edit)
        var mButtonDelete: Button = itemView.findViewById(R.id.btn_delete)

        init {
            mButtonEdit.setOnClickListener { v: View? ->
                if (listener != null) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEditClick(position)
                    }
                }
            }

            mButtonDelete.setOnClickListener { v: View? ->
                AlertDialog.Builder(itemView.context)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton(
                        "Yes"
                    ) { dialog: DialogInterface?, which: Int ->
                        if (listener != null) {
                            val position = adapterPosition
                            if (position != RecyclerView.NO_POSITION) {
                                listener.onDeleteClick(position)
                            }
                        }
                    }
                    .setNegativeButton(
                        "No"
                    ) { dialog: DialogInterface, which: Int ->
                        dialog.dismiss() // ✅ Do nothing if "No" is clicked
                    }
                    .show()
            }

            mCheckBox.setOnClickListener { v: View? ->
                if (listener != null) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onCheckboxClick(position)
                    }
                }
            }
        }
    }
}
