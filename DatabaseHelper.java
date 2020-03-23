package edu.usf.cse.labrador.familycare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Winter on 6/3/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME          = "Alarms.db";
    private static final int DATABASE_VERSION   = 1;

    //table names
    private static final String TABLE_ALARMS             = "ALARMS";

    //COLUMN name
    private static final String COLUMN_ALARM_ID          = "COL_ALARM_ID";
    private static final String COLUMN_TITLE             = "COL_TITLE";
    private static final String COLUMN_TIME              = "COL_TIME";
    private static final String COLUMN_TIME_IN_MILLIS    = "COL_TIME_IN_MILLIS";
    private static final String COLUMN_WHEN              = "COL_WHEN";
    private static final String COLUMN_TYPE              = "COL_TYPE";

    //only two  Type of alarms
    private static final String ONE_TIME_TYPE = "One Time";
    private static final String FREQUENT_TYPE = "Frequent";

    //CREATE ALARM TABLE
    private static final String TABLE_CREATE_ALARMS    =
            "CREATE TABLE " + TABLE_ALARMS + " (" +
                    COLUMN_ALARM_ID        + " INTEGER PRIMARY KEY, " +
                    COLUMN_TITLE     + " TEXT NOT NULL, " +
                    COLUMN_TIME     + " TEXT NOT NULL, " +
                    COLUMN_TIME_IN_MILLIS     + " INTEGER NOT NULL, " +
                    COLUMN_TYPE + " TEXT NOT NULL, " +
                    COLUMN_WHEN    + " TEXT NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_ALARMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);
        onCreate(db);
    }

    public long createOneTimeAlarm(OneTimeAlarm alarm){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, alarm.getTitle());
        contentValues.put(COLUMN_TIME, alarm.getTime());
        contentValues.put(COLUMN_TIME_IN_MILLIS, alarm.getTimeInMillis());
        contentValues.put(COLUMN_WHEN, alarm.getDate());
        contentValues.put(COLUMN_TYPE, ONE_TIME_TYPE);

        long result = db.insert(TABLE_ALARMS, null, contentValues);

        //if fails to insert alarm then return -1
        return result;
    }

    public long createFrequentAlarm(FrequentAlarm alarm, DayOfFrequentAlarm day){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, alarm.getTitle());
        contentValues.put(COLUMN_TIME, alarm.getTime());
        contentValues.put(COLUMN_TIME_IN_MILLIS, day.getTimeInMillis());
        contentValues.put(COLUMN_WHEN, day.getDayOfWeek());
        contentValues.put(COLUMN_TYPE, FREQUENT_TYPE);

        long result = db.insert(TABLE_ALARMS, null, contentValues);

        //if fails to insert alarm then return -1
        return result;
    }

    public boolean isTitleUsed(String title){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ALARMS + " A "
                + " WHERE A." + COLUMN_TITLE + " = ?";
        Cursor c = db.rawQuery(selectQuery, new String[] { title });
        if(c != null && c.getCount()> 0)  //if true means there already exists a alarm with that title
            return true;
        else
            return false;
    }

    public ArrayList<OneTimeAlarm> getAllOneTimeAlarms(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<OneTimeAlarm> oneTimeAlarms = new ArrayList<OneTimeAlarm>();

        String selectQuery = "SELECT  * "
                + " FROM " + TABLE_ALARMS + " A "
                + " WHERE A." + COLUMN_TYPE + " = ?";

        Cursor c = db.rawQuery(selectQuery, new String[] { ONE_TIME_TYPE });

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                OneTimeAlarm alarm = new OneTimeAlarm();
                alarm.setId(c.getInt(c.getColumnIndex(COLUMN_ALARM_ID)));
                alarm.setTitle(c.getString(c.getColumnIndex(COLUMN_TITLE)));
                alarm.setTime(c.getString(c.getColumnIndex(COLUMN_TIME)));
                alarm.setDate(c.getString(c.getColumnIndex(COLUMN_WHEN)));
                alarm.setTimeInMillis(c.getLong(c.getColumnIndex(COLUMN_TIME_IN_MILLIS)));

                // adding to list
                oneTimeAlarms.add(alarm);
            } while (c.moveToNext());
        }
        return oneTimeAlarms;
    }

    public ArrayList<DayOfFrequentAlarm> getDaysOfFrequentAlarm(String title){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DayOfFrequentAlarm> days = new ArrayList<DayOfFrequentAlarm>();

        String selectQuery = "SELECT * FROM " + TABLE_ALARMS + " A "
                + " WHERE A." + COLUMN_TITLE + " = ?";
        Cursor c = db.rawQuery(selectQuery, new String[] { title });

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                DayOfFrequentAlarm day = new DayOfFrequentAlarm();
                day.setId(c.getInt(c.getColumnIndex(COLUMN_ALARM_ID)));
                day.setTimeInMillis(c.getLong(c.getColumnIndex(COLUMN_TIME_IN_MILLIS)));
                day.setDayOfWeek(c.getString(c.getColumnIndex(COLUMN_WHEN)));
                // adding to list
                days.add(day);
            } while (c.moveToNext());
        }
        return days;
    }

    public ArrayList<FrequentAlarm> getAllFrequentAlarms(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<FrequentAlarm> frequentAlarms = new ArrayList<FrequentAlarm>();
        //Query
        String selectQuery = " SELECT  DISTINCT A." + COLUMN_TITLE + ", A." + COLUMN_TIME +
                " FROM " + TABLE_ALARMS + " A "
                + " WHERE A." + COLUMN_TYPE + " = ?";

        Cursor c = db.rawQuery(selectQuery, new String[] { FREQUENT_TYPE });

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                FrequentAlarm alarm = new FrequentAlarm();
                alarm.setTitle((c.getString(c.getColumnIndex(COLUMN_TITLE))));
                alarm.setTime((c.getString(c.getColumnIndex(COLUMN_TIME))));
                //getting all days when frequent alarm rings
                ArrayList<DayOfFrequentAlarm> days = getDaysOfFrequentAlarm(alarm.getTitle());
                alarm.setAlarms(days);
                // adding to list
                frequentAlarms.add(alarm);
            } while (c.moveToNext());
        }
        return frequentAlarms;
    }

    //Deleting a Alarm by id
    public void deleteAlarm(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALARMS, COLUMN_ALARM_ID + " = ?", new String[] { String.valueOf(id) });
    }

    //deleting alarms by title
    public void deleteAlarm(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALARMS, COLUMN_TITLE + " = ?", new String[] { title });
    }

}
