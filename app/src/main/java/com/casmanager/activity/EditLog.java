package com.casmanager.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.casmanager.database.DatabaseHelper;
import com.casmanager.fragment.DatePickerFragment;
import com.casmanager.fragment.TimePickerFragment;
import com.casmanager.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class EditLog extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private DatabaseHelper db = new DatabaseHelper(this);

    //View and Dialog objects that are shown in activity
    ProgressDialog prog;

    //Views in the application
    TextView choose_activity, choose_date, logger_date, choose_time, logger_time, duration;
    Spinner activities_dropdown, length_type;
    EditText set_length;
    RelativeLayout date_container, time_container;
    Calendar calendar;

    ArrayList<String> activities = new ArrayList<>();

    //Placeholder variables to update activity
    int id;
    String date, activity, length, type;

    //Different menu from when user enters in update activity
    Menu editMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logger_edit);

        calendar = Calendar.getInstance();

        //Initializing and populating dropdown
        choose_activity = findViewById(R.id.choose_activity);
        choose_date = findViewById(R.id.choose_date);
        logger_date = findViewById(R.id.logger_date);
        choose_time = findViewById(R.id.choose_time);
        logger_time = findViewById(R.id.logger_time);
        duration = findViewById(R.id.duration);

        date_container = findViewById(R.id.edit_date);
        time_container = findViewById(R.id.edit_time);

        date_container.setOnClickListener(this::onClickDate);
        time_container.setOnClickListener(this::onClickTime);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM YYYY");
        dateFormat.setCalendar(calendar);

        logger_date.setText(dateFormat.format(calendar.getTime()));
        logger_time.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));

        activities_dropdown = findViewById(R.id.activities_dropdown);
        set_length = findViewById(R.id.set_length);
        length_type = findViewById(R.id.length_type);

        //Retrieves intent information from transition from mainactivity to editactivity
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        date = intent.getStringExtra("date");
        activity = intent.getStringExtra("activity");
        length = intent.getStringExtra("length");

        //Retrieves intent data to fill in the text input
        setDataFromIntentExtra();

        String[] activities_dropper = populateDropdown();
        if (activities_dropper != null) {
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activities_dropper);
            activities_dropdown.setAdapter(adapter);
            getActivitySelection();
        }
        activities_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activity = activities.get(position);
                activities_dropdown.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        String[] length_type_dropper = {"Minutes", "Hours"};
        if (length_type_dropper != null) {
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, length_type_dropper);
            length_type.setAdapter(adapter);
            getTimeTypeSelection(length_type, length_type_dropper, length);
        }

        length_type.setOnItemSelectedListener(this);

        //Initializes the progress dialog
        prog = new ProgressDialog(this);
        prog.setMessage("Please wait...");
        prog.setCancelable(false);

    }


    //Basic initialization of options menu inside activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflates menu layout to cast layout file onto menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_activity, menu);
        getSupportActionBar().setTitle("Edit Log");

        //Sets the update mode menu to the normal menu
        editMenu = menu;

        /*If the id is at its default value, meaning no id value,
        the activity doesn't exist yet, and only the save button appears
        * else, all the buttons appear because the reflection exists and it is editable and deletable
        * */

        if (id != 0) {
            editMenu.findItem(R.id.edit).setVisible(true);
            editMenu.findItem(R.id.delete).setVisible(true);
            editMenu.findItem(R.id.save).setVisible(false);
            editMenu.findItem(R.id.add).setVisible(false);
            editMenu.findItem(R.id.close).setVisible(true);
        } else {
            getSupportActionBar().setTitle("Log Time");
            editMenu.findItem(R.id.close).setVisible(true);
            editMenu.findItem(R.id.edit).setVisible(false);
            editMenu.findItem(R.id.delete).setVisible(false);
            editMenu.findItem(R.id.save).setVisible(true);
            editMenu.findItem(R.id.add).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String length = set_length.getText().toString();
        SimpleDateFormat formattedDate = new SimpleDateFormat("dd MMM YYYY hh:mm aaa");
        formattedDate.setCalendar(calendar);
        date = formattedDate.format(calendar.getTime());

        //Get dropdown values here

        switch (item.getItemId()) {
            case R.id.save:

                if (length.isEmpty()) {
                    set_length.setError("No duration specified!");
                } else {
                    saveLog(activity, date, length, type);
                }

                return true;

            case R.id.edit:

                editMode();
                editMenu.findItem(R.id.edit).setVisible(false);
                editMenu.findItem(R.id.delete).setVisible(false);
                editMenu.findItem(R.id.save).setVisible(false);
                editMenu.findItem(R.id.add).setVisible(true);

                return true;

            case R.id.add:
                //Updates the data when the button is selected

                if (length.isEmpty()) {
                    set_length.setError("No duration specified!");
                } else {
                    updateLog(id, activity, date, length, type);
                }


                return true;
            case R.id.delete:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Confirm");
                alert.setMessage("Are you sure you want to delete this log?");
                alert.setNegativeButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    deleteLog(id);
                });
                alert.setPositiveButton("Cancel", (dialog, which) -> dialog.dismiss());

                alert.show();

                return true;

            case R.id.close:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveLog(String activity, String date, String length, String type) {
        prog.show();

        boolean result = db.logTime(activity, date, length, type);

        if (result == true) {
            Toast.makeText(this, "Save successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            prog.dismiss();
        } else {
            Toast.makeText(this, "Save unsuccessful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            prog.dismiss();
        }

    }

    private void updateLog(int id, String activity, String date, String length, String type) {
        prog.show();

        String ids = Integer.toString(id);

        boolean result = db.updateLog(ids, activity, date, length, type);

        if (result == true) {
            Toast.makeText(this, "Update successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            //Insert activity transition animation here
            finish();
            prog.dismiss();
        } else Toast.makeText(this, "Save unsuccessful", Toast.LENGTH_SHORT).show();

    }

    private void deleteLog(int id) {
        prog.show();

        String ids = Integer.toString(id);

        db.deleteLog(ids);

        Toast.makeText(this, "Deletion successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        //Insert activity transition animation here
        finish();
        prog.dismiss();

    }

    private void setDataFromIntentExtra() {
        if (id != 0) {
            set_length.setText(length);
            readMode();
        } else {
            editMode();
        }
    }

    private String[] populateDropdown() {

        int counter = 0;
        Cursor data = db.getAllActivityData();

        if (data.getCount() == 0) {
            return null;
        } else {
            while(data.moveToNext()) {
                activities.add(data.getString(1));
                counter++;
            }
        }

        String[] dropdown_contents = new String[counter];
        for (int i = 0; i < activities.size(); i++) {
            dropdown_contents[i] = activities.get(i);
        }

        return dropdown_contents;
    }

    private void getActivitySelection() {setDropdownSelection(activities_dropdown, activities, activity);}

    private void getTimeTypeSelection(Spinner dropdown, String[] arr, String length_type) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(length_type)) {
                dropdown.setSelection(i);
                break;
            }
        }
    }

    private void setDropdownSelection(Spinner dropdown, ArrayList<String> activities, String activity) {

        for (int i = 0; i < activities.size(); i++) {
            if (activities.get(i).equals(activity)) {
                dropdown.setSelection(i);
                break;
            }
        }

    }

    private void editMode() {
        set_length.setFocusableInTouchMode(true);
    }

    private void readMode() {
        set_length.setFocusableInTouchMode(false);
        set_length.setFocusable(false);

    }

    private void onClickDate(View view) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "Date Picker");

    }

    private void onClickTime(View view) {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "Time Picker");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position==0) type = "minutes";
        else type = "hours";

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM YYYY");
        dateFormat.setCalendar(calendar);

        String timeText = dateFormat.format(calendar.getTime());
        logger_date.setText(timeText);
        Log.e("date","date was set");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        String timeText = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
        logger_time.setText(timeText);
        Log.e("time","time was set");
    }

    private void updateTimeText(Calendar c) {
        String timeText = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        logger_time.setText(timeText);
    }
}
