package com.airvideo;

import java.net.URL;
import java.util.HashMap;

public class AVVideo extends AVResource {
	Object details;
	HashMap <Object,Object> videoStream;
	HashMap <Object,Object> audioStream;
	
	AVVideo (AVClient server, String name, String location, Object detail) {
		this.server = server;
		this.name = name;
		this.location = "/" + location;
		this.details = detail;
		this.videoStream = new HashMap <Object,Object>();
		this.audioStream = new HashMap <Object,Object>();
		this.videoStream.put("index", new Integer(1));
		this.audioStream.put("index", new Integer(0));
	}
	
	Object details() {
		return this.details;
	}
	
	void audioStream(HashMap <Object,Object>stream) {
		@SuppressWarnings("unused")
		Integer index = (Integer)stream.get("index");
	}
	
	void videoStream(HashMap <Object,Object> stream) {
		@SuppressWarnings("unused")
		Integer index = (Integer)stream.get("index");
	}
	
	URL url () {
		try {
			return server.getUrl(this, false);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	URL live_url () {
		try {
			return server.getUrl(this, true);
		}
		catch (Exception e) {
			return null;
		}
	}
}
