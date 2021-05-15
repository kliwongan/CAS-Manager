package com.casmanager.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.casmanager.database.DatabaseHelper;
import com.casmanager.R;

import androidx.appcompat.app.AppCompatActivity;

public class EditActivity extends AppCompatActivity{

    //Database manager to manage data input/output using helper object
    private DatabaseHelper db = new DatabaseHelper(this);

    //View and Dialog objects that are shown in activity
    EditText title, description;
    ProgressDialog prog;

    //Placeholder variables to update activity
    int id;
    String text, desc, date;

    //Different menu from when user enters in update activity
    Menu editMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //Initializes all views, textedits, and visible layouts in the activity
        title = findViewById(R.id.title);
        description = findViewById(R.id.descr);

        //Initializes the progress dialog
        prog = new ProgressDialog(this);
        prog.setMessage("Please wait...");
        prog.setCancelable(false);

        //Retrieves intent information from transition from mainactivity to editactivity
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        text = intent.getStringExtra("title");
        desc = intent.getStringExtra("description");
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
        getSupportActionBar().setTitle("Edit Activity");


        //Sets the update mode menu to the normal menu
        editMenu = menu;

        /*If the id is at its default value, meaning no id value,
        the activity doesn't exist yet, and only the save button appears
        * else, all the buttons appear because the activity exists and it is editable and deletable
        * */

        if (id != 0) {
            editMenu.findItem(R.id.edit).setVisible(true);
            editMenu.findItem(R.id.delete).setVisible(true);
            editMenu.findItem(R.id.save).setVisible(false);
            editMenu.findItem(R.id.add).setVisible(false);
            editMenu.findItem(R.id.close).setVisible(true);
        } else {
            getSupportActionBar().setTitle("Add Activity");
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
        String date = "";

        switch (item.getItemId()) {
            case R.id.save:

                if (name.isEmpty()) {
                    title.setError("Title is empty");
                } else if (descr.isEmpty()) {
                    description.setError("Description is empty");
                } else {
                    saveActivity(name, descr, date);
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
                    updateActivity(id, name, descr, date);
                }

                return true;
            case R.id.delete:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Confirm");
                alert.setMessage("Are you sure you want to delete this activity? This action will delete all associated reflections and logs");
                alert.setNegativeButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    deleteActivity(id);
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

    private void saveActivity(String name, String desc, String date) {
        prog.show();

        boolean result = db.insertActivity(name, desc);

        if (result == true) {
            Toast.makeText(this, "Save successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            prog.dismiss();
        } else {
            startActivity(new Intent(this, MainActivity.class));
            prog.dismiss();
            finish();
            Toast.makeText(this, "Save unsuccessful", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateActivity(int id, String name, String desc, String date) {
        prog.show();

        String ids = Integer.toString(id);

        boolean result = db.updateActivity(ids, name, desc, date);

        if (result == true) {
            Toast.makeText(this, "Update successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            prog.dismiss();
        } else Toast.makeText(this, "Save unsuccessful", Toast.LENGTH_SHORT).show();

    }

    private void deleteActivity(int id) {
        prog.show();

        String ids = Integer.toString(id);

        Cursor reflectionData = db.getAllReflectionData();
        Cursor loggerData = db.getAllLogs();
        Cursor activity = db.getActivityData(id);

        activity.moveToNext();

        if (!(reflectionData.getCount()==0)) {
            while(reflectionData.moveToNext()) {
                if (reflectionData.getString(3).equalsIgnoreCase(activity.getString(1))) {
                    db.deleteReflection(Integer.toString(reflectionData.getInt(0)));
                }
            }
        }

        if (!(loggerData.getCount() == 0)) {
            while (loggerData.moveToNext()) {
                if (loggerData.getString(1).equalsIgnoreCase(activity.getString(1))) {
                    db.deleteLog(Integer.toString(loggerData.getInt(0)));
                }
            }
        }

        db.deleteActivity(ids);

        Toast.makeText(this, "Deletion successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
        prog.dismiss();

    }

    private void setDataFromIntentExtra() {
        if (id != 0) {
            title.setText(text);
            description.setText(desc);
            getSupportActionBar().setTitle("Edit Activity");

            readMode();
        } else {
            editMode();
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

}
