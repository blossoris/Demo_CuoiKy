package com.example.demo_cuoiky;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.demo_cuoiky.Database.Event;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventsArrayAdapter extends ArrayAdapter<Event> {
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    public EventsArrayAdapter(Context context, List<Event> eventsList) {
        super(context, 0, eventsList);
    }

    @SuppressLint("ResourceType")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // Sử dụng đúng tài nguyên layout (R.layout.home_item_events)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.home_item_events, parent, false);
        }

        TextView tvItemName = convertView.findViewById(R.id.tv_item_name);
        TextView tvItemTime = convertView.findViewById(R.id.tv_item_date);

        Event event = getItem(position);

        if (event != null) {
            tvItemName.setText(event.getName());
            if (event.getDate() != null) {
                // Hiển thị ngày (và giờ nếu có)
                tvItemTime.setText(event.getDate());  // Nếu có giờ, bạn có thể format thêm giờ vào đây
            } else {
                tvItemTime.setText("No date set"); // Nếu không có ngày
            }
        }

        return convertView;
    }
}
