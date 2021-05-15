package com.casmanager.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.casmanager.database.DatabaseHelper;
import com.casmanager.R;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class EditReflection extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private DatabaseHelper db = new DatabaseHelper(this);

    //View and Dialog objects that are shown in activity
    EditText title, description;
    ProgressDialog prog;

    //Dropdown resources
    Spinner dropdown;
    ArrayList<String> activities = new ArrayList<>();

    //Placeholder variables to update activity
    int id;
    String text, desc, date, activity;

    //Different menu from when user enters in update activity
    Menu editMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reflection_edit);

        //Initializes all views, textedits, and visible layouts in the activity
        title = findViewById(R.id.ref_edit_title);
        description = findViewById(R.id.ref_descr);

        //Initializing and populating dropdown
        dropdown = findViewById(R.id.activity_dropdown);

        String[] dropper = populateDropdown();
        if (dropper != null) {
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dropper);
            dropdown.setAdapter(adapter);
            getSelection();
        }

        dropdown.setOnItemSelectedListener(this);

        //Initializes the progress dialog
        prog = new ProgressDialog(this);
        prog.setMessage("Please wait...");
        prog.setCancelable(false);

        //Retrieves intent information from transition from mainactivity to editactivity
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        text = intent.getStringExtra("title");
        desc = intent.getStringExtra("description");
        activity = intent.getStringExtra("activity");
        date = intent.getStringExtra("date");

        //Retrieves intent data to fill in the text input
        setDataFromIntentExtra();
    }

    //Basic initialization of options menu inside activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflates menu layout to cast layout file onto menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_activity, menu);
        getSupportActionBar().setTitle("Edit Reflection");

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
            getSupportActionBar().setTitle("Add Reflection");
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
        String name = title.getText().toString();
        String descr = description.getText().toString();
        String activity = dropdown.getSelectedItem().toString();

        //Get dropdown values here

        switch (item.getItemId()) {
            case R.id.save:

                if (name.isEmpty()) {
                    title.setError("Title is empty");
                } else if (descr.isEmpty()) {
                    description.setError("Description is empty");
                } else {
                    saveReflection(name, descr, activity);
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

                if (name.isEmpty()) {
                    title.setError("Title is empty");
                } else if (descr.isEmpty()) {
                    description.setError("Description is empty");
                } else {
                    updateReflection(id, name, descr, activity, date);
                }

                return true;
            case R.id.delete:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Confirm");
                alert.setMessage("Are you sure you want to delete this reflection");
                alert.setNegativeButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    deleteReflection(id);
                });
                alert.setPositiveButton("Cancel", (dialog, which) -> dialog.dismiss());

                alert.show();

                return true;

            case R.id.close:
                finish();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveReflection(String title, String desc, String activity) {
        prog.show();

        boolean result = db.insertReflection(title, desc, activity);

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

    private void updateReflection(int id, String name, String desc, String activity, String date) {
        prog.show();

        String ids = Integer.toString(id);

        boolean result = db.updateReflection(ids, name, desc, activity, date);

        if (result == true) {
            Toast.makeText(this, "Update successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            //Insert activity transition animation here
            finish();
            prog.dismiss();
        } else Toast.makeText(this, "Save unsuccessful", Toast.LENGTH_SHORT).show();

    }

    private void deleteReflection(int id) {
        prog.show();

        String ids = Integer.toString(id);

        db.deleteReflection(ids);

        Toast.makeText(this, "Deletion successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        //Insert activity transition animation here
        finish();
        prog.dismiss();

    }

    private void setDataFromIntentExtra() {
        if (id != 0) {
            title.setText(text);
            description.setText(desc);

            getSupportActionBar().setTitle("Edit Reflection");
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

    private void getSelection() {setDropdownSelection(dropdown, activities, activity);}

    private void setDropdownSelection(Spinner dropdown, ArrayList<String> activities, String activity) {

        for (int i = 0; i < activities.size(); i++) {
            if (activities.get(i).equals(activity)) {
                dropdown.setSelection(i);
                break;
            }
        }

    }

    private void editMode() {
        title.setFocusableInTouchMode(true);
        description.setFocusableInTouchMode(true);
    }

    private void readMode() {
        title.setFocusableInTouchMode(false);
        description.setFocusableInTouchMode(false);
        title.setFocusable(false);
        description.setFocusable(false);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        activity = activities.get(position);
        dropdown.setSelection(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }
}
