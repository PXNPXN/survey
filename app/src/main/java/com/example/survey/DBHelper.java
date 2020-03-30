package com.example.survey;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name,SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table if not exists survey(id integer " +
                "primary key autoincrement,"+"surveyID text not null unique,"+
                "surveyJsonString text not null)");

        db.execSQL("Create table answer( id integer primary key autoincrement," +
                "latitude double not null, " +
                "longitude double not null," +
                "timestamp long not null," +
                "sync integer not null,"+
                "IMEI char(20)," + //15-17 number sequence
                "answer text)");
    }
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){

    }

}
