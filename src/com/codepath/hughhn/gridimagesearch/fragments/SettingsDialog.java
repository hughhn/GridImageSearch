package com.codepath.hughhn.gridimagesearch.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.codepath.hughhn.gridimagesearch.R;
import com.codepath.hughhn.gridimagesearch.models.SearchFilters;

public class SettingsDialog extends DialogFragment {
	private Spinner spSize;
	private Spinner spColor;
	private Spinner spType;
	private EditText etSite;
	private Button saveBtn;
	private SharedPreferences mSettings;
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

		spSize = (Spinner) view.findViewById(R.id.spSize);
		spColor = (Spinner) view.findViewById(R.id.spColor);
		spType = (Spinner) view.findViewById(R.id.spType);
		etSite = (EditText) view.findViewById(R.id.etSite);

		String cookieName = mSettings.getString("imgsz", "");
		if (!cookieName.equals("")) {
			setSpinnerToValue(spSize, cookieName);
		}
		cookieName = mSettings.getString("imgcolor", "");
		if (!cookieName.equals("")) {
			setSpinnerToValue(spColor, cookieName);
		}
		cookieName = mSettings.getString("imgtype", "");
		if (!cookieName.equals("")) {
			setSpinnerToValue(spType, cookieName);
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
				if (spSize.getSelectedItem() != null) {
					filters.imgSize = spSize.getSelectedItem().toString();
				}
				if (spColor.getSelectedItem() != null) {
					filters.imgColor = spColor.getSelectedItem().toString();
				}
				if (spType.getSelectedItem() != null) {
					filters.imgType = spType.getSelectedItem().toString();
				}
				if (etSite.getText() != null) {
					filters.imgSite = etSite.getText().toString();
				}
				listener.onFinishSettingsDialog(filters);
			}
		});

		return view;
	}
	
	public void setSpinnerToValue(Spinner spinner, String value) {
		int index = 0;
		SpinnerAdapter adapter = spinner.getAdapter();
		for (int i = 0; i < adapter.getCount(); i++) {
			if (adapter.getItem(i).equals(value)) {
				index = i;
				break; // terminate loop
			}
		}
		spinner.setSelection(index);
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
