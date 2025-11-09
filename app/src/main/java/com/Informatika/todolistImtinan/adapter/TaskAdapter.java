package com.Informatika.todolistImtinan.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Informatika.todolistImtinan.R;
import com.Informatika.todolistImtinan.model.Task;
import com.Informatika.todolistImtinan.ui.AddTaskActivity;
import com.Informatika.todolistImtinan.ui.MainActivity;
//import com.dhendra.todoappstorage.ui.AddTaskActivity;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final Context context;
    private final List<Task> tasks;

    public TaskAdapter(Context context, List<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.title.setText(task.title);
        holder.desc.setText(task.description);
        holder.checkDone.setChecked(task.isDone);

        Date date = new Date(task.timeMillis);
        holder.time.setText(DateFormat.getDateTimeInstance().format(date));

        if (task.isDone) {
            holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.title.setPaintFlags(holder.title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Klik item untuk edit (nanti aktif kalau sudah ada AddTaskActivity)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddTaskActivity.class);
            intent.putExtra("edit_task_id", task.id);
            intent.putExtra("title", task.title);
            intent.putExtra("desc", task.description);
            intent.putExtra("time", task.timeMillis);
            ((MainActivity) context).startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    // --- Tambahan helper untuk swipe delete ---
    public Task getTaskAt(int position) {
        return tasks.get(position);
    }

    public void removeAt(int position) {
        tasks.remove(position);
        notifyItemRemoved(position);
    }

    // --- ViewHolder class ---
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc, time;
        CheckBox checkDone;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            desc = itemView.findViewById(R.id.textDesc);
            time = itemView.findViewById(R.id.textTime);
            checkDone = itemView.findViewById(R.id.checkDone);
        }
    }


}
