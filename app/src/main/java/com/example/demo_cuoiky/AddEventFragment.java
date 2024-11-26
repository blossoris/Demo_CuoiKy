package com.example.demo_cuoiky;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.demo_cuoiky.Database.Event;
import com.example.demo_cuoiky.Database.EventDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEventFragment extends DialogFragment {
    private EditText etEventName;
    private EditText etDate;
    private Spinner spFrequency;
    private EditText etDescription;

    private OnEventAddedListener listener;
    private EventDatabaseHelper db;

    public static AddEventFragment newInstance(OnEventAddedListener listener) {
        AddEventFragment fragment = new AddEventFragment();
        fragment.setListener(listener);
        return fragment;
    }

    public void setListener(OnEventAddedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);  // Can be removed
    }

    @NonNull
    @Override
    public AlertDialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Event event = new Event();
        db = new EventDatabaseHelper(getContext()); // Initialize DB helper

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_addevent, null);

        // Map UI elements
        etEventName = dialogView.findViewById(R.id.tv_addevent_addevent_textbox);
        etDate = dialogView.findViewById(R.id.et_addevent_date_selector);
        etDescription = dialogView.findViewById(R.id.et_addevent_description);
        spFrequency = dialogView.findViewById(R.id.sp_addevent_frequency_selector);

        // Set up frequency spinner
        List<String> frequencies = new ArrayList<>();
        frequencies.add("Weekly");
        frequencies.add("Monthly");
        frequencies.add("Once");
        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(getContext(), R.layout.addevent_spinner_item_text, frequencies);
        frequencyAdapter.setDropDownViewResource(R.layout.addevent_spinner_item_text);
        spFrequency.setAdapter(frequencyAdapter);

        // Set current date in Date EditText
        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());
        etDate.setText(currentDate);

        // Date picker on Date field click
        etDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year, monthOfYear, dayOfMonth) -> {
                        calendar.set(year, monthOfYear, dayOfMonth);
                        String selectedDate = sdf.format(calendar.getTime());
                        etDate.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();  // Let system handle dialog buttons automatically
        });

        // Configure dialog buttons
        builder.setView(dialogView)
                .setPositiveButton("Save", (dialog, eventId) -> {
                    String name = etEventName.getText().toString().trim();
                    String date = etDate.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();

                    // Validate input
                    if (name.isEmpty() || date.isEmpty() || description.isEmpty()) {
                        showErrorDialog("Validation Error", "Please fill in all the fields!");
                        return;
                    }

                    // Format the date if the user manually enters it in a short format (like 1/11/2024)
                    if (date.matches("\\d{1}/\\d{1,2}/\\d{4}")) {  // If user entered day/month/year in short format
                        try {
                            SimpleDateFormat inputFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            calendar.setTime(inputFormat.parse(date));
                            String formattedDate = outputFormat.format(calendar.getTime());
                            date = formattedDate;  // Set the formatted date
                        } catch (Exception e) {
                            e.printStackTrace();
                            showErrorDialog("Error", "Invalid date format.");
                            return;
                        }
                    }

                    // Set values and save the event if valid
                    event.setName(name);
                    event.setDate(date);
                    event.setRepeatFrequency(spFrequency.getSelectedItem().toString());  // Updated method name
                    event.setDescription(description);

                    long result = db.addEvent(event);
                    if (result == -1) {
                        showErrorDialog("Error", "Failed to save the event. Please try again.");
                    } else {
                        if (listener != null) {
                            listener.onEventAdded(event);  // Notify listener if set
                        } else {
                            showErrorDialog("Warning", "No listener set for AddEventFragment!");
                        }
                        dismiss();  // Close dialog after saving
                    }
                })
                .setNegativeButton("Cancel", (dialog, eventId) -> dismiss());

        return builder.create();
    }

    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

}
