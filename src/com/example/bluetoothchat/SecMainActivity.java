package com.example.bluetoothchat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;


public class SecMainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sec_activity_main);
    }
    public  void AsyncTask(View v){
        MyAsyncTask obj=new MyAsyncTask();
        obj.execute("ok start");
    }
    class MyAsyncTask extends AsyncTask <String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(),"Async Task started",Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... params) {
            int count = 0;
            for (; ; ) {
                System.out.println("Thread" + count++);
                if (count >1000) {
                    break;
                } else if (count == 2426) {
                    publishProgress(2426);
                }
            }
                return "Completed doInBackground()";


        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(),"Progress update",Toast.LENGTH_LONG).show();
            if(values[0]==2426){
                Toast.makeText(getApplicationContext(), "My Fav No", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(),"Async Task finished",Toast.LENGTH_LONG).show();
        }
    }
}

