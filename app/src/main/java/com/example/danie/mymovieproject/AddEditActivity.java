package com.example.danie.mymovieproject;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static com.example.danie.mymovieproject.DBConstants.isEdit;

public class AddEditActivity extends AppCompatActivity implements View.OnClickListener {

    EditText subject_et , body_et , url_et;
    ImageView imageView;
    String id, body , subject , url , imdb , imageString;
    MySqLlHelper mySqLlHelper;
    ProgressDialog dialog;
    String MovieUrlBody = "http://www.omdbapi.com/?i=";
    RatingBar ratingBar;
    String ratingBarValue;
    Cursor cursor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        //3 buttons in this screen
        ((Button)findViewById(R.id.show_btn)).setOnClickListener(this);
        ((Button)findViewById(R.id.ok_btn)).setOnClickListener(this);
        ((Button)findViewById(R.id.cancel_btn)).setOnClickListener(this);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

       mySqLlHelper = new MySqLlHelper(this);

        subject_et = (EditText) findViewById(R.id.subject_et);
        body_et = (EditText) findViewById(R.id.body_et);
        url_et = (EditText) findViewById(R.id.url_et);


        /// if user clicked to edit IN main activity   (isedit = true):
        if(DBConstants.isEdit == true) {

            //DBConstants.isInternet = false;

            Intent getIntent = getIntent();
            subject = getIntent.getStringExtra(DBConstants.subjectColumn);
            body = getIntent.getStringExtra(DBConstants.bodyColumn);
            url = getIntent.getStringExtra(DBConstants.urlColumn);
            id = getIntent.getStringExtra(DBConstants.idColumn);
            ratingBarValue = getIntent.getStringExtra(DBConstants.ratingColumn);

            subject_et.setText(subject);
            body_et.setText(body);
            url_et.setText(url);

                if (ratingBarValue != null) {
                      ratingBar.setRating(Float.parseFloat(ratingBarValue));

                }





        }else if (DBConstants.isInternet == true) {
            Intent getIntentFromInternet = getIntent();
            subject = getIntentFromInternet.getStringExtra(DBConstants.subjectColumn);
            imdb = getIntentFromInternet.getStringExtra(DBConstants.bodyColumn);
            url = getIntentFromInternet.getStringExtra(DBConstants.urlColumn);


            subject_et.setText(subject);
            url_et.setText(url);


            DownloadBody downloadBody = new DownloadBody();
            downloadBody.execute(MovieUrlBody + imdb);

        }



    }

    /////////////onclick//////////////
    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case (R.id.show_btn):

                /// show btn...
                imageView = (ImageView) findViewById(R.id.imageView);

                String imageUrl= ((EditText) findViewById(R.id.url_et)).getText().toString();
                DownloadImage downloadImgeTask= new DownloadImage();
                downloadImgeTask.execute(imageUrl);

                break;

            case (R.id.ok_btn):


                if (DBConstants.isEdit == false) {

                    DBConstants.isInternet = false;

                    subject = subject_et.getText().toString();
                    body = body_et.getText().toString();
                    url = url_et.getText().toString();
                    ratingBarValue = Float.toString(ratingBar.getRating());
                    boolean exist=false;

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBConstants.subjectColumn, subject);
                    contentValues.put(DBConstants.bodyColumn, body);
                    contentValues.put(DBConstants.urlColumn, url);
                    contentValues.put(DBConstants.ratingColumn, ratingBarValue);
                    contentValues.put(DBConstants.imageColumn , imageString);

                    cursor = mySqLlHelper.getReadableDatabase().query(DBConstants.tableName , null , null , null , null , null , null);
                    while (cursor.moveToNext())
                    {
                        if (cursor.getString(cursor.getColumnIndex(DBConstants.subjectColumn)).equals(subject))
                        {
                            Toast.makeText(this, "movie exist", Toast.LENGTH_SHORT).show();
                            exist=true;
                        }
                    }

                    if (exist == false) {

                        subject = subject.trim();
                        if (subject.length() > 0) {

                            mySqLlHelper.getWritableDatabase().insert(DBConstants.tableName, null, contentValues);
                            //this intent close all the activities that opened
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "movie must have a subject", Toast.LENGTH_SHORT).show();
                            //close fun
                            return;
                        }

                    }


                }else  if (DBConstants.isEdit == true) {

                    subject = subject_et.getText().toString();
                    body = body_et.getText().toString();
                    url = url_et.getText().toString();
                    ratingBarValue = Float.toString(ratingBar.getRating());


                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBConstants.subjectColumn, subject);
                    contentValues.put(DBConstants.bodyColumn, body);
                    contentValues.put(DBConstants.urlColumn, url);
                    contentValues.put(DBConstants.ratingColumn, ratingBarValue);
                    contentValues.put(DBConstants.imageColumn , imageString);

                        subject = subject.trim();
                        if (subject.length() > 0) {

                            mySqLlHelper.getWritableDatabase().update(DBConstants.tableName, contentValues, "_id=?", new String[]{id});
                            DBConstants.isEdit = false;

                            //this intent close all the activities that opened
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "movie must have a subject", Toast.LENGTH_SHORT).show();
                            //close fun
                            return;
                        }



                }

                break;

            case (R.id.cancel_btn):

                finish();

                break;


        }


    }

    ////////DownloadImage////////////
    class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        private ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(AddEditActivity.this);
            dialog.setTitle("connecting");
            dialog.setMessage("please wait...");
            dialog.setCancelable(true);
            dialog.show();
            Log.d("pre", "exe");
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap= null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                // open a connection
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = (InputStream) url.getContent();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap downloadedImage) {

            imageView.setImageBitmap(downloadedImage);

            imageString = BitMapToString(downloadedImage);

            dialog.dismiss();

        }
    }


    /////bitmap  to string////////////
    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }


    /////////DownloadBody////////////
    public class DownloadBody extends AsyncTask<String, Long, String> {

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(AddEditActivity.this);
            dialog.setTitle("connecting");
            dialog.setMessage("please wait...");
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder response = null;
            try {
                URL website = new URL(params[0]);
                URLConnection connection = website.openConnection();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    response.append(inputLine);
                in.close();
            } catch (Exception rr) {
                rr.fillInStackTrace();
            }


            return response.toString();
        }

        @Override
        protected void onPostExecute(String s) {


            try {
                JSONObject mainO = new JSONObject(s);

                body_et.setText(mainO.getString("Plot"));

                ratingBarValue =(mainO.getString("imdbRating"));
                if(ratingBarValue.equals("N/A"))
                    ratingBar.setRating (0);
                else
                ratingBar.setRating (Float.parseFloat(ratingBarValue));

                dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();

                super.onPostExecute(s);
            }
        }


    }

}




