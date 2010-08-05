package com.airvideo;

import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Home extends ListActivity {
	private AVFolderListAdapter _adapter;
	private Runnable viewContents;
	private AVClient server;
	private ArrayList <AVResource> items ;
	private AVFolder pwd;
	private ProgressDialog _ProgressDialog = null;
	private Activity activity;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.activity = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.main);
		getListView().setOnItemClickListener(listlistener);

		server = new AVClient("192.168.1.105", 45631, "");
		pwd = new AVFolder(server, "root", null);
		items = new ArrayList <AVResource>();

		this._adapter = new AVFolderListAdapter(this, R.layout.row, items);
		setListAdapter(this._adapter);

		viewContents = new Runnable() {
			public void run() {
				getContents();
			}
		};
		Thread thread =  new Thread(null, viewContents, "Interrogation");
		thread.start();
		_ProgressDialog = ProgressDialog.show(this, "Communication Status", "Interrogating AirVideoServer", true);
	}


	private void getContents() {
		items = server.ls(pwd);
		runOnUiThread(returnRes);
	}
	private Runnable returnRes = new Runnable() {
		public void run() {
			if(items != null && items.size() > 0){
				_adapter.notifyDataSetChanged();
				_adapter.clear();
				for(int i=0;i<items.size();i++)
					_adapter.add(items.get(i));
			}
			_ProgressDialog.dismiss();
			_adapter.notifyDataSetChanged();
		}
	};
	private OnItemClickListener listlistener = new OnItemClickListener() {
		public void onItemClick(AdapterView parent, View arg1, int position, long arg3) {
			AVResource item = ((AVResource)parent.getItemAtPosition(position));
			if (item instanceof AVFolder) {
				pwd = server.cd((AVFolder)item);
				Thread thread =  new Thread(null, viewContents, "Interrogation");
				thread.start();
				_ProgressDialog = ProgressDialog.show(activity, "Communication Status", "Interrogating AirVideoServer", true);
			} else if (item instanceof AVVideo) {
				URL url = ((AVVideo)item).live_url();
				
				Context context = getApplicationContext();
				CharSequence text = url.toString();
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}
		}
	};

	private class AVFolderListAdapter extends ArrayAdapter<AVResource> {
		private ArrayList <AVResource> al;


		public AVFolderListAdapter(Context context, int textViewResourceId, ArrayList <AVResource> items) {
			super(context, textViewResourceId, items);
			this.al = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row, null);
			}
			AVResource o = al.get(position);
			if (o != null) {
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
				ImageView iv = (ImageView) v.findViewById(R.id.icon);
				if (tt != null) {
					tt.setText("Name: "+o.name);
				}
				if(bt != null){
					if (o instanceof AVFolder) {
						bt.setText("Folder");
						iv.setVisibility(View.GONE);
					} else if (o instanceof AVVideo) {
						bt.setText("Video");
						iv.setImageBitmap(((AVVideo)o).thumbnail());
					}
					
				}
			}
			return v;
		}

	}
}