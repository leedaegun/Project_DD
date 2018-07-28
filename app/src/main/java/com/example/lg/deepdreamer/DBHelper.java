package com.example.lg.deepdreamer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lg on 2018-07-25.
 */

public class DBHelper extends SQLiteOpenHelper {

    static final String TABLE_ALARM = "DeepDreamerAlarm";

    //////////////////////////////////////////////////////////////////

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
//            ALARM_ON + " integer, " +
            ALARM_APDAY + " integer, " +
            ALARM_HOUR + " integer, " +
            ALARM_MINUTE + " integer);" ;
//            ALARM_VIBRATE + " integer, " +
//            ALARM_RINGTONE + " text);";




    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
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
    public void insert(int create_at, int item, int price) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO DeepDreamerAlarm VALUES(null, '" + item + "', " + price + ", '" + create_at + "');");
        db.close();
    }


    public void update(String item, int price) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 가격 정보 수정
        db.execSQL("UPDATE DeepDreamerAlarm SET price=" + price + " WHERE item='" + item + "';");
        db.close();
    }

    public void delete(String item) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM DeepDreamerAlarm WHERE item='" + item + "';");
        db.close();
    }
    public void allDelete(){
        //전체삭제
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM DeepDreamerAlarm ;");
        db.close();
    }



    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public String getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM DeepDreamerAlarm", null);
        while (cursor.moveToNext()) {
            result += cursor.getInt(0)
                    + " : "
                    + cursor.getInt(1)
                    + " | "
                    + cursor.getInt(2)
                    + "원 "
                    + cursor.getInt(3)
                    + "\n";
        }

        return result;
    }

    public void check(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if(c.moveToFirst()) {

            for(;;) {

                Log.e("table name : " ,c.getString(0));

                if(!c.moveToNext())

                    break;

            }

        }

    }






}

