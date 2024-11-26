package com.example.demo_cuoiky;

public class CategoriesItem {
    private int iconResId;
    private String label;

    public CategoriesItem(int iconResId, String label) {
        this.iconResId = iconResId;
        this.label = label;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getLabel() {
        return label;
    }
}
