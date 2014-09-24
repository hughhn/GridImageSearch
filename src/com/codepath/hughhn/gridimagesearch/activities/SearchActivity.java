package com.codepath.hughhn.gridimagesearch.activities;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.codepath.hughhn.gridimagesearch.R;
import com.codepath.hughhn.gridimagesearch.adapters.ImageResultsAdapter;
import com.codepath.hughhn.gridimagesearch.fragments.SettingsDialog;
import com.codepath.hughhn.gridimagesearch.fragments.SettingsDialog.SettingDialogListener;
import com.codepath.hughhn.gridimagesearch.listeners.EndlessScrollListener;
import com.codepath.hughhn.gridimagesearch.models.ImageResult;
import com.codepath.hughhn.gridimagesearch.models.SearchFilters;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class SearchActivity extends FragmentActivity implements
		SettingDialogListener {
	private GridView gvResults;
	private ArrayList<ImageResult> imageResults;
	private ImageResultsAdapter aImageResults;
	SharedPreferences mSettings;
	private SearchView searchView;
	private String query;
	private SettingsDialog settingsDialog;
	
	public Boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setupViews();
	}

	private void setupViews() {
		gvResults = (GridView) findViewById(R.id.gvResults);

		// Initialize data source for gridView
		imageResults = new ArrayList<ImageResult>();
		// Attach data source to adapter
		aImageResults = new ImageResultsAdapter(this, imageResults);
		// Link adapter to gridView
		gvResults.setAdapter(aImageResults);

		gvResults.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Check Internet connection
				if (!isNetworkAvailable()) {
					Toast.makeText(getApplicationContext(), "network unavailable", Toast.LENGTH_SHORT).show();
					return;
				}
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

		// Attach endless scroll listener
		gvResults.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				// Triggered only when new data needs to be appended to the list
				customLoadMoreDataFromApi(SearchActivity.this.query, page);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		searchView = (SearchView) searchItem.getActionView();
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				// perform query here
				SearchActivity.this.query = query;
				aImageResults.clear();
				customLoadMoreDataFromApi(SearchActivity.this.query, 0);

				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			showSettingsDiaglog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showSettingsDiaglog() {
		FragmentManager fm = getSupportFragmentManager();
		settingsDialog = SettingsDialog.newInstance();
		settingsDialog.show(fm, "fragment_settings");
	}

	public void onFinishSettingsDialog(SearchFilters filters) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.clear();
		editor.putString("imgsz", filters.imgSize);
		editor.putString("imgcolor", filters.imgColor);
		editor.putString("imgtype", filters.imgType);
		editor.putString("as_sitesearch", filters.imgSite);
		editor.commit();
		settingsDialog.dismiss();
	}

	public void customLoadMoreDataFromApi(String query, int page) {
		// Check Internet connection
		if (!isNetworkAvailable()) {
			Toast.makeText(getApplicationContext(), "network unavailable", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// Create HTTP request
		AsyncHttpClient client = new AsyncHttpClient();

		// https://ajax.googleapis.com/ajax/services/search/images
		StringBuilder searchUrl = new StringBuilder(
				"https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q="
						+ query + "&rsz=8" + "&start=" + String.valueOf(page));

		mSettings = getSharedPreferences("Settings", 0);
		String cookieName = mSettings.getString("imgsz", "none");
		if (!cookieName.equals("none")) {
			searchUrl.append("&imgsz=" + cookieName);
		}
		cookieName = mSettings.getString("imgcolor", "none");
		if (!cookieName.equals("none")) {
			searchUrl.append("&imgcolor=" + cookieName);
		}
		cookieName = mSettings.getString("imgtype", "none");
		if (!cookieName.equals("none")) {
			searchUrl.append("&imgtype=" + cookieName);
		}
		cookieName = mSettings.getString("as_sitesearch", "");
		if (!cookieName.equals("")) {
			searchUrl.append("&as_sitesearch=" + cookieName);
		}

		Log.i("DEBUG", "searchUrl = " + searchUrl.toString());
		final int startPage = page;
		client.get(searchUrl.toString(), new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				Log.d("DEBUG", response.toString());

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

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				Log.d("DEBUG", responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
}
