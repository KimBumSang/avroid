package com.airvideo;

import java.util.ArrayList;

public class AVFolder {
	AVClient server;
	String name;
	String location;
	
	AVFolder(AVClient server, String name, String location) {
		this.server = server;
		this.name = name;
		this.location = location;
	}
	
	void cd () {
		this.server.cd(this);
	}
	
	ArrayList<Object> ls () {
		return this.server.ls(this);
	}
}
