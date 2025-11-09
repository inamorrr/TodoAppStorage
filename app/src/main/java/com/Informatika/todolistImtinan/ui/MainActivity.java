package com.Informatika.todolistImtinan.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.Informatika.todolistImtinan.R;
import com.Informatika.todolistImtinan.adapter.TaskAdapter;
import com.Informatika.todolistImtinan.model.Task;
import com.Informatika.todolistImtinan.model.TaskDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private TaskDatabase db;
    private FloatingActionButton btnAdd;
    Button btnAll, btnToday;
    private static final int ADD_TASK_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.btnAdd);
        btnAll = findViewById(R.id.btnAll);
        btnToday = findViewById(R.id.btnToday);

        db = TaskDatabase.getInstance(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadTasks();
        btnAll.setOnClickListener(v -> loadTasks()); // tampil semua
        btnToday.setOnClickListener(v -> loadTodayTasks());

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });


        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Task taskToDelete = adapter.getTaskAt(position);
                if (direction == ItemTouchHelper.LEFT) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Hapus Tugas?")
                            .setMessage("Apakah kamu yakin ingin menghapus \"" + taskToDelete.title + "\"?")
                            .setPositiveButton("Ya", (dialog, which) -> {
                                db.taskDao().delete(taskToDelete);
                                adapter.removeAt(position);
                                Toast.makeText(MainActivity.this, "Tugas dihapus", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Batal", (dialog, which) -> {
                                adapter.notifyItemChanged(position);
                            })
                            .show();
                }
//                Task taskToDelete = adapter.getTaskAt(position);
//                db.taskDao().delete(taskToDelete);
//                adapter.removeAt(position);
//                Toast.makeText(MainActivity.this, "Tugas dihapus", Toast.LENGTH_SHORT).show();
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    private void loadTasks() {
        List<Task> taskList = db.taskDao().getAll();
        adapter = new TaskAdapter(this, taskList);
        recyclerView.setAdapter(adapter);
    }

    private void loadTodayTasks() {
        List<Task> todayList = db.taskDao().getTodayTasks();
        adapter = new TaskAdapter(this, todayList);
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TASK_REQUEST && resultCode == RESULT_OK) {
            loadTasks(); // refresh list
        }
    }
}