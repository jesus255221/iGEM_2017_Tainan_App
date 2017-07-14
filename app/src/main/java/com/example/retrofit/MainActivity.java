package com.example.retrofit;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> name = new ArrayList<String>();
    private ArrayList<String> glucose = new ArrayList<String>();

    public interface Service {
        @FormUrlEncoded
        @POST("/users")
        Call<JsonResponse> Create(
                @Field("name") String name,
                @Field("glucose") String glucose
        );

        @FormUrlEncoded
        @PUT("/users/{id}")
        Call<JsonResponse> Update(
                @Path("id") String id,
                @Field("name") String name,
                @Field("glucose") String glucose
        );
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //PostData();
                EditText editText1 = (EditText) findViewById(R.id.eidttext1);
                EditText editText2 = (EditText) findViewById(R.id.eidttext2);
                SQLiteOpenHelper sqLiteOpenHelper = new DatabaseHelper(MainActivity.this);
                try {
                    SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
                    DatabaseHelper.insertData(database,editText1.getText().toString(),Integer.parseInt(editText2.getText().toString()));
                    Cursor cursor = database.query("DATA",
                            new String[]{"_id","NAME"}, null, null, null, null, null);
                    CursorAdapter cursorAdapter = new SimpleCursorAdapter(
                            MainActivity.this,
                            android.R.layout.simple_list_item_1,
                            cursor,
                            new String[]{"NAME"},
                            new int[]{android.R.id.text1},
                            0
                    );
                    ListView listView = (ListView) findViewById(R.id.data);
                    listView.setAdapter(cursorAdapter);
                } catch (SQLiteException e) {
                    Toast toast = Toast.makeText(MainActivity.this, "DB failed", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        Button WebView_Button = (Button) findViewById(R.id.WebView_Button);
        WebView_Button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Url = "https://www.google.com";
                Intent intent = new Intent(MainActivity.this, WebView_Activity.class);
                intent.putExtra("Url", Url);
                startActivity(intent);
            }
        });
        SQLiteOpenHelper sqLiteOpenHelper = new DatabaseHelper(MainActivity.this);
        try {
            SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
            Cursor cursor = database.query("DATA",
                    new String[]{"_id","NAME"}, null, null, null, null, null);
            CursorAdapter cursorAdapter = new SimpleCursorAdapter(
                    MainActivity.this,
                    android.R.layout.simple_list_item_1,
                    cursor,
                    new String[]{"NAME"},
                    new int[]{android.R.id.text1},
                    0
            );
            ListView listView = (ListView) findViewById(R.id.data);
            listView.setAdapter(cursorAdapter);
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(MainActivity.this, "DB failed", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public boolean PostData() {
        EditText editText1 = (EditText) findViewById(R.id.eidttext1);
        EditText editText2 = (EditText) findViewById(R.id.eidttext2);
        name.add(editText1.getText().toString());
        glucose.add(editText2.getText().toString());
        if (isNetworkConnected()) {
            Retrofit retrofit = new Retrofit
                    .Builder()
                    .baseUrl("http://jia.ee.ncku.edu.tw")
                    .addConverterFactory(GsonConverterFactory.create()).build();
            Service service = retrofit.create(Service.class);
            for (int i = 0; i < this.name.size(); i++) {
                Call<JsonResponse> post = service.Create(name.get(i), glucose.get(i));
                post.enqueue(new Callback<JsonResponse>() {
                    @Override
                    public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                        TextView textView = (TextView) findViewById(R.id.textview);
                        textView.setText(response.body().getMessage());
                    }

                    @Override
                    public void onFailure(Call<JsonResponse> call, Throwable t) {
                        TextView textView = (TextView) findViewById(R.id.textview);
                        textView.setText(t.toString());
                    }
                });
            }
            /*Call<JsonResponse> put = service.Update("5953c40f86cd4e51735883af","Hi","777");
            put.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                TextView textView = (TextView)findViewById(R.id.textview2);
                textView.setText(response.body().getMessage());
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                TextView textView = (TextView)findViewById(R.id.textview2);
                textView.setText(t.toString());
            }
        });*/
            name.clear();
            glucose.clear();
            return true;
        } else {
            TextView textView = (TextView) findViewById(R.id.textview);
            textView.setText("No internet connection");
            return false;
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }
}

/*    private class Update extends AsyncTask<Integer,Void,CursorAdapter>{

        CursorAdapter cursorAdapter;

        protected CursorAdapter doInBackground(Integer... params){
            SQLiteOpenHelper sqLiteOpenHelper = new DatabaseHelper(MainActivity.this);
            try {
                SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
                database = sqLiteOpenHelper.getReadableDatabase();
                Cursor cursor = database.query("DATA",
                        new String[]{"NAME"},null,null,null,null,null);
                cursorAdapter = new SimpleCursorAdapter(
                        MainActivity.this,
                        android.R.layout.simple_list_item_1,
                        cursor,
                        new String[] {"NAME"},
                        new int[]{android.R.id.text1},
                        0
                );
                return cursorAdapter;
            }catch (SQLiteException e){
                Toast toast = Toast.makeText(MainActivity.this,"DB failed",Toast.LENGTH_LONG);
                toast.show();
                return null;
            }
        }
        protected void onPostExecute(CursorAdapter cursorAdapter){
            if(cursorAdapter != null){
                MainActivity mainActivity = (MainActivity) Activity;
                ListView listView = (ListView)findViewById(R.id.data);
            }
        }
    }

}
*/
