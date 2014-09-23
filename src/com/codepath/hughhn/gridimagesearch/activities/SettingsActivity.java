package com.codepath.hughhn.gridimagesearch.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.codepath.hughhn.gridimagesearch.R;

public class SettingsActivity extends Activity {
	private EditText etSize;
	private EditText etColor;
	private EditText etType;
	private EditText etSite;
	SharedPreferences mSettings;
	SharedPreferences.Editor editor;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		mSettings = getSharedPreferences("Settings", 0);
		
		etSize = (EditText) findViewById(R.id.etSize);
		etColor = (EditText) findViewById(R.id.etColor);
		etType = (EditText) findViewById(R.id.etType);
		etSite = (EditText) findViewById(R.id.etSite);
		
		String cookieName = mSettings.getString("imgsz", "");
		if (!cookieName.equals("")) {
			etSize.setText(cookieName);
		}
		cookieName = mSettings.getString("imgcolor", "");
		if (!cookieName.equals("")) {
			etColor.setText(cookieName);
		}
		cookieName = mSettings.getString("imgtype", "");
		if (!cookieName.equals("")) {
			etType.setText(cookieName);
		}
		cookieName = mSettings.getString("as_sitesearch", "");
		if (!cookieName.equals("")) {
			etSite.setText(cookieName);
		}
		
	}
	
	public void OnSaveClicked(View view) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.clear();
		editor.putString("imgsz", etSize.getText().toString());
		editor.putString("imgcolor", etColor.getText().toString());
		editor.putString("imgtype", etType.getText().toString());
		editor.putString("as_sitesearch", etSite.getText().toString());
		editor.commit();
		finish();
	}
	
}
