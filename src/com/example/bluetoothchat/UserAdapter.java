package com.example.bluetoothchat;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class UserAdapter extends ArrayAdapter<UserModel>{

	UserModel[] usersList;
	Context myContext;

	public UserAdapter(Context context, UserModel[] usersList) {
		super(context, R.layout.chat_list_item, usersList);
		this.usersList=usersList;
		this.myContext=context;
	}

	// It gets a View that displays in the drop down popup the data at the specified position
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	// It gets a View that displays the data at the specified position
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}


	private View getCustomView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			LayoutInflater mLayoutInflater = LayoutInflater.from(myContext);
			convertView = mLayoutInflater.inflate(R.layout.chat_list_item, parent, false);
		}

		TextView username = (TextView) convertView.findViewById(R.id.username);
		TextView message = (TextView) convertView.findViewById(R.id.msg);
		TextView time = (TextView) convertView.findViewById(R.id.time);
		TextView count = (TextView) convertView.findViewById(R.id.count);
		//		ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView_photo);

		username.setText(usersList[position].getUsername());
		message.setText(usersList[position].getMessage());
		time.setText(usersList[position].getTime());
		count.setText(usersList[position].getCount()+"");



		return convertView;
	}
}
