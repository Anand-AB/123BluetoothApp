package com.example.bluetoothchat;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

	private static final int port = 8089;

	private Socket socket;

	public static void main(String[] args) {
		ServerSocket listener=null;
		try {
			listener= new ServerSocket(port);

			while (true) {
				System.out.println("Server Ready");
				Server file_rec = new Server();
				file_rec.socket = listener.accept();

				new Thread(file_rec).start();
			}
		} catch (java.lang.Exception ex) {
			if(listener!=null)
				try {
					listener.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			ex.printStackTrace();
		}
	}

	public void run() {
		try {
			System.out.println("Server Receiving start");

			InputStream in = socket.getInputStream();

			int nof_files = ByteStream.toInt(in);
			System.out.println("No of files - "+nof_files);

			String file_name = ByteStream.toString(in);

			System.out.println("Filename - "+file_name);

			File file = new File("/home/imrokraft/Desktop/Android Trainees/Anand/images/" +file_name);

			if(! file.exists()){
				file.createNewFile();
			}

			ByteStream.toFile(in, file);

			in.close();
			socket.close();
		} catch (java.lang.Exception ex) {
			ex.printStackTrace(System.out);
		}
	}
}