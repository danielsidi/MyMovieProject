package com.example.danie.mymovieproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by danie on 08/02/2017.
 */

public class MySqLlHelper extends SQLiteOpenHelper {

    Context context;

    public MySqLlHelper(Context context) {
        super(context, "movie_db" , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQLCreate="CREATE TABLE "+ DBConstants.tableName+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+ DBConstants.subjectColumn+" TEXT,  "+DBConstants.bodyColumn+" TEXT,  "+DBConstants.urlColumn+" TEXT , "+DBConstants.ratingColumn+" TEXT , "+DBConstants.imageColumn+" TEXT  )";
        db.execSQL(SQLCreate);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
