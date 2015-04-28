package com.example.bluetoothchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;


public class MainActivitys extends ActionBarActivity {
	int count=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	public  void FirstClick(View v){
		Toast.makeText(getApplicationContext(),"first click",Toast.LENGTH_LONG).show();
		startActivity(new Intent(this,SecMainActivity.class));
	}
	public  void TwoClick(View v){
		Thread th=new Thread(new Runnable() {
			@Override
			public void run() {
				for (; ; ) {
					System.out.println("Thread" + count++);
					if (count > 1000) {
						break;
					}

				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getApplicationContext(), "Finished the loops", Toast.LENGTH_LONG).show();
					}
				});
			}
		});
		th.start();

	}
}
