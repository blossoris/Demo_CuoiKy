package com.example.demo_cuoiky;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CategoriesAdapter extends BaseAdapter {

    private Context context;
    private List<CategoriesItem> items;
    private LayoutInflater inflater;

    public CategoriesAdapter(Context context, List<CategoriesItem> items) {
        this.context = context;
        this.items = items;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ResourceType")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.drawable.home_item_categories, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.img_home_categories);
        TextView textView = convertView.findViewById(R.id.tv_home_categories);

        CategoriesItem item = items.get(position);
        imageView.setImageResource(item.getIconResId());
        textView.setText(item.getLabel());

        return convertView;
    }
}
