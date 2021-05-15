package com.casmanager.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.casmanager.database.DatabaseHelper;
import com.casmanager.R;

import com.casmanager.activity_recyclerview.Activity;
import com.casmanager.logger_recyclerview.Log;
import com.casmanager.reflection_recyclerview.Reflection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class ExportActivity extends AppCompatActivity implements Button.OnClickListener{

    public static final int PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;
    public static final String EXPORT_DIR = "CAS Manager";


    private String FILE_NAME;
    EditText file_name;
    Button export_btn;
    TextView preview;
    Menu exportMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        file_name = findViewById(R.id.export_filename);
        export_btn = findViewById(R.id.export_btn);
        preview = findViewById(R.id.export_preview);

        preview.setText("");
        export_btn.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_activity, menu);
        getSupportActionBar().setTitle("Export");

        exportMenu = menu;

        exportMenu.findItem(R.id.close).setVisible(true);
        exportMenu.findItem(R.id.edit).setVisible(false);
        exportMenu.findItem(R.id.delete).setVisible(false);
        exportMenu.findItem(R.id.save).setVisible(false);
        exportMenu.findItem(R.id.add).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.close:
                startActivityForResult(new Intent(this, MainActivity.class),0);
                finish();
            default:
                return true;
        }
    }

    private boolean isExternalStorageWritable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }


    public void export() {
        if (isExternalStorageWritable()) {
            FileOutputStream fos = null;

            FILE_NAME = file_name.getText().toString();
            File export_dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), EXPORT_DIR + "/exports");
            File export = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + EXPORT_DIR + "/exports" , FILE_NAME + ".txt");

            if (!export_dir.exists()) export_dir.mkdirs();
            if (FILE_NAME.isEmpty()) {
                file_name.setError("Filename is empty!");
            } else if (export.exists()) {
                file_name.setError("File already exists, choose another name!");
            } else {
                try {
                    StringBuffer exportData = populateData();
                    if (exportData.toString().isEmpty())  {
                        AlertDialog.Builder alert = new AlertDialog.Builder(this);
                        alert.setTitle("No data to export!");
                        alert.setNegativeButton("Exit", (dialog, which) -> {
                            dialog.dismiss();
                        });

                        alert.show();
                    } else {
                        fos = new FileOutputStream(export);
                        fos.write(exportData.toString().getBytes());
                        fos.close();

                        Toast.makeText(this, "Successfully exported to"  + Environment.getExternalStorageDirectory().getAbsolutePath() +
                                EXPORT_DIR + "/exports" + "/" + FILE_NAME + ".txt", Toast.LENGTH_LONG).show();
                        load();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
    }

    public void load() {
        FileInputStream fis = null;
        File export = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + EXPORT_DIR + "/exports", FILE_NAME + ".txt");

        try {
            fis = new FileInputStream(export);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            preview.setText(sb.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public StringBuffer populateData() {
        StringBuffer stringBuffer = new StringBuffer();
        StringBuilder stringBuilder = new StringBuilder();
        DatabaseHelper db = new DatabaseHelper(this);

        ArrayList<Activity> activities = new ArrayList();
        ArrayList<Reflection> reflections = new ArrayList();
        ArrayList<Log> logs = new ArrayList();

        Cursor activityData = db.getAllActivityData();
        Cursor reflectionData = db.getAllReflectionData();
        Cursor logData = db.getAllLogs();

        if (activityData.getCount() != 0) {
            while(activityData.moveToNext()) {
                activities.add(new Activity(activityData.getInt(0),activityData.getString(1),
                        activityData.getString(2), activityData.getString(3)));
            }
        }

        if (reflectionData.getCount() != 0) {
            while(reflectionData.moveToNext()) {
                reflections.add(new Reflection(reflectionData.getInt(0),reflectionData.getString(1),
                        reflectionData.getString(2), reflectionData.getString(3), reflectionData.getString(4)));
            }
        }

        if (logData.getCount() != 0) {
            while(logData.moveToNext()) {
                logs.add(new Log(logData.getInt(0),logData.getString(1),
                        logData.getString(2), logData.getString(3), logData.getString(4)));
            }
        }

        if (!activities.isEmpty()) {
            stringBuffer.append("\n\nActivities:");
            for(Activity activity: activities) {
                stringBuffer.append("\nActivity name: " + activity.getTitle());
                stringBuffer.append("\nActivity description: " + activity.getDescription());
                stringBuffer.append("\nDate created: " + activity.getDate());
            }
        }

        if (!reflections.isEmpty()) {
            stringBuffer.append("\n\nReflections:");
            for(Reflection reflection: reflections) {
                stringBuffer.append("\n" + reflection.getTitle() + "     " + reflection.getDate() + "     " + reflection.getDescription());
                stringBuffer.append("\nReflection title: " + reflection.getTitle());
                stringBuffer.append("\nReflection contents: " + reflection.getDescription());
                stringBuffer.append("\nDate created: " + reflection.getDate());
            }
        }

        if (!logs.isEmpty()) {
            stringBuffer.append("\n\nLogs:");
            for(Log log: logs) {
                stringBuffer.append("\nLogged activity: " + log.getActivity());
                stringBuffer.append("\nDate and time logged: " + log.getDate());
                stringBuffer.append("\nDuration: " + log.getLength() + " " + log.getType().toLowerCase());
            }
        }

        if (stringBuffer.toString().isEmpty()) {
            return new StringBuffer("");
        }

        return stringBuffer;
    }

    @Override
    public void onClick(View v) {
        String[] PERMISSIONS = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        if ((ContextCompat.checkSelfPermission(ExportActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(ExportActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            export();
        } else {
            ActivityCompat.requestPermissions(ExportActivity.this, PERMISSIONS, PERMISSIONS_WRITE_EXTERNAL_STORAGE);

            if ((ContextCompat.checkSelfPermission(ExportActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(ExportActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                export();
            } else {
                Toast.makeText(this, "The app does not have permission to export the data!", Toast.LENGTH_LONG).show();
            }
        }

    }
}
