package com.example.bluetoothchat;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class SearchActivity extends SampleActivityBase {

	private static final int REQUEST_ENABLE_BT = 3;

	private static final String TAG = "SearchActivity";

	/**
	 * Array adapter for the conversation thread
	 */
	private ArrayAdapter<String> mConversationArrayAdapter;

	/**
	 * Name of the connected device
	 */
	private String mConnectedDeviceName = null;

	private BluetoothChatService mChatService =null;


	BluetoothChatFragment bf=new BluetoothChatFragment();

	public final int REQUEST_ENABLE_BT_CONST=1;
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	BroadcastReceiver mReceiver ;
	BluetoothAdapter mBluetoothAdapter=null;
	boolean bluetoothEnabled=false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBluetoothAdapter == null ){
			//bluetoothEnabled=true;
			Toast.makeText(getApplicationContext(), "Bluetooth is not Available", Toast.LENGTH_LONG).show();
		}
		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else if (mChatService == null) {
			bf.setupChat();

		}



	}

	//	@Override
	//	public void onStart() {
	//		super.onStart();
	//		// If BT is not on, request that it be enabled.
	//		// setupChat() will then be called during onActivityResult
	//		if (!mBluetoothAdapter.isEnabled()) {
	//			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	//			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
	//			// Otherwise, setup the chat session
	//		} else if (mChatService == null) {
	//			bf.setupChat();
	//		}
	//	}

	/**
	 * Updates the status on the action bar.
	 *
	 * @param subTitle status
	 */
	private void setStatus(CharSequence subTitle) {
		final ActionBar actionBar=getActionBar();
		//        FragmentActivity activity = getActivity();
		//        if (null == activity) {
		//            return;
		//        }
		//        final ActionBar actionBar = activity.getActionBar();
		//        if (null == actionBar) {
		//            return;
		//        }
		actionBar.setSubtitle(subTitle);
	}

	//	/**
	//	 * Set up the UI and background operations for chat.
	//	 */
	//	private void setupChat() {
	//		Log.d(TAG, "setupChat()");
	//
	//		// Initialize the array adapter for the conversation thread
	//		mConversationArrayAdapter = new ArrayAdapter<String>(ChatActivity(), R.layout.message);
	//
	//		mConversationView.setAdapter(mConversationArrayAdapter);
	//
	//		// Initialize the compose field with a listener for the return key
	//		mOutEditText.setOnEditorActionListener(mWriteListener);
	//
	//		// Initialize the send button with a listener that for click events
	//		mSendButton.setOnClickListener(new View.OnClickListener() {
	//			public void onClick(View v) {
	//				// Send a message using content of the edit text widget
	//				View view = getView();
	//				if (null != view) {
	//					TextView textView = (TextView) view.findViewById(R.id.edit_text_out);
	//					String message = textView.getText().toString();
	//					sendMessage(message);
	//				}
	//			}
	//		});
	//
	//		// Initialize the BluetoothChatService to perform bluetooth connections
	//		mChatService = new BluetoothChatService(getActivity(), mHandler);
	//
	//		// Initialize the buffer for outgoing messages
	//		mOutStringBuffer = new StringBuffer("");
	//	}

	/**
	 * The Handler that gets information back from the BluetoothChatService
	 */
	public  final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//FragmentActivity activity = getActivity();
			switch (msg.what) {
			case Constants.MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
					mConversationArrayAdapter.clear();
					break;
				case BluetoothChatService.STATE_CONNECTING:
					setStatus("Connecting...");
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					setStatus("not connected");
					break;
				}
				break;
			case Constants.MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				mConversationArrayAdapter.add("Me:  " + writeMessage);
				break;
			case Constants.MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
				break;
			case Constants.MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
				if (null != getApplicationContext()) {
					Toast.makeText(getApplicationContext(), "Connected to "
							+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
				}
				break;
			case Constants.MESSAGE_TOAST:
				if (null != getApplicationContext()) {
					Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.TOAST),
							Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};


	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_SECURE:

			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data, true);				
				//	startActivity(new Intent(this,ChatActivity.class));
			}
			break;
			//		case REQUEST_CONNECT_DEVICE_INSECURE:
			//			// When DeviceListActivity returns with a device to connect
			//			if (resultCode == Activity.RESULT_OK) {
			//				connectDevice(data, false);
			//			}
			//			break;
			//		case REQUEST_ENABLE_BT:
			//			// When the request to enable Bluetooth returns
			//			if (resultCode == Activity.RESULT_OK) {
			//				// Bluetooth is now enabled, so set up a chat session
			//				setupChat();
			//			} else {
			//				// User did not enable Bluetooth or an error occurred
			//				Log.d(TAG, "BT not enabled");
			//				Toast.makeText(getApplicationContext(), R.string.bt_not_enabled_leaving,
			//						Toast.LENGTH_SHORT).show();
			//				//getApplicationContext().finish();
			//			}
		}
	}

	private void connectDevice(Intent data, boolean secure) {
		// Get the device MAC address
		String address = data.getExtras()
				.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device, secure);
	}




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//FragmentActivity activity = getActivity();
		switch ( item.getItemId()) {
		case R.id.action_list_pired_devices:
			Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
			startActivityForResult(serverIntent,REQUEST_CONNECT_DEVICE_SECURE);
			return true;

			//		case R.id.action_scan_devices:
			//			Toast.makeText(activity, "You pressed Scan",Toast.LENGTH_SHORT).show();
			//			scanBluetoothDevices();
			//			doDiscovery();
			//			return true;

		case R.id.action_turn_on:
			Toast.makeText(getApplicationContext(), "You pressed Turn On/Off",Toast.LENGTH_SHORT).show();
			checkBluetoothStatus();
			return true;

		default:
			break;
		}
		return true;
	}

	private void checkBluetoothStatus(){
		//FragmentActivity activity = getActivity();
		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
			Toast.makeText(getApplicationContext(), "No Bluetooth found",Toast.LENGTH_SHORT).show();
			bluetoothEnabled=false;
		}
		else if (!mBluetoothAdapter.isEnabled()) {
			startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT_CONST);
			Toast.makeText(getApplicationContext(), "Please Turn on Bluetooth", Toast.LENGTH_LONG).show();

		}
		else if(mBluetoothAdapter!=null && mBluetoothAdapter.isEnabled()){
			Toast.makeText(getApplicationContext(), "Turning off Bluetooth", Toast.LENGTH_LONG).show();
			mBluetoothAdapter.disable();
		}
	}










}



//		String prodts=(PreferenceManager.getDefaultSharedPreferences(getApplicationContext())).getString("productlist", "");
//		StringTokenizer stk=new StringTokenizer(prodts, "|");
//		StringTokenizer stk2=null;
//		int no_of_prdts = stk.countTokens();
//		String[] ids = new String[no_of_prdts];
//		String[] pnames = new String[no_of_prdts];
//		String[] qtys = new String[no_of_prdts];
//		String[] prices = new String[no_of_prdts];
//		for (int i = 0; i < no_of_prdts; i++) {
//			stk2=new StringTokenizer(stk.nextToken(), ",");
//			ids[i]=((stk2.nextToken()));
//
//
//			pnames[i]=(stk2.nextToken());
//			qtys[i]=((stk2.nextToken()));
//			prices[i]=((stk2.nextToken()));




//				phone=(stk2.nextToken());
//				photo=(stk2.nextToken());
//				if(usename.equalsIgnoreCase(username_entered) && passw.equals(password_entered)){
//					loginStatus=true;
//					Editor e=(PreferenceManager.getDefaultSharedPreferences(getApplicationContext())).edit();
//					e.putString("username", username_entered);
//					e.putString("password", password_entered);
//					e.putString("name", stk2.nextToken());
//					e.putString("email", stk2.nextToken());
//					e.putLong("phone",Long.parseLong(stk2.nextToken()));
//					e.commit();
//					break;