package com.example.demo_cuoiky;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.demo_cuoiky.Database.Task;
import com.example.demo_cuoiky.Database.TaskDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {

    private ListView upcoming_task_ListView;
    private ListView overdue_task_ListView;

    private TaskDatabaseHelper taskDatabaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        taskDatabaseHelper = new TaskDatabaseHelper(getContext());

        // Get the upcoming tasks and overdue tasks from the database
        List<Task> upcomingTasks = taskDatabaseHelper.getUpcomingTasks();
        List<Task> overdueTasks = taskDatabaseHelper.getOverdueTasks();

        // Check if there are no tasks in either list, and set a message if empty
        if (upcomingTasks.isEmpty()) {
            // Handle empty upcoming tasks list (optional: display a message)
        }
        if (overdueTasks.isEmpty()) {
            // Handle empty overdue tasks list (optional: display a message)
        }

        // Set up adapters for both lists
        TaskAdapter upcoming_task_adapter = new TaskAdapter(getContext(), upcomingTasks);
        upcoming_task_ListView = view.findViewById(R.id.upcoming_task_ListView);
        upcoming_task_ListView.setAdapter(upcoming_task_adapter);

        TaskAdapter overdue_task_adapter = new TaskAdapter(getContext(), overdueTasks);
        overdue_task_ListView = view.findViewById(R.id.overdue_task_ListView);
        overdue_task_ListView.setAdapter(overdue_task_adapter);

        return view;
    }

    // Custom ArrayAdapter to handle the display of tasks with checkboxes
    private static class TaskAdapter extends ArrayAdapter<Task> {

        public TaskAdapter(Context context, List<Task> tasks) {
            super(context, 0, tasks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_task_with_checkbox, parent, false);
            }

            // Find views
            CheckBox checkBox = convertView.findViewById(R.id.item_checkbox);
            TextView textView = convertView.findViewById(R.id.item_text);

            // Get the task for the current position
            Task task = getItem(position);
            if (task != null) {
                textView.setText(task.getName());
            }

            // Handle checkbox state change if needed
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Perform any action when checkbox state changes
            });

            return convertView;
        }
    }
}
