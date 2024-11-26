package com.example.demo_cuoiky.Database;

import androidx.annotation.NonNull;

public class Event {
    private int id;  // Đổi tên `eventId` thành `id`
    private String name;
    private String date;
    private String repeatFrequency;  // Đổi tên `repeat_frequency` thành `repeatFrequency`
    private String description;

    // Constructor mặc định
    public Event() {
    }

    // Constructor đầy đủ
    public Event(int id, String name, String date, String repeatFrequency, String description) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.repeatFrequency = repeatFrequency;
        this.description = description;
    }

    @NonNull
    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +  // Sử dụng `id` thay vì `eventId`
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", repeatFrequency='" + repeatFrequency + '\'' +  // Sử dụng `repeatFrequency`
                ", description='" + description + '\'' +
                '}';
    }

    // Getter và setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
}
