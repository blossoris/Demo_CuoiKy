package com.example.demo_cuoiky;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<CategoriesItem> {

    public SpinnerAdapter(Context context, List<CategoriesItem> categories) {
        super(context, 0, categories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.addtask_spinner_item, parent, false);
        }

        CategoriesItem category = getItem(position);

        ImageView icon = view.findViewById(R.id.img_dropdownlist_icon);
        TextView label = view.findViewById(R.id.tv_dropdownlist_label);

        icon.setImageResource(category.getIconResId());
        label.setText(category.getLabel());


        return view;
    }

}
