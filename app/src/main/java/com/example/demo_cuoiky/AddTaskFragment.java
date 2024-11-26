package com.example.demo_cuoiky;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.demo_cuoiky.Database.Task;
import com.example.demo_cuoiky.Database.TaskDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTaskFragment extends DialogFragment {
    EditText etName;
    Spinner spCategories;
    EditText etDate;
    EditText etTime;
    Spinner spPriority;
    Spinner spFrequency;
    EditText etDescription;

    TaskDatabaseHelper db;
    List<Task> tasks;
    ArrayList<Task> taskArrayList;
    TasksArrayAdapter taskAdapter;

    OnTaskAddedListener listener;

    public static AddTaskFragment newInstance(OnTaskAddedListener listener) {
        AddTaskFragment fragment = new AddTaskFragment();
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo db (TaskDatabaseHelper)
        db = new TaskDatabaseHelper(getContext());  // Khởi tạo db
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Task task = new Task();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_addtask, null);

        // Ánh xạ View
        etName = dialogView.findViewById(R.id.tv_addtask_addtask_textbox);
        spCategories = dialogView.findViewById(R.id.sp_addtask_categories);
        etDate = dialogView.findViewById(R.id.et_addtask_date_selector);
        etTime = dialogView.findViewById(R.id.et_addtask_time_selector);
        spPriority = dialogView.findViewById(R.id.sp_addtask_priority);
        spFrequency = dialogView.findViewById(R.id.sp_addtask_frequency_selector);
        etDescription = dialogView.findViewById(R.id.et_addtask_description);

        // Cài đặt spinner Categories
        List<CategoriesItem> categories = new ArrayList<>();
        categories.add(new CategoriesItem(R.drawable.icon_user, "Work"));
        categories.add(new CategoriesItem(R.drawable.icon_user, "Health"));
        categories.add(new CategoriesItem(R.drawable.icon_user, "Shopping"));
        categories.add(new CategoriesItem(R.drawable.icon_user, "Cooking"));
        SpinnerAdapter categoryAdapter = new SpinnerAdapter(getContext(), categories);
        spCategories.setAdapter(categoryAdapter);
        spCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Xử lý khi người dùng chọn category
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Cài đặt spinner Priority và Frequency
        setUpSpinner(spPriority, Arrays.asList("High", "Medium", "Low"));
        setUpSpinner(spFrequency, Arrays.asList("Daily", "Weekly", "Monthly", "Once"));

        // Cài đặt DatePicker và TimePicker
        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());
        etDate.setText(currentDate);

        // DatePicker
        etDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year, monthOfYear, dayOfMonth) -> {
                        calendar.set(year, monthOfYear, dayOfMonth);
                        etDate.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // TimePicker
        SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm", Locale.getDefault());
        etTime.setText(sdf_time.format(calendar.getTime()));

        etTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    (view, hourOfDay, minute) -> {
                        etTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });

        builder.setView(dialogView)
                .setPositiveButton("Save", (dialog, id) -> {
                    // Lấy dữ liệu từ các trường và thêm vào Task
                    task.setName(etName.getText().toString());
                    task.setCategoryId(spCategories.getSelectedItemPosition());
                    String fullDate = etDate.getText().toString() + " " + etTime.getText().toString();
                    try {
                        SimpleDateFormat sdfFull = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                        task.setTime(sdfFull.parse(fullDate).getTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Invalid date/time format", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String selectedPriority = spPriority.getSelectedItem().toString();
                    task.setPriority("High".equals(selectedPriority) ? 3 : ("Medium".equals(selectedPriority) ? 2 : 1));
                    task.setRepeatFrequency(spFrequency.getSelectedItem().toString());
                    task.setDescription(etDescription.getText().toString());

                    if (task.getName().isEmpty()) {
                        Toast.makeText(getContext(), "Task name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Gọi db.addTask() sau khi khởi tạo db
                    db.addTask(task);

                    if (listener != null) {
                        listener.onTaskAdded(task);
                    }
                    dismiss();
                })
                .setNegativeButton("Cancel", (dialog, id) -> AddTaskFragment.this.getDialog().cancel());

        return builder.create();
    }

    // Phương thức setUpSpinner
    private void setUpSpinner(Spinner spinner, List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.addtask_spinner_item_text, items);
        adapter.setDropDownViewResource(R.layout.addtask_spinner_item_text);
        spinner.setAdapter(adapter);
    }
}
