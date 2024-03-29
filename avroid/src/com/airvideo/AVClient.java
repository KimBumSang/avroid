package com.airvideo;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class AVClient {
	HttpURLConnection request;
	URL endpoint;
	String pwd;
	int max_w, max_h;
	
	AVClient(String server, int port, String password) {
		try {
			endpoint = new URL("http://" + server + ":" + port + "/service");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			request = (HttpURLConnection)endpoint.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.addRequestProperty("User-Agent", "AirVideo/2.2.4 CFNetwork/459 Darwin/10.0.0d3");
		request.addRequestProperty("Accept", "*/*");
		request.addRequestProperty("Accept-Language", "en-us");
		request.addRequestProperty("Accept-Encoding", "gzip, deflate");
		
		pwd = "/";
		max_w = 640;
		max_h = 480;
	}
	
	ArrayList ls(AVFolder dir) {
		ArrayList results = new ArrayList();
		String path = null; //dir.location;
		ArrayList paths = new ArrayList();
		paths.add(path);
		AVMap files = request("browseService","getItems",paths);
		try {
			HashMap o = (HashMap)files.get("result");
			o = (HashMap)o.get("items");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// files['result']['items'];
		return results;
	}
	
	void cd(AVFolder dir) {
		pwd = dir.location;
	}
	
	URL getUrl(AVVideo video, boolean live) {
		AVMap packet;
		if (live) {
			packet = request("livePlaybackService","initLivePlayback",conversionSettings(video));
			//['result']['contentURL'];
		} else {
			packet = request("playbackService","initPlayback",video.location);
			//['result']['contentURL']
		}
		return null;
	}
	
	void getDetails(ArrayList items) {
		AVMap a = request("browseService","getItemsWithDetail",items);
		//a['result'][0];
		
	}
	
	// search
	
	AVMap conversionSettings(AVVideo file) {
		double v_w = ((Integer)file.videoStream.get("width")).intValue();
		double v_h = ((Integer)file.videoStream.get("height")).intValue();
		int desired_width = (int)v_w;
		int desired_height = (int)v_h;

		// code to convert width, height to max_width, max_height
		AVMap settings = new AVMap();
		settings.name = "air.video.ConversionRequest";
		settings.put("itemId", file.location);
		settings.put("audioStram", 1);
		settings.put("allowedBitrates", BitRateList.defaults());
		settings.put("audioBoost", new Double(0.0));
		settings.put("cropRight", new Integer(0));
		settings.put("cropLeft", new Integer(0));
		settings.put("resolutionWidth", new Integer(desired_width));
		settings.put("videoStream", new Integer(0));
		settings.put("cropBottom", new Integer(0));
		settings.put("cropTop", new Integer(0));
		settings.put("quality", new Double(0.699999988079071));
		settings.put("subtitleInfo", null);
		settings.put("offset", new Double(0.0));
		settings.put("resolutionHeight", new Integer(desired_height));
		return settings;
	}
	
	AVMap request(String service, String method, Object params) {
		try {
			AVMap avrequest = new AVMap();
			request.setRequestMethod("POST");
			request.setDoInput(true);
			request.setDoOutput(true);
			OutputStream ost = request.getOutputStream();
			PrintWriter broadcaster = new PrintWriter(ost);
			avrequest.name = "air.connect.Request";
			avrequest.put("requestURL", this.endpoint.toString());
			avrequest.put("clientIdentifier", "89eae483355719f119d698e8d11e8b356525ecfb");
			avrequest.put("passwordDigest", "DA5353CC7A72F02A7BBA1ABEA1AC566882805B89");
			avrequest.put("clientVersion", 221);
			avrequest.put("serviceName", service);
			avrequest.put("methodName", method);
			avrequest.put("parameters", params);
			String rpc = avrequest.to_avmap(avrequest, true);
			broadcaster.print(rpc);
			broadcaster.flush();
			broadcaster.close();
			
			
			return AVMap.parse(request.getInputStream());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
