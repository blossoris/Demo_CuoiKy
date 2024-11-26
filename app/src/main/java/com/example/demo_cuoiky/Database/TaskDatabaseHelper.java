package com.example.demo_cuoiky.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class TaskDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_TASKS = "Tasks";
    private static final String COLUMN_TASK_ID = "task_id";
    private static final String COLUMN_CATEGORY_ID = "category_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRIORITY = "priority";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_REPEAT_FREQUENCY = "repeat_frequency";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_HAS_ALARM = "has_alarm";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_TASKS + " (" +
                    COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CATEGORY_ID + " INTEGER, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_PRIORITY + " INTEGER, " +
                    COLUMN_STATUS + " INTEGER, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_TIME + " INTEGER, " +
                    COLUMN_REPEAT_FREQUENCY + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_HAS_ALARM + " INTEGER);";

    public TaskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    // Thêm task vào cơ sở dữ liệu
    public long addTask(Task task) {
        if (task.getName() == null || task.getCategoryId() <= 0) {
            return -1;  // Kiểm tra invalid task
        }

        if (isTaskDuplicate(task)) {
            return -1;  // Kiểm tra trùng lặp
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_ID, task.getCategoryId());
        values.put(COLUMN_NAME, task.getName());
        values.put(COLUMN_PRIORITY, task.getPriority());
        values.put(COLUMN_STATUS, task.getStatus());
        values.put(COLUMN_DATE, task.getDate());
        values.put(COLUMN_TIME, task.getTime());
        values.put(COLUMN_REPEAT_FREQUENCY, task.getRepeatFrequency());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_HAS_ALARM, task.isHasAlarm() ? 1 : 0);

        long result = db.insert(TABLE_TASKS, null, values);
        db.close();
        return result;
    }

    // Kiểm tra trùng lặp task
    public boolean isTaskDuplicate(Task task) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_TASKS + " WHERE " +
                COLUMN_NAME + " = ? AND " +
                COLUMN_CATEGORY_ID + " = ?";

        if (task.getName() == null || task.getCategoryId() <= 0) {
            return false;  // Tránh kiểm tra trùng lặp với thông tin không hợp lệ
        }

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(selectQuery, new String[]{task.getName(), String.valueOf(task.getCategoryId())});
            return cursor != null && cursor.moveToFirst();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    // Lấy tất cả các task
    @SuppressLint("Range")
    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_TASKS, null, null, null, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                Task task = new Task();
                task.setTaskId(cursor.getInt(cursor.getColumnIndex(COLUMN_TASK_ID)));
                task.setCategoryId(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
                task.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                task.setPriority(cursor.getInt(cursor.getColumnIndex(COLUMN_PRIORITY)));
                task.setStatus(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)));
                task.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
                task.setTime(cursor.getLong(cursor.getColumnIndex(COLUMN_TIME)));
                task.setRepeatFrequency(cursor.getString(cursor.getColumnIndex(COLUMN_REPEAT_FREQUENCY)));
                task.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                task.setHasAlarm(cursor.getInt(cursor.getColumnIndex(COLUMN_HAS_ALARM)) == 1);

                taskList.add(task);
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return taskList;
    }

    // Lấy task theo ID
    @SuppressLint("Range")
    public Task getTaskById(int taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_TASKS, null, COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(taskId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                Task task = new Task();
                task.setTaskId(cursor.getInt(cursor.getColumnIndex(COLUMN_TASK_ID)));
                task.setCategoryId(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
                task.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                task.setPriority(cursor.getInt(cursor.getColumnIndex(COLUMN_PRIORITY)));
                task.setStatus(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)));
                task.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
                task.setTime(cursor.getLong(cursor.getColumnIndex(COLUMN_TIME)));
                task.setRepeatFrequency(cursor.getString(cursor.getColumnIndex(COLUMN_REPEAT_FREQUENCY)));
                task.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                task.setHasAlarm(cursor.getInt(cursor.getColumnIndex(COLUMN_HAS_ALARM)) == 1);
                return task;
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return null;
    }

    // Xóa task theo ID
    public void deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});
        db.close();
    }

    @SuppressLint("Range")
    public List<Task> getUpcomingTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Task> upcomingTasks = new ArrayList<>();

        // Lấy thời gian hiện tại (millisecond)
        long currentTime = System.currentTimeMillis();

        // Truy vấn tất cả các task có thời gian > thời gian hiện tại
        String selectQuery = "SELECT * FROM " + TABLE_TASKS + " WHERE " + COLUMN_TIME + " > ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(currentTime)});

        if (cursor.moveToFirst()) {
            do {
                // Chuyển đổi kết quả từ Cursor thành đối tượng Task
                Task task = new Task();
                task.setTaskId(cursor.getInt(cursor.getColumnIndex(COLUMN_TASK_ID)));
                task.setCategoryId(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
                task.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                task.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                task.setPriority(cursor.getInt(cursor.getColumnIndex(COLUMN_PRIORITY)));
                task.setTime(cursor.getLong(cursor.getColumnIndex(COLUMN_TIME)));
                task.setRepeatFrequency(cursor.getString(cursor.getColumnIndex(COLUMN_REPEAT_FREQUENCY)));

                // Thêm task vào danh sách
                upcomingTasks.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return upcomingTasks;
    }

    @SuppressLint("Range")
    public List<Task> getOverdueTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Task> overdueTasks = new ArrayList<>();

        // Get the current time (in milliseconds)
        long currentTime = System.currentTimeMillis();

        // Query the database for tasks with time < currentTime (i.e., overdue tasks)
        String selectQuery = "SELECT * FROM " + TABLE_TASKS + " WHERE " + COLUMN_TIME + " < ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(currentTime)});

        if (cursor.moveToFirst()) {
            do {
                // Convert the cursor result to a Task object
                Task task = new Task();
                task.setTaskId(cursor.getInt(cursor.getColumnIndex(COLUMN_TASK_ID)));
                task.setCategoryId(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
                task.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                task.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                task.setPriority(cursor.getInt(cursor.getColumnIndex(COLUMN_PRIORITY)));
                task.setTime(cursor.getLong(cursor.getColumnIndex(COLUMN_TIME)));
                task.setRepeatFrequency(cursor.getString(cursor.getColumnIndex(COLUMN_REPEAT_FREQUENCY)));

                // Add the task to the overdue tasks list
                overdueTasks.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return overdueTasks;
    }
}
