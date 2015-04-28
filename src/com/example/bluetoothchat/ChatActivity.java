
package com.example.bluetoothchat;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;



public class ChatActivity extends ActionBarActivity  {


	int PHOTO_CAMERA_REQUEST=1;
	int PHOTO_GALLERY_REQUEST=2;
	BluetoothChatFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frag);


		if (savedInstanceState == null) {
			fragment = new BluetoothChatFragment();
			getSupportFragmentManager().beginTransaction().replace(R.id.sample_content_fragment, fragment).commit();
		}



	}


}



//	public void attach(View v) {
//
//		new AlertDialog.Builder(ChatActivity.this )
//		.setTitle("Camera Photo")
//		.setMessage("Select This Image for Sending?")
//		.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int whichButton) {
//				// go to camera Activity
//				startActivityForResult(new Intent(getApplicationContext(), CameraActivity.class), 1);
//			}
//		}).setNegativeButton("Gallery", 	new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int whichButton) {	
//				//Go to Gallery choose
//				Intent intent = new Intent();
//				intent.setType("image/*");
//				intent.setAction(Intent.ACTION_GET_CONTENT);
//				startActivityForResult(Intent.createChooser(intent, "Choose for sending Image"), 2);
//			}
//		}).show();
//
//
//	}
//
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (resultCode == RESULT_OK) {
//			File userImageFile=new File(Environment.getExternalStorageDirectory(),""+new Date().getSeconds()+".JPG");//CameraActivity.scaledData
//			if(!userImageFile.exists()){
//				try {
//					userImageFile.createNewFile();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			
//			//PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("userimagepath", userImageFile.getAbsolutePath()).commit();
//
//			System.out.println("Result OK Got");
//
//			//Edit Anand From
////
////			mConversationArrayAdapter = new ArrayAdapter<ImageView>(getApplicationContext(), R.layout.image_message);
////			mConversationView = (ListView)findViewById(R.id.in);
////			mConversationView.setAdapter(mConversationArrayAdapter);
////			
////			BluetoothChatFragment 	bf=new BluetoothChatFragment();
////			bf.sendMessage(userImageFile);
//			
//			//until here
//
//			profileImageView=(ImageView) findViewById(R.id.take_photo);
//			//profileImageView.setPlaceholder(getResources().getDrawable(R.drawable.ic_launcher));
//
//			  
//			
//			if (requestCode == 1) {
//				try {
//
//
//					FileOutputStream fos = new FileOutputStream(userImageFile);
//					fos.write(CameraActivity.scaledData);
//					fos.flush();
//					fos.close();
//
//					profileImageView.setImageBitmap(new BitmapDrawable(userImageFile.getPath()).getBitmap());
//
//					System.out.println("Image Set from camera");
//				} catch (Exception e) {
//					System.out.println("Exception from Register Activity Result - From Camera");
//					e.printStackTrace();
//				}
//			}else if (requestCode == 2) {
//				try {
//					UserPicture usrPic=new UserPicture(data.getData(), getContentResolver());
//
//					//write data to file
//					FileOutputStream fos = new FileOutputStream(userImageFile);
//					usrPic.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, fos);
//					fos.flush();
//					fos.close();
//
//					profileImageView.setImageBitmap(usrPic.getBitmap());
//					System.out.println("Image Set from Gallery");
//
//				} catch (Exception e) {
//					System.out.println("Exception from Register Activity Result - From Gallery");
//					e.printStackTrace();
//				}
//			} else{
//				System.out.println("Invalid request Code");
//			}
//		}
