package com.codepath.hughhn.gridimagesearch.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.hughhn.gridimagesearch.R;
import com.codepath.hughhn.gridimagesearch.models.SearchFilters;

public class SettingsDialog extends DialogFragment {
	private EditText etSize;
	private EditText etColor;
	private EditText etType;
	private EditText etSite;
	private Button saveBtn;
	private SharedPreferences mSettings;
	private SharedPreferences.Editor editor;
	private SettingDialogListener listener;

	public SettingsDialog() {
		// Empty constructor required for DialogFragment
	}

	public static SettingsDialog newInstance() {
		SettingsDialog frag = new SettingsDialog();
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_settings, container);

		mSettings = getActivity().getSharedPreferences("Settings", 0);

		etSize = (EditText) view.findViewById(R.id.etSize);
		etColor = (EditText) view.findViewById(R.id.etColor);
		etType = (EditText) view.findViewById(R.id.etType);
		etSite = (EditText) view.findViewById(R.id.etSite);

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

		saveBtn = (Button) view.findViewById(R.id.btnSave);
		saveBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				SearchFilters filters = new SearchFilters();
				filters.imgSize = "";
				filters.imgColor = "";
				filters.imgType = "";
				filters.imgSite = "";
				if (etSize.getText() != null) {
					filters.imgSize = etSize.getText().toString();
				}
				if (etColor.getText() != null) {
					filters.imgColor = etColor.getText().toString();
				}
				if (etType.getText() != null) {
					filters.imgType = etType.getText().toString();
				}
				if (etSite.getText() != null) {
					filters.imgSite = etSite.getText().toString();
				}
				listener.onFinishSettingsDialog(filters);
			}
		});

		return view;
	}

	public interface SettingDialogListener {
		void onFinishSettingsDialog(SearchFilters filters);
	}

	// Store the listener (activity) that will have events fired once the
	// fragment is attached
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof SettingDialogListener) {
			listener = (SettingDialogListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement SettingsDialog.SettingDialogListener");
		}
	}
}
