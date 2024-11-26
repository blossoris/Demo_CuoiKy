package com.example.demo_cuoiky;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.demo_cuoiky.Database.Task;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TasksArrayAdapter extends ArrayAdapter<Task> {
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private List<Task> tasks;

    public TasksArrayAdapter(@NonNull Context context, List<Task> tasks) {
        super(context, 0, tasks);
        this.tasks = tasks;
    }

    @NonNull
    @SuppressLint({"ResourceType", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.drawable.home_item_tasks, parent, false);
        }

        TextView tvItemName = convertView.findViewById(R.id.tv_item_name);
        TextView tvItemTime = convertView.findViewById(R.id.tv_item_time);

        Task task = getItem(position);

        if (task != null) {
            tvItemName.setText(task.getName());

            // Kiểm tra xem thời gian của task có hợp lệ không (time > 0)
            if (task.getTime() > 0) {
                // Chuyển đổi thời gian (long) thành chuỗi theo định dạng "hh:mm a"
                String formattedTime = timeFormat.format(task.getTime());
                tvItemTime.setText(formattedTime);
            } else {
                tvItemTime.setText("No time set");  // Hoặc để trống tùy theo yêu cầu
            }
        }

        return convertView;
    }

    // Phương thức cập nhật dữ liệu
    public void updateData(List<Task> newTasks) {
        this.tasks.clear();
        this.tasks.addAll(newTasks);
        notifyDataSetChanged();  // Cập nhật lại ListView
    }
}
