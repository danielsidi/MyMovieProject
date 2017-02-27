package com.example.danie.mymovieproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class InternetActivity extends AppCompatActivity implements View.OnClickListener {

    EditText search_line_et;
    Button go_btn , cancelBTN;
    ListView listView_internetActivity;
    String MoviesUrl = "http://omdbapi.com/?s=";
    ArrayList<MyMovie> allMovies;
    ArrayAdapter<MyMovie> adapter;
    ProgressDialog dialog;
    String imdbID;
    String name;
    String Poster;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet);

        ////buttons////
        go_btn = (Button) findViewById(R.id.go_btn);
        go_btn.setOnClickListener(this);
        cancelBTN = (Button) findViewById(R.id.cancelBTN);
        cancelBTN.setOnClickListener(this);

        search_line_et = (EditText) findViewById(R.id.search_line_et);
        listView_internetActivity = (ListView) findViewById(R.id.listView_internetActivity);

        allMovies = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,android.R.id.text1, allMovies);
        listView_internetActivity.setAdapter(adapter);


        //////set On Item Click Listener////open add edit screen with data
        listView_internetActivity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MyMovie currentMovie = allMovies.get(position);

                DBConstants.isInternet = true;
                Intent openAddScreenIntent = new Intent(InternetActivity.this , AddEditActivity.class);
                openAddScreenIntent.putExtra(DBConstants.subjectColumn , currentMovie.getSubject() );
                openAddScreenIntent.putExtra(DBConstants.bodyColumn , currentMovie.getBody());
                openAddScreenIntent.putExtra(DBConstants.urlColumn , currentMovie.getUrl());
                startActivity(openAddScreenIntent);

            }
        });
    }

    //////on click///////
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.go_btn:

                DownloadFromWeb downloadFromWeb = new DownloadFromWeb();;
                downloadFromWeb.execute(MoviesUrl+search_line_et.getText().toString());



                break;

            case R.id.cancelBTN:
                finish();
                break;
        }

    }
    

//////////DownloadFromWeb///////AsyncTask/////////
    public class DownloadFromWeb extends AsyncTask<String, Long, String> {

        @Override
        protected void onPreExecute()

        {
            dialog = new ProgressDialog(InternetActivity.this);
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
                        JSONArray jsonArray = mainO.getJSONArray("Search");
                        allMovies.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectcurrent = jsonArray.getJSONObject(i);
                    name = jsonObjectcurrent.getString("Title");
                    imdbID = jsonObjectcurrent.getString("imdbID");
                    Poster = jsonObjectcurrent.getString("Poster");
                    allMovies.add(new MyMovie(name, imdbID, Poster));
                }
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();

                super.onPostExecute(s);
            }
        }


    }

}


