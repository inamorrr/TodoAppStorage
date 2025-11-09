package com.Informatika.todolistImtinan.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Informatika.todolistImtinan.R;
import com.Informatika.todolistImtinan.model.Task;
import com.Informatika.todolistImtinan.model.TaskDatabase;
import com.Informatika.todolistImtinan.receiver.AlarmReceiver;

import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    private EditText edtTitle, edtDesc, edtDate;
    private Button btnSave;
    private long timeMillis = 0;
    private TaskDatabase db;
    private int editTaskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        edtTitle = findViewById(R.id.edtTitle);
        edtDesc = findViewById(R.id.edtDesc);
        edtDate = findViewById(R.id.edtDate);
        btnSave = findViewById(R.id.btnSave);

        db = TaskDatabase.getInstance(this);
        edtDate.setOnClickListener(v -> showDateTimePicker());
        btnSave.setOnClickListener(v -> saveTask());

        Intent intent = getIntent();
        if (intent.hasExtra("edit_task_id")) {
            editTaskId = intent.getIntExtra("edit_task_id", -1);
            edtTitle.setText(intent.getStringExtra("title"));
            edtDesc.setText(intent.getStringExtra("desc"));
            long time = intent.getLongExtra("time", 0);
            if (time > 0) {
                edtDate.setText(android.text.format.DateFormat.format("dd/MM/yyyy HH:mm", time));
                timeMillis = time;
            }
            btnSave.setText("Update Tugas");
        }


    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {

                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (timeView, hour, minute) -> {
                                calendar.set(year, month, dayOfMonth, hour, minute, 0);
                                timeMillis = calendar.getTimeInMillis();
                                edtDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year + " " + hour + ":" + String.format("%02d", minute));
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true);
                    timePickerDialog.show();

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void saveTask() {
        String title = edtTitle.getText().toString().trim();
        String desc = edtDesc.getText().toString().trim();

        if (title.isEmpty() || timeMillis == 0) {
            Toast.makeText(this, "Isi judul dan waktu!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editTaskId != -1) {
            // Update task
            Task task = new Task(title, desc, timeMillis);
            task.id = editTaskId;
            db.taskDao().delete(db.taskDao().getById(editTaskId)); // hapus lama
            db.taskDao().insert(task);
            setAlarm(task);
            Toast.makeText(this, "Tugas diperbarui!", Toast.LENGTH_SHORT).show();
        } else {
            // Task baru
            Task task = new Task(title, desc, timeMillis);
            db.taskDao().insert(task);
            setAlarm(task);
            Toast.makeText(this, "Tugas disimpan!", Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(this, "Tugas disimpan!", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void setAlarm(Task task) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("title", task.title);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, task.timeMillis, pendingIntent);
    }
}