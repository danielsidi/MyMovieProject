package com.example.danie.mymovieproject;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by danie on 26/02/2017.
 */

public class MyMovieAdapter extends CursorAdapter {
    public MyMovieAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View v = LayoutInflater.from(context).inflate(R.layout.my_movie_item, null);

        return v;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        String movieName = cursor.getString(cursor.getColumnIndex(DBConstants.subjectColumn));
        TextView nameTV = (TextView) view.findViewById(R.id.movie_name_tv);
        nameTV.setText(movieName);

        ImageView moviePoster = (ImageView) view.findViewById(R.id.movie_poster);

        String imageString = cursor.getString(cursor.getColumnIndex(DBConstants.imageColumn));
        moviePoster.setImageBitmap(StringToBitMap(imageString));

    }


    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    //TODO: CURSOR ADAPTER
}

