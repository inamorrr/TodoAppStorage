package com.Informatika.todolistImtinan.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "time")
    public long timeMillis; // waktu alarm (epoch)

    @ColumnInfo(name = "is_done")
    public boolean isDone;

    public Task(String title, String description, long timeMillis) {
        this.title = title;
        this.description = description;
        this.timeMillis = timeMillis;
        this.isDone = false; // default belum selesai
    }
}
