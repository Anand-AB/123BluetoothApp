package com.example.bluetoothchat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;

public class ChooseImageActivity extends Activity {
	public static byte[] scaledData;
	private static int rotationAngle=0;
	private Camera camera;
	private boolean frontCamera=true;
	private SurfaceHolder holder;
	private byte[] imageDataArray;
	private ProgressDialog pd = null;
	private boolean inPreview=false;
	private int currentCameraId;

	private File userImageFile;
	ImageView profileImageView;
	public static int state;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_preview);

		new AlertDialog.Builder(ChooseImageActivity.this)
		.setTitle("Camera Photo")
		.setMessage("Choose Image as Profile Photo?")
		.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// go to camera Activity
				startActivityForResult(new Intent(ChooseImageActivity.this, CameraActivity.class), 1);
			}
		}).setNegativeButton("Gallery", 	new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {	
				//Go to Gallery choose
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Choose Profile Image"), 2);
			}
		}).show();

	}



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			userImageFile=new File(Environment.getExternalStorageDirectory(),""+new Date().getSeconds()+".JPG");//CameraActivity.scaledData
			if(!userImageFile.exists()){
				try {
					userImageFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("userimagepath", userImageFile.getAbsolutePath()).commit();

			System.out.println("Result OK Got");
			profileImageView=(ImageView) findViewById(R.id.registration_userImage);
			//profileImageView.setPlaceholder(getResources().getDrawable(R.drawable.ic_launcher));

			if (requestCode == 1) {
				try {


					FileOutputStream fos = new FileOutputStream(userImageFile);
					fos.write(CameraActivity.scaledData);
					fos.flush();
					fos.close();

					profileImageView.setImageBitmap(new BitmapDrawable(userImageFile.getPath()).getBitmap());

					System.out.println("Image Set from camera");
				} catch (Exception e) {
					System.out.println("Excehippoption from Register Activity Result - From Camera");
					e.printStackTrace();
				}
			}else if (requestCode == 2) {
				try {
					UserPicture usrPic=new UserPicture(data.getData(), getContentResolver());

					//write data to file
					FileOutputStream fos = new FileOutputStream(userImageFile);
					usrPic.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, fos);
					fos.flush();
					fos.close();

					profileImageView.setImageBitmap(usrPic.getBitmap());
					System.out.println("Image Set from Gallery");

				} catch (Exception e) {
					System.out.println("Exception from Register Activity Result - From Gallery");
					e.printStackTrace();
				}
			} else{
				System.out.println("Invalid request Code");
			}
		}
	}

	public void cancel(View v)
	{
		finish();
	}

	public void done(View v) throws IOException
	{
		
		BluetoothChatFragment.state=1;
		BluetoothChatFragment.fileToSend=userImageFile;
		setResult(RESULT_OK); 
		//setResult(Activity.RESULT_OK, intent);
		finish(); 
		
		//		try {
		//			reader= new BufferedReader( new FileReader(userImageFile));
		//			    String         line = null;
		//			    StringBuilder  stringBuilder = new StringBuilder();
		//			    String         ls = System.getProperty("line.separator");
		//
		//			    while( ( line = reader.readLine() ) != null ) {
		//			        stringBuilder.append( line );
		//			        stringBuilder.append( ls );
		//			    }
		//
		//			    bf.sendImage(stringBuilder.toString());
		//			    
		//			//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//			//			bm.compress(Bitmap.CompressFormat.JPEG, 100,baos); //bm is the bitmap object
		//			//			byte[] b = baos.toByteArray();
		//			//			Toast.makeText(getBaseContext(), String.valueOf(b.length), Toast.LENGTH_SHORT).show();
		//			//			outStream.write(b);
		//			//			outStream.flush();
		//		} catch (FileNotFoundException e) {
		//			e.printStackTrace();
		//		}
		//		Bitmap bm=BitmapFactory.decodeStream(fis);
		//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//		bm.compress(Bitmap.CompressFormat.JPEG, 100,baos); //bm is the bitmap object
		//		baos.flush();
		//		byte[] b = baos.toByteArray();
		//		BluetoothChatFragment bf= new BluetoothChatFragment();
		//		baos.write(b);
		//		
		//		Toast.makeText(getBaseContext(), String.valueOf(b.length), Toast.LENGTH_SHORT).show();
	}

}