package com.example.demo_cuoiky;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.demo_cuoiky.Database.Task;
import com.example.demo_cuoiky.Database.TaskDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements OnTaskAddedListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_CODE_PERMISSION = 1001;

    private GridView gvCategories;
    private CategoriesAdapter gvAdapter;
    private List<CategoriesItem> categories;
    private ListView listView;
    private TasksArrayAdapter lvAdapter;
    private List<Task> tasksList;
    private TaskDatabaseHelper db;
    private List<Task> filteredTasks;
    private int selectedCategoryId = -1;

    public HomeFragment() {
        // Constructor yêu cầu phải có
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("ScheduleExactAlarm")
    public void setTaskAlarm(long timeInMillis, String taskName) {
        try {
            // Thiết lập báo thức
            AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);

            // Tạo Intent để kích hoạt AlarmReceiver
            Intent intent = new Intent(getActivity(), AlarmReceiver.class);
            intent.putExtra("task_name", taskName);

            // Tạo PendingIntent với FLAG_IMMUTABLE
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Sử dụng setExactAndAllowWhileIdle để báo thức hoạt động trong Doze Mode
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskAdded(Task task) {
        // Kiểm tra quyền trước khi cài đặt báo thức
        checkExactAlarmPermission();

        // Thêm task vào cơ sở dữ liệu
        db.addTask(task);

        // Thêm task vào danh sách của adapter
        tasksList.add(task);  // Thêm task vào danh sách gốc (tasksList)
        filteredTasks.clear();  // Xóa danh sách filteredTasks
        filteredTasks.addAll(tasksList);  // Cập nhật lại filteredTasks

        // Thông báo adapter cập nhật dữ liệu
        lvAdapter.notifyDataSetChanged();

        // Cài đặt báo thức nếu cần thiết
        setTaskAlarm(task.getTime(), task.getName());  // Bạn có thể bỏ comment để cài đặt báo thức
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new TaskDatabaseHelper(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo các view
        gvCategories = view.findViewById(R.id.gv_home_categories);
        listView = view.findViewById(R.id.lv_Todaytask);
        SearchView searchView = view.findViewById(R.id.sv_home);

        // Tạo danh sách categories
        categories = new ArrayList<>();
        categories.add(new CategoriesItem(R.drawable.icon_user, "Work"));
        categories.add(new CategoriesItem(R.drawable.icon_user, "Health"));
        categories.add(new CategoriesItem(R.drawable.icon_user, "Shopping"));
        categories.add(new CategoriesItem(R.drawable.icon_user, "Cooking"));

        // Khởi tạo và thiết lập adapter cho GridView
        gvAdapter = new CategoriesAdapter(requireActivity(), categories);
        gvCategories.setAdapter(gvAdapter);

        // Lấy danh sách task từ database
        tasksList = db.getAllTasks();
        filteredTasks = new ArrayList<>(tasksList);

        // Thiết lập adapter cho ListView
        lvAdapter = new TasksArrayAdapter(requireActivity(), filteredTasks);
        listView.setAdapter(lvAdapter);

        // Xử lý sự kiện click vào item trong ListView
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Task clickedTask = tasksList.get(position);
            showTaskDetailsDialog(clickedTask);
        });

        // Xử lý sự kiện tìm kiếm trong SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTasks();
                return true;
            }
        });

        // Xử lý sự kiện click vào category trong GridView
        gvCategories.setOnItemClickListener((parent, view12, position, id) -> {
            if (selectedCategoryId == position) {
                selectedCategoryId = -1;
                filterTasks();
            } else {
                selectedCategoryId = position;
                filterTasks();
            }
        });

        // Xử lý sự kiện long click để xóa task
        listView.setOnItemLongClickListener((parent, view13, position, id) -> {
            Task taskToDelete = tasksList.get(position);
            new AlertDialog.Builder(requireContext())
                    .setMessage("Bạn có chắc muốn xóa task này?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        db.deleteTask(taskToDelete.getTaskId());  // Xóa task khỏi cơ sở dữ liệu
                        cancelTaskAlarm(requireContext(), taskToDelete.getTaskId());  // Hủy báo thức nếu có
                        tasksList.remove(position);  // Xóa task khỏi danh sách tasks
                        filteredTasks.remove(position);  // Cập nhật filteredTasks
                        lvAdapter.notifyDataSetChanged();  // Cập nhật lại adapter
                    })
                    .setNegativeButton("Không", null)
                    .show();
            return true;
        });

        return view;
    }

    private void filterTasks() {
        filteredTasks.clear();
        for (Task task : tasksList) {
            // Kiểm tra categoryId đã được chọn hay không
            if (selectedCategoryId == -1 || task.getCategoryId() == selectedCategoryId) {
                filteredTasks.add(task);
            }
        }
        lvAdapter.notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    private void showTaskDetailsDialog(Task task) {
        // Lấy tên category từ selectedCategoryId
        String categoryName = categories.get(task.getCategoryId()).getLabel();

        // Tạo view cho dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_task_details, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle(task.getName())
                .setView(dialogView)
                .setPositiveButton("OK", null);

        // Hiển thị thông tin chi tiết task
        TextView tvName = dialogView.findViewById(R.id.tv_task_name);
        TextView tvDescription = dialogView.findViewById(R.id.tv_task_description);
        TextView tvDate = dialogView.findViewById(R.id.tv_task_date);
        TextView tvCategory = dialogView.findViewById(R.id.tv_task_category);

        tvName.setText(task.getName());
        tvDescription.setText(task.getDescription());
        tvDate.setText("Date: " + task.getDate());
        tvCategory.setText("Category: " + categoryName);  // Hiển thị tên danh mục

        builder.show();
    }

    // Kiểm tra quyền báo thức chính xác
    private void checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivityForResult(intent, REQUEST_CODE_PERMISSION);
            }
        }
    }

    // Hủy báo thức theo taskId
    private void cancelTaskAlarm(Context context, int taskId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, taskId, new Intent(context, AlarmReceiver.class), PendingIntent.FLAG_IMMUTABLE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
