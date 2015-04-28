package com.example.bluetoothchat;

public class UserModel {

	String username="";
	String message="";
	String time="";
	int count;

	public UserModel(String username,String message,String time,int count){
		this.username=username;
		this.message=message;
		this.time=time;
		this.count=count;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	//	public String getEmail() {
	//		return email;
	//	}
	//	public String getName() {
	//		return name;
	//	}
	//	public void setEmail(String email) {
	//		this.email = email;
	//	}
	//	public void setName(String name) {
	//		this.name = name;
	//	}
}