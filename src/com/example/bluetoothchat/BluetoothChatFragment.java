
package com.example.bluetoothchat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothChatFragment extends Fragment {

	private static final String TAG = "BluetoothChatFragment";
	public static Intent data;
	public static boolean secure;

	private byte[] buffer = new byte[8192];
	//private ImageView image;
	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_IMAGE=2;
	private static final int REQUEST_ENABLE_BT = 3;
	public final int REQUEST_ENABLE_BT_CONST=1;
	boolean bluetoothEnabled=false;

	private BluetoothDevice device = null;
	public static BluetoothSocket socket = null;

	// Layout Views
	private ListView mConversationView;
	private EditText mOutEditText;
	private ImageButton mSendButton;
	private ImageButton mAtachButton;


	public static File fileToSend=null;

	public FileOutputStream fileOuputStream;

	/**
	 * Name of the connected device
	 */
	private String mConnectedDeviceName = null;

	ImageView image;
	/**
	 * Array adapter for the conversation thread
	 */
	private ArrayAdapter<String> mConversationArrayAdapter;
	private ArrayAdapter<ImageView> mConversationArrayAdapter1;

	/**
	 * String buffer for outgoing messages
	 */
	private StringBuffer mOutStringBuffer;

	/**
	 * Local Bluetooth adapter
	 */
	private BluetoothAdapter mBluetoothAdapter = null;

	/**
	 * Member object for the chat services
	 */
	private BluetoothChatService mChatService = null;

	public static int state=0;

	@Override
	public void onCreate(Bundle savedInstanceState) { 


		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);


		new AlertDialog.Builder(getActivity())
		.setMessage("Connect A Device.")
		.setCancelable(false)
		.setPositiveButton("Connect A Device", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Launch the DeviceListActivity to see devices and do scan
				Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
			}
		})
		.setNegativeButton("Turn On/Off Bluetooth", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				checkBluetoothStatus();

			}
		})
		.create()
		.show();

		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			FragmentActivity activity = getActivity();
			Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			activity.finish();
		}
		else{
			bluetoothEnabled=true;
		}


	}


	@Override
	public void onStart() {
		super.onStart();
		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else if (mChatService == null) {
			setupChat();
		}
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mChatService != null) {
			mChatService.stop();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't started already
			if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
				// Start the Bluetooth chat services
				mChatService.start();
			}
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_chatlist_new, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		mConversationView = (ListView) view.findViewById(R.id.in);
		mOutEditText = (EditText) view.findViewById(R.id.edit_text_out);
		mSendButton = (ImageButton) view.findViewById(R.id.button_send);
		mAtachButton = (ImageButton) view.findViewById(R.id.button_attach);
		image = (ImageView) view.findViewById(R.id.imageView1);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode==REQUEST_CONNECT_DEVICE_SECURE && resultCode == Activity.RESULT_OK) {
			connectDevice(data, true);
		}

		if (requestCode==REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {


			String filepath = fileToSend.getAbsolutePath();
			File imagefile = new File(filepath);
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(imagefile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			BluetoothSocket btSocket = null;
			OutputStream outStream = null;
			UUID MY_UUID=UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
			try {
				btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			}

			try {
				outStream = btSocket.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Bitmap bm = BitmapFactory.decodeStream(fis);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			bm.compress(Bitmap.CompressFormat.JPEG, 100 , baos);    
			byte[] b = baos.toByteArray(); 
			Toast.makeText(getActivity(), String.valueOf(b.length), Toast.LENGTH_SHORT).show();
			try {
				outStream.write(b);
				outStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sendImage(b);

			//String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
		}


	}

	private void checkBluetoothStatus(){
		FragmentActivity activity = getActivity();
		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
			Toast.makeText(activity, "No Bluetooth found",Toast.LENGTH_SHORT).show();
			bluetoothEnabled=false;
		}
		else if (!mBluetoothAdapter.isEnabled()) {
			startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT_CONST);
			Toast.makeText(activity, "Please Turn on Bluetooth", Toast.LENGTH_LONG).show();

		}
		else if(mBluetoothAdapter!=null && mBluetoothAdapter.isEnabled()){
			Toast.makeText(activity, "Turning off Bluetooth", Toast.LENGTH_LONG).show();
			mBluetoothAdapter.disable();
		}
	}

	/**
	 * Set up the UI and background operations for chat.
	 */
	public  void setupChat() {
		Log.d(TAG, "setupChat()");

		mConversationArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);
		mConversationArrayAdapter1=new ArrayAdapter<ImageView>(getActivity(), R.layout.imageview);

		//		mConversationView.setAdapter(mConversationArrayAdapter);
		//		mConversationView.setAdapter(mConversationArrayAdapter1);

		// Initialize the compose field with a listener for the return key
		mOutEditText.setOnEditorActionListener(mWriteListener);

		// Initialize the send button with a listener that for click events
		mSendButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Send a message using content of the edit text widget
				View view = getView();
				if (null != view) {
					TextView textView = (TextView) view.findViewById(R.id.edit_text_out);
					String message = textView.getText().toString();
					sendMessage(message);
				}
			}
		});


		//Anand
		mAtachButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				// Launch the DeviceListActivity to see devices and do scan
				Intent serverIntent = new Intent(getActivity(), ChooseImageActivity.class);
				startActivityForResult(serverIntent, REQUEST_IMAGE);

				//startActivity(new Intent(getActivity(),ChooseImageActivity.class));
			}
		});

		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new BluetoothChatService(getActivity(), mHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");
	}

	/**
	 * Sends a message.
	 *
	 * @param message A string of text to send.
	 */


	public void sendMessage(String message) {
		state=0;

		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(getActivity(),"Not Connected", Toast.LENGTH_SHORT).show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mChatService.write(send);

			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);
			mOutEditText.setText(mOutStringBuffer);
		}
	}

	public void sendImage(byte[] send)
	{
		mChatService.write(send);

		// Reset out string buffer to zero and clear the edit text field
		mOutStringBuffer.setLength(0);
		mOutEditText.setText(mOutStringBuffer);
	}

	class MyAsyncTask extends AsyncTask <OutputStream, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//Toast.makeText(getApplicationContext(),"Async Task started",Toast.LENGTH_LONG).show();
		}

		@Override
		protected String doInBackground(OutputStream... params) {

			OutputStream outStream = null;

			try {
				mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bm.compress(Bitmap.CompressFormat.JPEG, 100,baos); //bm is the bitmap object
				byte[] b = baos.toByteArray();
				Toast.makeText(getActivity(), String.valueOf(b.length), Toast.LENGTH_SHORT).show();
				outStream.write(b);
				outStream.flush();
			} catch (IOException e) {
				return "exception";
			}
			return "finished";
		}

		@Override
		protected void onPostExecute(String s) {

			super.onPostExecute(s);
			//Toast.makeText(getApplicationContext(),"Async Task finished",Toast.LENGTH_LONG).show();
		}
	}

	//Anand

	//	public void MyServerTask (){
	//		BluetoothServerSocket tmp = null;
	//		BluetoothSocket socket = null;
	//		try {
	//			tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Bluetooth", UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66"));
	//		} catch (IOException e) {
	//			//
	//		}
	//		try {
	//			socket = tmp.accept();
	//		} catch (IOException e) {
	//			//
	//		}
	//		String device = socket.getRemoteDevice().getName();
	//		InputStream tmpIn = null;
	//		try {
	//			tmpIn = socket.getInputStream();
	//		} catch (IOException e) {
	//			//
	//		}
	//		InputStream mmInStream = tmpIn;
	//		int byteNo;
	//		byte[] buffer = new byte[8192];
	//		try {
	//			byteNo = mmInStream.read(buffer);
	//			if (byteNo != -1) {
	//				//ensure DATAMAXSIZE Byte is read.
	//				int byteNo2 = byteNo;
	//				int bufferSize = 7340;
	//				while(byteNo2 != bufferSize){
	//					bufferSize = bufferSize - byteNo2;
	//					byteNo2 = mmInStream.read(buffer,byteNo,bufferSize);
	//					if(byteNo2 == -1){
	//						break;
	//					}
	//					byteNo = byteNo+byteNo2;
	//				}
	//			}
	//			if (socket != null) {
	//				try {
	//					tmp.close();
	//				} catch (IOException e) {
	//					//
	//				}
	//			}
	//		}
	//		catch (Exception e) {
	//
	//			// TODO: handle exception
	//		}
	//	}



	//Anand




	/**
	 * The action listener for the EditText widget, to listen for the return key
	 */
	private TextView.OnEditorActionListener mWriteListener
	= new TextView.OnEditorActionListener() {
		public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
			// If the action is a key-up event on the return key, send the message
			if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
				String message = view.getText().toString();
				sendMessage(message);
			}
			return true;
		}
	};

	/**
		public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
			// If the action is a key-up event on the return key, send the message
			if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
				String message = view.getText().toString();
				sendMessage(message);
			}
			return true;
		}

	 * Updates the status on the aMessagection bar.
	 *
	 * @param resId a string resource ID
	 */
	private void setStatus(int resId) {
		FragmentActivity activity = getActivity();
		if (null == activity) {
			return;
		}
		final ActionBar actionBar = activity.getActionBar();
		if (null == actionBar) {
			return;
		}
		actionBar.setSubtitle(resId);
	}

	/**
	 * Updates the status on the action bar.
	 *
	 * @param subTitle status
	 */
	private void setStatus(CharSequence subTitle) {
		FragmentActivity activity = getActivity();
		if (null == activity) {
			return;
		}
		final ActionBar actionBar = activity.getActionBar();
		if (null == actionBar) {
			return;
		}
		actionBar.setSubtitle(subTitle);
	}

	/**
	 * The Handler that gets information back from the BluetoothChatService
	 */


	public final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			FragmentActivity activity = getActivity();
			switch (msg.what) {
			case Constants.MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					setStatus("connected to"+ mConnectedDeviceName);
					mConversationArrayAdapter.clear();
					mConversationArrayAdapter1.clear();
					break;
				case BluetoothChatService.STATE_CONNECTING:
					setStatus("Connecting");
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					setStatus("Not connected");
					break;
				}
				break;
			case Constants.MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				if(state==0)
				{
					mConversationView.setAdapter(mConversationArrayAdapter);
					String writeMessage = new String(writeBuf);
					mConversationArrayAdapter.add("Me:  " + writeMessage);
				}

				if(state==1)
				{

					//					Bitmap bitmap = BitmapFactory.decodeByteArray(writeBuf, 0,writeBuf.length);
					//					// Find the SD Card path
					//					File filepath = Environment.getExternalStorageDirectory();
					//		 
					//					// Create a new folder in SD Card
					//					File dir = new File(filepath.getAbsolutePath()
					//							+ "/Save Image Tutorial/");
					//					dir.mkdirs();

					// Create a name for the saved image
					File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/AnandFolder/", "myimage.png");
					if(!file.exists()){
						//file.mkdirs();
						try {
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					// Show a toast message on successful save
					Toast.makeText(getActivity(), "myImage Saved to SD Card\n"+file.getAbsolutePath(),
							Toast.LENGTH_SHORT).show();
					try {

						FileOutputStream output = new FileOutputStream(file);

						// Compress into png format image from 0% - 100%
						//						bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
						output.write(writeBuf);
						output.flush();
						output.close();
					}

					catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					//Anand..........................
					//					mConversationView.setAdapter(mConversationArrayAdapter1);
					//					//convert array of bytes into file
					//
					//					File userImageFile=new File(Environment.getExternalStorageDirectory(),""+new Date().getSeconds()+".JPG");//CameraActivity.scaledData
					//					if(!userImageFile.exists()){
					//						try {
					//							userImageFile.createNewFile();
					//						} catch (IOException e) {
					//							// TODO Auto-generated catch block
					//							e.printStackTrace();
					//						}
					//						FileOutputStream fileOuputStream = null;
					//						try {
					//							fileOuputStream = new FileOutputStream(userImageFile);
					//						} catch (FileNotFoundException e1) {
					//							// TODO Auto-generated catch block
					//							e1.printStackTrace();
					//						} 
					//						try {
					//							fileOuputStream.write(writeBuf);
					//							fileOuputStream.close();
					//						} catch (IOException e) {
					//							// TODO Auto-generated catch block
					//							e.printStackTrace();
					//						}
					//
					//						//Bitmap bmp = BitmapFactory.decodeByteArray(writeBuf, 0, writeBuf.length);
					//						//						View v = new ImageView(getActivity());
					//						//						 ImageView image; 
					//						//						 image = new ImageView(v.getContext()); 
					//						//						//image.setImageBitmap(bmp);
					//						
					//						imageset(userImageFile);
					////						image.setImageBitmap(new BitmapDrawable(userImageFile.getPath()).getBitmap());
					////
					////						Toast.makeText(getActivity(),userImageFile.getAbsolutePath(),Toast.LENGTH_LONG).show();
					//
					//						//mConversationArrayAdapter1.add(image);
					//					}
				}

				break;
			case Constants.MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				if(state==0)
				{
					mConversationView.setAdapter(mConversationArrayAdapter);
					String readMessage = new String(readBuf, 0, msg.arg1);
					mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
				}
				if(state==1)
				{
					File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/AnandFolder/", "myimage.png");
					if(!file.exists()){
						//file.mkdirs();
						try {
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					// Show a toast message on successful save
					Toast.makeText(getActivity(), "myImage Saved to SD Card\n"+file.getAbsolutePath(),
							Toast.LENGTH_SHORT).show();
					try {

						FileOutputStream output = new FileOutputStream(file);

						// Compress into png format image from 0% - 100%
						//						bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
						output.write(readBuf);
						output.flush();
						output.close();
					}

					catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


					//////
					//					Bitmap bitmap = BitmapFactory.decodeByteArray(readBuf, 0,readBuf.length);
					//					// Find the SD Card path
					//					File filepath = Environment.getExternalStorageDirectory();
					//
					//					// Create a new folder in SD Card
					//					File dir = new File(filepath.getAbsolutePath()
					//							+ "/Save Image Tutorial/");
					//					dir.mkdirs();
					//
					//					// Create a name for the saved image
					//					File file1 = new File(dir, "myimage.png");
					//
					//					// Show a toast message on successful save
					//					Toast.makeText(getActivity(), "myImage Saved to SD Card",
					//							Toast.LENGTH_SHORT).show();
					//					try {
					//
					//						FileOutputStream output = new FileOutputStream(file1);
					//
					//						// Compress into png format image from 0% - 100%
					//						bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
					//						output.flush();
					//						output.close();
					//					}
					//
					//					catch (Exception e) {
					//						// TODO Auto-generated catch block
					//						e.printStackTrace();
					//					}


					//					mConversationView.setAdapter(mConversationArrayAdapter1);
					//					File userImageFile=new File(Environment.getExternalStorageDirectory(),""+new Date().getSeconds()+".JPG");//CameraActivity.scaledData
					//					if(!userImageFile.exists()){
					//						try {
					//							userImageFile.createNewFile();
					//						} catch (IOException e) {
					//							// TODO Auto-generated catch block
					//							e.printStackTrace();
					//						}
					//						FileOutputStream fileOuputStream = null;
					//						try {
					//							fileOuputStream = new FileOutputStream(userImageFile);
					//						} catch (FileNotFoundException e1) {
					//							// TODO Auto-generated catch block
					//							e1.printStackTrace();
					//						} 
					//						try {
					//							fileOuputStream.write(readBuf);
					//							fileOuputStream.close();
					//						} catch (IOException e) {
					//							// TODO Auto-generated catch block
					//							e.printStackTrace();
					//						}
					//						
					//						
					//
					//						//Bitmap rbmp = BitmapFactory.decodeByteArray(readBuf, 0,readBuf.length);
					//						//						View v = new ImageView(getActivity());
					//						//						 ImageView rimage; 
					//						// image = new ImageView(v.getContext());  
					//						imageset(userImageFile);
					//						//						Toast.makeText(getActivity(),userImageFile.getAbsolutePath(),Toast.LENGTH_LONG).show();
					//						//						
					//						//						image.setImageBitmap(new BitmapDrawable(userImageFile.getPath()).getBitmap());
					//						//mConversationArrayAdapter.add("mConnectedDeviceName:  " + rimage);
					//						//mConversationArrayAdapter.add(mConnectedDeviceName + ":  " );
					//						//mConversationArrayAdapter1.add(rimage);
					//					}
				}
				break;
			case Constants.MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
				if (null != activity) {
					Toast.makeText(activity, "Connected to "
							+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
				}
				break;
			case Constants.MESSAGE_TOAST:
				if (null != activity) {
					Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
							Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};

	/**
	 * Establish connection with other divice
	 *
	 * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
	 * @param secure Socket Security type - Secure (true) , Insecure (false)
	 */


	public void connectDevice(Intent data, boolean secure) {
		// Get the device MAC address
		String address = data.getExtras()
				.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device, secure);
	}


	public void imageset(File f1)
	{
		Toast.makeText(getActivity(),f1.getAbsolutePath(),Toast.LENGTH_LONG).show();

		image.setImageBitmap(new BitmapDrawable(f1.getPath()).getBitmap());
	}
}
