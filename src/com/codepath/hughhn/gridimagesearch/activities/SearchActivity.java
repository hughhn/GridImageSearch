package com.codepath.hughhn.gridimagesearch.activities;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;

import com.codepath.hughhn.gridimagesearch.R;
import com.codepath.hughhn.gridimagesearch.adapters.ImageResultsAdapter;
import com.codepath.hughhn.gridimagesearch.models.ImageResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class SearchActivity extends Activity {
	private EditText etQuery;
	private GridView gvResults;
	private ArrayList<ImageResult> imageResults;
	private ImageResultsAdapter aImageResults;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setupViews();
		imageResults = new ArrayList<ImageResult>();
		
		// Attach data source to adapter
		aImageResults = new ImageResultsAdapter(this, imageResults);
		
		// Link adapter to gridView
		gvResults.setAdapter(aImageResults);
		
	}

	private void setupViews() {
		etQuery = (EditText) findViewById(R.id.etQuery);
		gvResults = (GridView) findViewById(R.id.gvResults);
		gvResults.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Launch the image display activity
				// Create an intent
				Intent i = new Intent(SearchActivity.this, ImageDisplayActivity.class);
				
				// Get the image result to display
				ImageResult result = imageResults.get(position);
				// Pass image result into the intent
				i.putExtra("result", result);
				
				// Launch new activity
				startActivity(i);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Fire when button is pressed
	public void onImageSearch(View v) {
		String query = etQuery.getText().toString();
		AsyncHttpClient client = new AsyncHttpClient();
		// https://ajax.googleapis.com/ajax/services/search/images
		String searchUrl = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q="
				+ query + "&rsz=8";

		client.get(searchUrl, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				Log.d("DEBUG", response.toString());

				try {
					JSONArray imageResultsJSON = response.getJSONObject(
							"responseData").getJSONArray("results");
					imageResults.clear(); /// Clear existing images ONLY FOR NEW SEARCH
					
					// changes to adapter, underlying data also gets changed
					aImageResults.addAll(ImageResult.fromJSONArray(imageResultsJSON));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

	}
}
