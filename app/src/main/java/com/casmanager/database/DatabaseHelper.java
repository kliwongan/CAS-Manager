package com.casmanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.util.Calendar;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "storage";
    public static final String TABLE1_NAME = "activity_data";
    public static final String COL1 = "ID";
    public static final String COL2 = "NAME";
    public static final String COL3 = "DESCRIPTION";
    public static final String COL4 = "DATE";

    public static final String TABLE2_NAME = "reflection_data";
    public static final String TCOL1 = "ID";
    public static final String TCOL2 = "TITLE";
    public static final String TCOL3 = "DESCRIPTION";
    public static final String TCOL4 = "ACTIVITY";
    public static final String TCOL5 = "DATE";

    public static final String TABLE3_NAME = "logger_data";
    public static final String LCOL1 = "ID";
    public static final String LCOL2 = "ACTIVITY";
    public static final String LCOL3 = "DATE";
    public static final String LCOL4 = "DURATION";
    public static final String LCOL5 = "TYPE";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    /* Database-wide functions */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE1_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, DESCRIPTION TEXT, DATE TEXT)");
        db.execSQL("create table " + TABLE2_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, DESCRIPTION TEXT, ACTIVITY TEXT, DATE TEXT)");
        db.execSQL("create table " + TABLE3_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, ACTIVITY TEXT, DATE TEXT, DURATION TEXT, TYPE TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE1_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE2_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE3_NAME);
        onCreate(db);
    }

    /* Activity Database Methods */

    public boolean insertActivity(String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Calendar calendar = Calendar.getInstance();
        String created = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());

        contentValues.put(COL2, name);
        contentValues.put(COL3, description);
        contentValues.put(COL4, created);

        long result = db.insert(TABLE1_NAME, null, contentValues);
        if (result == -1) return false;
        else return true;
    }

    public Cursor getActivityData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE1_NAME + " where ID="+ id, null);
        return res;
    }

    public Cursor getAllActivityData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE1_NAME, null);
        return res;
    }

    public boolean updateActivity(String id, String name, String description, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("select * from " + TABLE1_NAME, null);

        ContentValues contentValues = new ContentValues();

        res.moveToNext();
        String created = res.getString(3);

        contentValues.put(COL1, id);
        contentValues.put(COL2, name);
        contentValues.put(COL3, description);
        contentValues.put(COL4, created);

        db.update(TABLE1_NAME, contentValues, COL1 + "=" + id, null);
        return true;
    }

    public void deleteActivity(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE1_NAME, COL1 + "=" +id, null);
    }

    public void clearActivities() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE1_NAME);
        onCreate(db);
    }


    /* Reflection Database Methods */

    public boolean insertReflection(String title, String description, String activity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Calendar calendar = Calendar.getInstance();
        String created = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.getTime());

        contentValues.put(TCOL2, title);
        contentValues.put(TCOL3, description);
        contentValues.put(TCOL4, activity);
        contentValues.put(TCOL5, created);

        long result = db.insert(TABLE2_NAME, null, contentValues);
        if (result == -1) return false;
        else return true;
    }

    public Cursor getReflectionData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE2_NAME + " where ID="+ id, null);
        return res;
    }

    public Cursor getAllReflectionData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE2_NAME, null);
        return res;
    }

    public boolean updateReflection(String id, String title, String description, String activity, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("select * from " + TABLE2_NAME, null);

        ContentValues contentValues = new ContentValues();

        res.moveToNext();

        contentValues.put(TCOL1, id);
        contentValues.put(TCOL2, title);
        contentValues.put(TCOL3, description);
        contentValues.put(TCOL4, activity);
        contentValues.put(TCOL5, date);

        db.update(TABLE2_NAME, contentValues, TCOL1 + "=" + id, null);
        return true;
    }

    public void deleteReflection(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE2_NAME, TCOL1 + "=" +id, null);
    }

    public void clearReflections() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE2_NAME);
        onCreate(db);
    }

    /* Logger Database Methods */

    public boolean logTime(String title, String date, String length, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(LCOL2, title);
        contentValues.put(LCOL3, date);
        contentValues.put(LCOL4, length);
        contentValues.put(LCOL5, type);

        long result = db.insert(TABLE3_NAME, null, contentValues);
        if (result == -1) return false;
        else return true;
    }

    public Cursor getLog(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE3_NAME + " where ID="+ id, null);
        return res;
    }

    public Cursor getAllLogs() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE3_NAME, null);
        return res;
    }

    public boolean updateLog(String id, String activity, String date, String length, String type) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("select * from " + TABLE3_NAME, null);

        ContentValues contentValues = new ContentValues();

        res.moveToNext();

        contentValues.put(LCOL1, id);
        contentValues.put(LCOL2, activity);
        contentValues.put(LCOL3, date);
        contentValues.put(LCOL4, length);
        contentValues.put(LCOL5, type);

        db.update(TABLE3_NAME, contentValues, LCOL1 + "=" + id, null);
        return true;
    }

    public void deleteLog(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE3_NAME, LCOL1 + "=" +id, null);
    }

    public void clearLogs() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE3_NAME);
        onCreate(db);
    }
}
