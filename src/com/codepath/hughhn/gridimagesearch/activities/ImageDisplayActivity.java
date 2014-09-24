package com.codepath.hughhn.gridimagesearch.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.hughhn.gridimagesearch.R;
import com.codepath.hughhn.gridimagesearch.models.ImageResult;
import com.squareup.picasso.Picasso;

public class ImageDisplayActivity extends Activity {
	ImageResult result;
	ImageView ivImageResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_display);

		// Remove action bar
		// getActionBar().hide();

		// Pull out the url from the intent
		result = (ImageResult) getIntent().getSerializableExtra("result");
		// Find the image view
		ivImageResult = (ImageView) findViewById(R.id.ivImageResult);
		
		// Load the image url into imageview using Picasso
		Picasso.with(this).load(result.fullUrl).fit().centerInside()
				.into(ivImageResult);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_display, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_share) {
			Drawable mDrawable = ivImageResult.getDrawable();
			Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();

			String path = Images.Media.insertImage(getContentResolver(),
					mBitmap, "Image Description", null);

			Uri uri = Uri.parse(path);

			if (uri != null) {
				// Construct a ShareIntent with link to image
				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
				shareIntent.setType("image/*");
				// Launch sharing dialog for image
				startActivity(Intent.createChooser(shareIntent, "Share Image"));
			} else {
				// ...sharing failed, handle error
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
