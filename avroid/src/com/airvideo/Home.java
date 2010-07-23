package com.airvideo;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
//import android.widget.TextView;
import android.widget.ListView;

public class Home extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView lvw = new ListView(this);
        setContentView(lvw);
        
        AVClient server = new AVClient("192.168.1.105", 45631, "");
        @SuppressWarnings("unused")
        AVFolder root = new AVFolder(server, "root", "/");
        ArrayList <Object> items = server.ls(root);
    }
}