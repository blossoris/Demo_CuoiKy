package com.example.demo_cuoiky.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class EventDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "events.db";
    private static final int DATABASE_VERSION = 2;

    // Table and columns
    private static final String TABLE_EVENTS = "events";

    private static final String COLUMN_ID = "id";  // Đổi từ eventId thành id
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_REPEAT_FREQUENCY = "repeatFrequency";  // Đổi từ repeat_frequency thành repeatFrequency
    private static final String COLUMN_DESCRIPTION = "description";

    public EventDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT," +
                COLUMN_DATE + " TEXT," +
                COLUMN_REPEAT_FREQUENCY + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT" +
                ")";
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            // Kiểm tra cột đã tồn tại hay chưa trước khi thêm mới
            String checkColumnQuery = "PRAGMA table_info(" + TABLE_EVENTS + ")";
            Cursor cursor = db.rawQuery(checkColumnQuery, null);
            boolean columnExists = false;

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") String columnName = cursor.getString(cursor.getColumnIndex("name"));
                    if (columnName.equals(COLUMN_REPEAT_FREQUENCY)) {
                        columnExists = true;
                        break;
                    }
                }
                cursor.close();
            }

            if (!columnExists) {
                String ADD_COLUMN_QUERY = "ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + COLUMN_REPEAT_FREQUENCY + " TEXT";
                db.execSQL(ADD_COLUMN_QUERY);
            }
        }
    }

    // Insert a new event
    public long addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, event.getName());
        values.put(COLUMN_DATE, event.getDate());
        values.put(COLUMN_REPEAT_FREQUENCY, event.getRepeatFrequency() != null ? event.getRepeatFrequency() : ""); // Cảnh báo nếu giá trị null
        values.put(COLUMN_DESCRIPTION, event.getDescription() != null ? event.getDescription() : ""); // Cảnh báo nếu giá trị null

        return db.insert(TABLE_EVENTS, null, values);
    }
    public boolean isEventExist(Event event) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Events", null, "date = ? AND name = ?",
                new String[]{event.getDate(), event.getName()}, null, null, null);

        return cursor != null && cursor.moveToFirst();
    }

    public Event getEventById(int eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Events", null, "id = ?", new String[]{String.valueOf(eventId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") Event event = new Event(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("date")),
                    cursor.getString(cursor.getColumnIndex("description")),
                    cursor.getString(cursor.getColumnIndex("repeatFrequency"))
            );
            cursor.close();
            return event;
        }

        return null; // Không tìm thấy sự kiện
    }

    // Lấy tất cả sự kiện
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT DISTINCT * FROM " + TABLE_EVENTS, null); // Use DISTINCT to ensure unique events
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                    @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                    @SuppressLint("Range") String frequency = cursor.getString(cursor.getColumnIndex(COLUMN_REPEAT_FREQUENCY));
                    @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));

                    Event event = new Event(id, name, date, frequency, description);
                    // Check for duplicate event before adding
                    if (!events.contains(event)) {
                        events.add(event);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("EventDatabaseHelper", "Error while fetching events", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return events;
    }

    // Update Event
    public void updateEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, event.getName());
        values.put(COLUMN_DATE, event.getDate());
        values.put(COLUMN_DESCRIPTION, event.getDescription());
        values.put(COLUMN_REPEAT_FREQUENCY, event.getRepeatFrequency());
        db.update(TABLE_EVENTS, values, COLUMN_ID + "=?", new String[]{String.valueOf(event.getId())});
        db.close();
    }
}
