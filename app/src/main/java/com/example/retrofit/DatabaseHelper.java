package com.example.retrofit;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "DATA";
    private static final Integer DB_VERSION = 1;

    DatabaseHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {//Creating the database
        database.execSQL("CREATE TABLE DATA ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "NAME TEXT, "
                + "GLUCOSE INTEGER );"
        );
        insertData(database,"David",123);
        insertData(database,"WTF",9487);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,int oldVersion,int newVersion){

    }

    public static void insertData(SQLiteDatabase database,String name,Integer glucose){
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME",name);
        contentValues.put("GLUCOSE",glucose);
        database.insert("DATA",null,contentValues);
    }
}
