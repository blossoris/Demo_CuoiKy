package com.example.demo_cuoiky.Database;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Task {

    private int taskId;                 // ID của task
    private int categoryId;             // ID danh mục (nếu có)
    private String name;                // Tên task
    private int priority;               // Độ ưu tiên (1 - thấp, 2 - trung bình, 3 - cao)
    private int status;                 // Trạng thái (0 - chưa hoàn thành, 1 - đã hoàn thành)
    private String date;                // Ngày của task (định dạng "yyyy-MM-dd")
    private long time;                  // Thời gian trong ngày (milliseconds từ 00:00)
    private String repeatFrequency;     // Tần suất lặp (daily, weekly, monthly, none)
    private String description;         // Mô tả task
    private boolean hasAlarm;           // Task có báo thức không?

    // Constructor với tất cả các tham số
    public Task(int taskId, int categoryId, String name, int priority, int status,
                String date, long time, String repeatFrequency, String description, boolean hasAlarm) {
        this.taskId = taskId;
        this.categoryId = categoryId;
        this.name = name;
        this.priority = priority;
        this.status = status;
        this.date = date;
        this.time = time;
        this.repeatFrequency = repeatFrequency;
        this.description = description;
        this.hasAlarm = hasAlarm;
    }

    // Constructor mặc định
    public Task() {
    }

    // Getter và Setter cho tất cả các trường
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getRepeatFrequency() {
        return repeatFrequency;
    }

    public void setRepeatFrequency(String repeatFrequency) {
        this.repeatFrequency = repeatFrequency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHasAlarm() {
        return hasAlarm;
    }

    public void setHasAlarm(boolean hasAlarm) {
        this.hasAlarm = hasAlarm;
    }

    // Hàm chuyển đổi từ chuỗi "HH:mm" sang long (milliseconds)
    public static long convertTimeToMilliseconds(String timeStr) throws ParseException {
        // Chuyển đổi timeStr thành milliseconds (long)
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return Objects.requireNonNull(sdf.parse(timeStr)).getTime();
    }

    // Hàm chuyển đổi từ long (milliseconds) sang chuỗi "HH:mm"
    public static String convertMillisecondsToTime(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return sdf.format(calendar.getTime());
    }

    // Hàm kiểm tra nếu task có báo thức vào một thời điểm cụ thể
    public Calendar getAlarmTime() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date taskDate = dateFormat.parse(this.date);
            Calendar calendar = Calendar.getInstance();
            if (taskDate != null) {
                calendar.setTime(taskDate);
                calendar.set(Calendar.HOUR_OF_DAY, (int) (this.time / (1000 * 60 * 60)));
                calendar.set(Calendar.MINUTE, (int) ((this.time / (1000 * 60)) % 60));
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                return calendar;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Hàm kiểm tra task có trùng với một ngày cụ thể
    public boolean isDueOnDate(String dateStr) {
        return this.date.equals(dateStr);
    }

    // Hàm tạo chuỗi tóm tắt task
    public String getSummary() {
        return String.format(Locale.getDefault(),
                "Task: %s\nPriority: %d\nDue: %s %s\nDescription: %s",
                this.name,
                this.priority,
                this.date,
                convertMillisecondsToTime(this.time),
                this.description
        );
    }

    // Hàm kiểm tra nếu task lặp lại
    public boolean isRepeating() {
        return !repeatFrequency.equals("none");
    }

    // Hàm tính toán thời gian báo thức tiếp theo
    public Calendar getNextAlarmTime() {
        Calendar alarmTime = getAlarmTime();
        if (alarmTime == null) return null;

        switch (repeatFrequency) {
            case "daily":
                alarmTime.add(Calendar.DAY_OF_MONTH, 1);
                break;
            case "weekly":
                alarmTime.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case "monthly":
                alarmTime.add(Calendar.MONTH, 1);
                break;
            default:
                break;
        }
        return alarmTime;
    }
}
