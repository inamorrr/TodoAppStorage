package com.Informatika.todolistImtinan.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY time ASC")
    List<Task> getAll();

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    Task getById(int id);

    @Query("UPDATE tasks SET is_done = :done WHERE id = :id")
    void updateStatus(int id, boolean done);

    @Query("SELECT * FROM tasks WHERE date(time/1000, 'unixepoch', 'localtime') = date('now', 'localtime') ORDER BY time ASC")
    List<Task> getTodayTasks();

    @Insert
    void insert(Task task);

    @Delete
    void delete(Task task);


}
