package com.illustratedbooks.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.illustratedbooks.R;
import com.illustratedbooks.activity.MainActivity;
import com.illustratedbooks.story.StorySurfaceView;

public class TopFragment extends Fragment {

	private static final String TAG = TopFragment.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.top_layout, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		// [はじめから]ボタン
		Button startBtn = (Button) getActivity().findViewById(R.id.startBtn);
		startBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "on click startBtn");
				((MainActivity) getActivity()).changeActivity("startBtn");
			}
		});

		// [つづきから]ボタン
		Button continueBtn = (Button) getActivity().findViewById(
				R.id.continueBtn);

		SharedPreferences pref = getActivity().getSharedPreferences(
				"savedata" + StorySurfaceView.CSV_FILE_NAME,
				Context.MODE_PRIVATE);

		if (!pref.getString("savedRowNo", "noData").equals("noData")) {
			// savedataが存在するとき
			continueBtn.setVisibility(View.VISIBLE);
			continueBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, "on click continueBtn");
					((MainActivity) getActivity())
							.changeActivity("continueBtn");
				}
			});
		} else {
			// 初回起動でsavedataが存在しないとき
			Log.d(TAG, "first boot");
			continueBtn.setVisibility(View.INVISIBLE);
		}
		
		// [あそびかた]ボタン
		Button configBtn = (Button) getActivity().findViewById(R.id.configBtn);
		configBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "on click configBtn");
				//TODO あそびかた画面の呼び出しを書く
			}
		});

	}
}
