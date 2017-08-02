package tori.studygroups.otherClass;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class EventDB {

    // database constants
    public static final String DB_NAME = "studyGroups.db";
    public static final int    DB_VERSION = 2;


    // event table constants
    public static final String EVENT_TABLE = "event";

    public static final String EVENT_ID = "_id";
    public static final int    EVENT_ID_COL = 0;

    public static final String EVENT_NAME = "name";
    public static final int    EVENT_NAME_COL = 1;

    public static final String EVENT_TIMESTAMP_DATE_EVENT = "timestamp_date_event";
    public static final int    EVENT_TIMESTAMP_DATE_EVENT_COL = 2;

    public static final String EVENT_DAY = "day";
    public static final int    EVENT_DAY_COL = 3;

    public static final String EVENT_HOUR = "hour";
    public static final int    EVENT_HOUR_COL = 4;

    public static final String EVENT_LOCATION = "location";
    public static final int    EVENT_LOCATION_COL = 5;

    public static final String EVENT_USERID = "userId";
    public static final int    EVENT_USERID_COL = 6;

    public static final String EVENT_USERNAME = "username";
    public static final int    EVENT_USERNAME_COL = 7;

    public static final String EVENT_CHANNEL_URL = "channel_url";
    public static final int    EVENT_CHANNEL_URL_COL = 8;

    public static final String EVENT_CHANNEL_NAME = "channel_name";
    public static final int    EVENT_CHANNEL_NAME_COL = 9;

    public static final String EVENT_TIMESTAMP_CREATED = "timestamp_created";
    public static final int    EVENT_TIMESTAMP_CREATED_COL = 10;


    public static final String CREATE_EVENT_TABLE =
            "CREATE TABLE " + EVENT_TABLE + " (" +
                    EVENT_ID                         + " TEXT PRIMARY KEY, " +
                    EVENT_NAME                       + " TEXT NOT NULL, " +
                    EVENT_TIMESTAMP_DATE_EVENT       + " INTEGER NOT NULL, " +
                    EVENT_DAY                        + " TEXT NOT NULL, " +
                    EVENT_HOUR                       + " TEXT NOT NULL, " +
                    EVENT_LOCATION                   + " TEXT NOT NULL, " +
                    EVENT_USERID                     + " TEXT NOT NULL, " +
                    EVENT_USERNAME                   + " TEXT NOT NULL, " +
                    EVENT_CHANNEL_URL                + " TEXT NOT NULL, " +
                    EVENT_CHANNEL_NAME               + " TEXT NOT NULL, " +
                    EVENT_TIMESTAMP_CREATED          + " INTEGER NOT NULL);";


    //TODO  cancellare tabella se disconnessione?
    public static final String DROP_EVENT_TABLE =
            "DROP TABLE IF EXISTS " + EVENT_TABLE;

    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name,
                        CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // create tables
            db.execSQL(CREATE_EVENT_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db,
                              int oldVersion, int newVersion) {

            Log.d("MAHHH", "Upgrading db from version "
                    + oldVersion + " to " + newVersion);

            db.execSQL(EventDB.DROP_EVENT_TABLE);
            onCreate(db);
        }
    }

    // database and database helper objects
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    // constructor
    public EventDB(Context context) {
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
    }

    // private methods
    private void openReadableDB() {
        db = dbHelper.getReadableDatabase();
    }

    private void openWriteableDB() {
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB() {
        if (db != null)
            db.close();
    }

    // public methods

    public ArrayList<MyEvent> getEvents() {

        this.openReadableDB();
        Cursor cursor = db.query(EVENT_TABLE, null, null, null,
                null, null, EVENT_TIMESTAMP_DATE_EVENT + " DESC");

        ArrayList<MyEvent> events = new ArrayList<MyEvent>();
        while (cursor.moveToNext()) {
            events.add(getEventFromCursor(cursor));
        }
        if (cursor != null)
            cursor.close();
        this.closeDB();

        return events;
    }

    public MyEvent getEvent(String id) {
        String where = EVENT_ID + "= ?";
        String[] whereArgs = { id };

        this.openReadableDB();
        Cursor cursor = db.query(EVENT_TABLE,
                null, where, whereArgs, null, null, null);
        cursor.moveToFirst();
        MyEvent event = getEventFromCursor(cursor);
        if (cursor != null)
            cursor.close();
        this.closeDB();

        return event;
    }

    private static MyEvent getEventFromCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0){
            return null;
        }
        else {
            try {
                MyEvent event = new MyEvent(
                        cursor.getString(EVENT_NAME_COL),
                        cursor.getString(EVENT_LOCATION_COL),
                        cursor.getLong(EVENT_TIMESTAMP_DATE_EVENT_COL),
                        cursor.getString(EVENT_DAY_COL),
                        cursor.getString(EVENT_HOUR_COL),
                        cursor.getString(EVENT_USERID_COL),
                        cursor.getString(EVENT_USERNAME_COL),
                        cursor.getString(EVENT_CHANNEL_URL_COL),
                        cursor.getString(EVENT_CHANNEL_NAME_COL),
                        cursor.getLong(EVENT_TIMESTAMP_CREATED_COL),
                        cursor.getString(EVENT_ID_COL));
                return event;
            }
            catch(Exception e) {
                return null;
            }
        }
    }

    public String insertEvent(MyEvent event) {
        ContentValues cv = new ContentValues();
        cv.put(EVENT_NAME, event.name);
        cv.put(EVENT_LOCATION,  event.location);
        cv.put(EVENT_TIMESTAMP_DATE_EVENT, event.timestampDateEvent);
        cv.put(EVENT_DAY, event.day);
        cv.put(EVENT_HOUR, event.hour);
        cv.put(EVENT_USERID, event.userId);
        cv.put(EVENT_USERNAME, event.userName);
        cv.put(EVENT_CHANNEL_URL, event.channelUrl);
        cv.put(EVENT_CHANNEL_NAME, event.channelName);
        cv.put(EVENT_TIMESTAMP_CREATED, event.timestampCreated);
        cv.put(EVENT_ID, event.eventId);

        this.openWriteableDB();
        db.insert(EVENT_TABLE, null, cv);
        this.closeDB();

        return event.eventId;
    }

    public int updateEvent(MyEvent event) {
        ContentValues cv = new ContentValues();
        cv.put(EVENT_ID, event.eventId);
        cv.put(EVENT_NAME, event.name);
        cv.put(EVENT_LOCATION,  event.location);
        cv.put(EVENT_TIMESTAMP_DATE_EVENT, event.timestampDateEvent);
        cv.put(EVENT_DAY, event.day);
        cv.put(EVENT_HOUR, event.hour);
        cv.put(EVENT_USERID, event.userId);
        cv.put(EVENT_USERNAME, event.userName);
        cv.put(EVENT_CHANNEL_URL, event.channelUrl);
        cv.put(EVENT_CHANNEL_NAME, event.channelName);
        cv.put(EVENT_TIMESTAMP_CREATED, event.timestampCreated);

        String where = EVENT_ID + "= ?";
        String[] whereArgs = { event.eventId };

        this.openWriteableDB();
        int rowCount = db.update(EVENT_TABLE, cv, where, whereArgs);
        this.closeDB();

        return rowCount;
    }

    public int deleteEvent(String id) {
        String where = EVENT_ID + "= ?";
        String[] whereArgs = { id };

        this.openWriteableDB();
        int rowCount = db.delete(EVENT_TABLE, where, whereArgs);
        this.closeDB();

        return rowCount;
    }
}