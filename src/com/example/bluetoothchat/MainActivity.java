package com.example.bluetoothchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		//((ListView) findViewById(R.id.my_main_listview)).setAdapter((new UserAdapter(getApplicationContext(), createDummyUserModels())));
		setContentView(R.layout.activity_main_new);


	}

	public void search(View v)
	{
		Toast.makeText(getApplicationContext(), "Plustouched", Toast.LENGTH_SHORT).show();
		startActivity(new Intent(this,ChatActivity.class));

	}

	//	public UserModel[] createDummyUserModels(){
	//
	//
	//		String[] usern={"cap","watch","tv","mobile","bags", "shoes","books","spray","computer", "ac"};
	//		String[] msg={"yoyo","watch","tv","mobile","bags", "shoes","books","spray","computer", "ac"};
	//		String[] time={"1.0","5.00","4.00","6.00","8.00", "10.00","2.00","11.00","12.00", "5.05"};
	//		int[] count={1,10,4,3,1, 2,1,6,1,5};
	//
	//		UserModel[] userlist=new UserModel[count.length];
	//		for (int i = 0; i < count.length; i++) {
	//			userlist[i]=new UserModel((usern[i]),msg[i],(time[i]),(count[i]));
	//		}
	//		return userlist;
	//
	//	}
}