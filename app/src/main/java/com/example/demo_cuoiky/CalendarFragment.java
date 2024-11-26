package com.example.demo_cuoiky;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener;
import com.example.demo_cuoiky.Database.Event;
import com.example.demo_cuoiky.Database.EventDatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment implements OnEventAddedListener {

    private ListView eventListView;
    private List<Event> eventList;
    private EventsArrayAdapter adapter;
    private CalendarView calendarView;
    private List<CalendarDay> events;
    private EventDatabaseHelper dbHelper;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Calendar selectedCalendar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new EventDatabaseHelper(requireContext());
        selectedCalendar = Calendar.getInstance(); // Mặc định chọn ngày hiện tại
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        eventListView = view.findViewById(R.id.event_ListView);
        calendarView = view.findViewById(R.id.view_calendar);
        // Load tất cả sự kiện từ cơ sở dữ liệu
        eventList = dbHelper.getAllEvents();
        events = new ArrayList<>();
        addEventsToCalendar();
        adapter = new EventsArrayAdapter(requireActivity(), eventList);
        eventListView.setAdapter(adapter);
        // Lắng nghe sự kiện click vào ngày trên CalendarView
        calendarView.setOnCalendarDayClickListener(new OnCalendarDayClickListener() {
            @Override
            public void onClick(@NonNull CalendarDay calendarDay) {
                selectedCalendar.setTime(calendarDay.getCalendar().getTime());
                loadEventsByDate(selectedCalendar);  // Lọc sự kiện theo ngày đã chọn
            }
        });
        // Hiển thị chi tiết sự kiện khi click vào một sự kiện
        eventListView.setOnItemClickListener((parent, view12, position, eventId) -> {
            Event clickedEvent = eventList.get(position);
            if (clickedEvent != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle(clickedEvent.getName());
                String taskDetails = "Ngày: " + clickedEvent.getDate() + "\n" +
                        "Lặp lại: " + clickedEvent.getRepeatFrequency() + "\n" +  // Đã thay đổi tên phương thức
                        "Mô tả: " + clickedEvent.getDescription();
                builder.setMessage(taskDetails);
                builder.setPositiveButton("OK", null);
                builder.show();
            }
        });

        return view;
    }
    // Phương thức cập nhật lại sự kiện trong CalendarView
    private void addEventsToCalendar() {
        if (eventList != null && !eventList.isEmpty()) {
            events.clear(); // Xóa danh sách sự kiện cũ
            // Dùng HashSet để tránh trùng lặp
            Set<CalendarDay> uniqueEvents = new HashSet<>();
            for (Event event : eventList) {
                try {
                    String dateStr = event.getDate(); // Lấy ngày từ Event
                    Calendar eventCalendar = Calendar.getInstance();
                    eventCalendar.setTime(sdf.parse(dateStr));  // Đảm bảo định dạng chuẩn
                    // Chuyển lại lịch để đồng bộ hóa định dạng
                    CalendarDay calendarDay = new CalendarDay(eventCalendar);
                    calendarDay.setImageResource(R.drawable.ic_check); // Icon cho sự kiện
                    // Thêm vào HashSet để tránh trùng lặp
                    uniqueEvents.add(calendarDay);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            // Cập nhật lại các ngày có sự kiện lên CalendarView
            calendarView.setCalendarDays(new ArrayList<>(uniqueEvents));
        }
    }

    // Phương thức lọc sự kiện theo ngày đã chọn
    private void loadEventsByDate(Calendar date) {
        String selectedDate = sdf.format(date.getTime());  // Đảm bảo rằng ngày được định dạng chuẩn
        List<Event> filteredEvents = new ArrayList<>();

        // Lọc các sự kiện theo ngày đã chọn
        for (Event event : dbHelper.getAllEvents()) {
            if (event.getDate().equals(selectedDate)) {  // So sánh ngày với định dạng chuẩn
                filteredEvents.add(event);
            }
        }
        // Xóa sự kiện cũ trong eventList và thêm sự kiện mới
        eventList.clear();
        eventList.addAll(filteredEvents);
        adapter.notifyDataSetChanged();
        // Nếu không có sự kiện, hiển thị thông báo
        if (filteredEvents.isEmpty()) {
            Toast.makeText(getContext(), "Không có sự kiện cho ngày này", Toast.LENGTH_SHORT).show();
        }
    }

    // Cập nhật lại sự kiện khi sự kiện được thêm vào
    @Override
    public void onEventAdded(Event event) {
        // Kiểm tra xem sự kiện đã tồn tại chưa
        if (!dbHelper.isEventExist(event)) {
            dbHelper.addEvent(event); // Thêm sự kiện vào cơ sở dữ liệu
        }
        // Xóa tất cả sự kiện cũ trong eventList và thêm sự kiện mới
        eventList.clear();
        eventList.addAll(dbHelper.getAllEvents());
        // Thông báo cho adapter rằng dữ liệu đã thay đổi
        adapter.notifyDataSetChanged();
        // Cập nhật các ngày có sự kiện trong CalendarView
        addEventsToCalendar();
    }
}
