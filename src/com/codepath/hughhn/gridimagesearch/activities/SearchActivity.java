package com.codepath.hughhn.gridimagesearch.activities;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.codepath.hughhn.gridimagesearch.listeners.EndlessScrollListener;
import com.codepath.hughhn.gridimagesearch.models.ImageResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class SearchActivity extends Activity {
	private EditText etQuery;
	private GridView gvResults;
	private ArrayList<ImageResult> imageResults;
	private ImageResultsAdapter aImageResults;
	SharedPreferences mSettings;

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

		// Attach endless scroll listener
		gvResults.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				// Triggered only when new data needs to be appended to the list
				// Add whatever code is needed to append new items to your
				// AdapterView
				customLoadMoreDataFromApi(page);
				// or customLoadMoreDataFromApi(totalItemsCount);
			}
		});

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
				Intent i = new Intent(SearchActivity.this,
						ImageDisplayActivity.class);

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
			Intent i = new Intent(this, SettingsActivity.class);

			// Execute my intent
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Fire when button is pressed
	public void onImageSearch(View v) {
		aImageResults.clear();
		customLoadMoreDataFromApi(0);
	}

	public void customLoadMoreDataFromApi(int page) {
		String query = etQuery.getText().toString();
		AsyncHttpClient client = new AsyncHttpClient();

		// https://ajax.googleapis.com/ajax/services/search/images
		StringBuilder searchUrl = new StringBuilder(
				"https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q="
						+ query + "&rsz=8" + "&start=" + String.valueOf(page));

		mSettings = getSharedPreferences("Settings", 0);
		String cookieName = mSettings.getString("imgsz", "");
		if (!cookieName.equals("")) {
			searchUrl.append("&imgsz=" + cookieName);
		}
		cookieName = mSettings.getString("imgcolor", "");
		if (!cookieName.equals("")) {
			searchUrl.append("&imgcolor=" + cookieName);
		}
		cookieName = mSettings.getString("imgtype", "");
		if (!cookieName.equals("")) {
			searchUrl.append("&imgtype=" + cookieName);
		}
		cookieName = mSettings.getString("as_sitesearch", "");
		if (!cookieName.equals("")) {
			searchUrl.append("&as_sitesearch=" + cookieName);
		}

		Log.i("DEBUGG", "searchUrl = " + searchUrl.toString());
		final int startPage = page;
		client.get(searchUrl.toString(), new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
//				Log.d("DEBUG", response.toString());

				try {
					JSONArray imageResultsJSON = response.getJSONObject(
							"responseData").getJSONArray("results");
					if (startPage == 0) {
						// Clear existing images ONLY FOR NEW SEARCH
						aImageResults.clear();
					}

					// changes to adapter, underlying data also gets changed
					aImageResults.addAll(ImageResult
							.fromJSONArray(imageResultsJSON));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
