package com.example.danie.mymovieproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    Cursor cursor;
    SimpleCursorAdapter adapter;
    MySqLlHelper mySqLlHelper;
    int currentPosition;



    @Override
    protected void onResume() {

        cursor = mySqLlHelper.getReadableDatabase().query(DBConstants.tableName , null, null, null, null, null, null);
        adapter.swapCursor(cursor);
        super.onResume();
}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listView = (ListView) findViewById(R.id.list_view);
        mySqLlHelper = new MySqLlHelper(this);
        cursor = mySqLlHelper.getReadableDatabase().query(DBConstants.tableName, null, null, null, null, null, null);
        String[] fromColums = new String[]{DBConstants.subjectColumn};
        int[] toTV = new int[]{R.id.movie_name_tv};
        adapter = new SimpleCursorAdapter(this, R.layout.my_movie_item, cursor, fromColums, toTV);

        listView.setAdapter(adapter);
        registerForContextMenu(listView);

//we now putting the edit boolean to false to see if clicked the edit button.
        DBConstants.isEdit = false;

//plus btn + alert dialog - manual or internet activity:
        ((Button) findViewById(R.id.plus_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                //create the dialog:
                AlertDialog dialog = builder
                        .setTitle("")
                        .setMessage("select your preference")
                        .setIcon(R.drawable.mark)
                        .setPositiveButton("MANUAL ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //USER CLICKED MANUAL
                                Intent openAddEditScreenIntent = new Intent(MainActivity.this, AddEditActivity.class);
                                startActivity(openAddEditScreenIntent);
                            }
                        })
                        .setNegativeButton("INTERNET", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //USER CLICKED INTERNET
                                Intent openAddEditScreenIntent = new Intent(MainActivity.this, InternetActivity.class);
                                startActivity(openAddEditScreenIntent);
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                //show the dialog:
                dialog.show();

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

////from main activity to edit activity//////
                DBConstants.isEdit = true;
                cursor = mySqLlHelper.getReadableDatabase().query(DBConstants.tableName, null, null, null, null, null, null);
                cursor.moveToPosition(position);
                // cursor.moveToPosition(currentPosition);

                Intent openEditScreenIntent = new Intent(MainActivity.this, AddEditActivity.class);

                    openEditScreenIntent.putExtra(DBConstants.subjectColumn, cursor.getString(cursor.getColumnIndex(DBConstants.subjectColumn)));
                    openEditScreenIntent.putExtra(DBConstants.bodyColumn, cursor.getString(cursor.getColumnIndex(DBConstants.bodyColumn)));
                    openEditScreenIntent.putExtra(DBConstants.urlColumn, cursor.getString(cursor.getColumnIndex(DBConstants.urlColumn)));
                    openEditScreenIntent.putExtra(DBConstants.idColumn, cursor.getString(cursor.getColumnIndex(DBConstants.idColumn)));
                    openEditScreenIntent.putExtra(DBConstants.ratingColumn, cursor.getString(cursor.getColumnIndex(DBConstants.ratingColumn)));

                startActivity(openEditScreenIntent);


            }
        });

    }

////////////////////////////menus//////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.exit_btn:
                //exit

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                //create the dialog:
                AlertDialog dialog = builder
                        .setTitle(" exit ")
                        .setMessage("Are you sure you want to exit ?")
                        .setIcon(R.drawable.exit)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                finish();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //close the dialog
                                dialog.dismiss();
                            }
                        }).create();

                //show the dialog:
                dialog.show();


                break;

            case R.id.delete_all_btn:

                AlertDialog.Builder deleteAllBtnBuilder = new AlertDialog.Builder(this);

                //create the dialog:
                AlertDialog dialog2 = deleteAllBtnBuilder
                        .setTitle(" Delete data")
                        .setMessage("Are you sure you want to delete all?")
                        .setIcon(R.drawable.deleteall)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                //delete all
                                MySqLlHelper mySqLlHelper = new MySqLlHelper(MainActivity.this);
                                //LET ME WRITE. TABLE....CULOMN...
                                mySqLlHelper.getWritableDatabase().delete(DBConstants.tableName,null,null);
                                //cursor -  read only
                                cursor = mySqLlHelper.getReadableDatabase().query(DBConstants.tableName,null,null,null,null,null,null);
                                adapter.swapCursor(cursor);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //close the dialog
                                dialog.dismiss();
                            }
                        }).create();

                //show the dialog:
                dialog2.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    //SET ON contextMenu CLICK  (OPEN CONTEXT MENU WITH 2 OPTIONS :   INTENT EDIT OR DELETE item)
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        currentPosition  =((AdapterView.AdapterContextMenuInfo)menuInfo).position;

        getMenuInflater().inflate(R.menu.context_menu , menu);
        super.onCreateContextMenu(menu, v, menuInfo);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {


        switch (item.getItemId()){

            case (R.id.delete_btn):

                AlertDialog.Builder deleteAllBtnBuilder = new AlertDialog.Builder(this);
                //create the dialog:
                AlertDialog dialog2 = deleteAllBtnBuilder
                        .setTitle(" Delete data")
                        .setMessage("Are you sure you want to delete this item?")
                        .setIcon(R.drawable.questionmark)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //delete item
                                //openXL

                                cursor.moveToPosition(currentPosition);
                                int id=cursor.getInt(cursor.getColumnIndex(DBConstants.idColumn));

                                MySqLlHelper mySqLlHelper = new MySqLlHelper(MainActivity.this);

                                String[]strings=new String[]{""+id};
                                mySqLlHelper.getWritableDatabase().delete(DBConstants.tableName,"_id=?",strings);


                                cursor = mySqLlHelper.getReadableDatabase().query(DBConstants.tableName,null,null,null,null,null,null);
                                adapter.swapCursor(cursor);


                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //close the dialog
                                dialog.dismiss();
                            }
                        }).create();

                //show the dialog:
                dialog2.show();

                break;



            case (R.id.edit_btn):
                DBConstants.isEdit=true;
                cursor = mySqLlHelper.getReadableDatabase().query(DBConstants.tableName,null,null,null,null,null,null);

                cursor.moveToPosition(currentPosition);

                Intent openEditScreenIntent = new Intent(MainActivity.this , AddEditActivity.class);

                openEditScreenIntent.putExtra(DBConstants.subjectColumn , cursor.getString(cursor.getColumnIndex(DBConstants.subjectColumn)));
                openEditScreenIntent.putExtra(DBConstants.bodyColumn , cursor.getString(cursor.getColumnIndex(DBConstants.bodyColumn)));
                openEditScreenIntent.putExtra(DBConstants.urlColumn , cursor.getString(cursor.getColumnIndex(DBConstants.urlColumn)));
                openEditScreenIntent.putExtra(DBConstants.idColumn , cursor.getString(cursor.getColumnIndex(DBConstants.idColumn)));
                openEditScreenIntent.putExtra(DBConstants.ratingColumn , cursor.getString(cursor.getColumnIndex(DBConstants.ratingColumn)));
                startActivity(openEditScreenIntent);

                break;
        }
        return true;
    }



}
