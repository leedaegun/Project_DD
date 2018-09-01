package com.example.lg.deepdreamer.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lg on 2018-07-24.
 */
//알람 데이터를 담을 내부 데이터베이스
public class DBAdapter {
    private final Context context;
    static final String DB = "DeepDreamerAlarm";
    static final String TABLE_ALARM = "alarm";

    //////////////////////////////////////////////////////////////////
    static final int DB_VERSION = 1;
    // 1 : 2012년 4월 21일 (TABLE_ALARM 추가)


    // alarm table
    public static final String ALARM_ON        = "onoff";     // ON / OFF
    public static final String ALARM_APDAY     = "apday";  // 적용일
    public static final String ALARM_HOUR      = "hour";   // 적용일 : 시
    public static final String ALARM_MINUTE    = "minute"; // 적용일 : 분
    public static final String ALARM_RINGTONE  = "ring";   // 링톤
    public static final String ALARM_VIBRATE   = "vibrate";// vibrate

    ///////////////////////////////////////////////////////////////////
    static final String CREATE_ALARM = "create table " + TABLE_ALARM +
            " (_id integer primary key autoincrement, " +
            ALARM_ON + " integer, " +
            ALARM_APDAY + " integer, " +
            ALARM_HOUR + " integer, " +
            ALARM_MINUTE + " integer, " +
            ALARM_VIBRATE + " integer, " +
            ALARM_RINGTONE + " text);";


    static final String DROP = "drop table ";
    private SQLiteDatabase db;
    private NoteOpenHelper dbHelper;

    public DBAdapter(Context ctx) {
        context = ctx;
    }

    private static class NoteOpenHelper extends SQLiteOpenHelper {
        // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
        public NoteOpenHelper(Context c) {
            super(c, DB, null, DB_VERSION);
        }

        // DB를 새로 생성할 때 호출되는 함수
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_ALARM);
        }
        // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
            // TODO Auto-generated method stub
        }
    }

    public DBAdapter open() throws SQLException {
        dbHelper = new NoteOpenHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public Cursor fetchAllAlarm() {
        return db.query(TABLE_ALARM, null, null, null, null, null, ALARM_HOUR + " asc, " + ALARM_MINUTE + " asc");
    }

    public String addAlarm(int on, int day, int hour, int min, int vib, String ring) {//insert
        //알람 추가
        ContentValues values = new ContentValues();
        values.put(ALARM_ON, on);
        values.put(ALARM_APDAY, day);
        values.put(ALARM_HOUR, hour);
        values.put(ALARM_MINUTE, min);
        values.put(ALARM_RINGTONE, ring);
        values.put(ALARM_VIBRATE, vib);

        long id = db.insert(TABLE_ALARM, null, values);
        if (id < 0) {
            return "";
        }
        return Long.toString(id);
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    public void delAlarm(String id) {//delete
        //알람 행삭제
        db.delete(TABLE_ALARM, "_id = ?", new String[] {id});
    }

    public void modifyAlarm(long id, int on, int day, int hour, int min, int vib, String ring) {//update

        ContentValues values = new ContentValues();
        values.put(ALARM_ON, on);
        values.put(ALARM_APDAY, day);
        values.put(ALARM_HOUR, hour);
        values.put(ALARM_MINUTE, min);
        values.put(ALARM_RINGTONE, ring);
        values.put(ALARM_VIBRATE, vib);

        db.update(TABLE_ALARM, values, "_id" + "='" + id + "'", null);
    }

    public void modifyAlarmOn(long id, int on) {//update

        ContentValues values = new ContentValues();
        values.put(ALARM_ON, on);

        db.update(TABLE_ALARM, values, "_id" + "='" + id + "'", null);
    }


}
