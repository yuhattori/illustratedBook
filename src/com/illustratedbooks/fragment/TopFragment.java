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

		// // Zipファイルを一時ファイルとして保存
		// File file = null;
		// try {
		// file = File.createTempFile("sample",
		// "zip");//一時ファイル名は暫定的にsample.zipとする
		// InputStream is = getResources().getAssets().open("akazukin.zip");
		// FileOutputStream fos = new FileOutputStream(file);
		// byte[] buffer = new byte[1024];
		// int length = 0;
		// while ((length = is.read(buffer))>0) {
		// fos.write(buffer, 0, length);
		// }
		// fos.close();
		// is.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		//

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
				Log.d("TopFragment", "on click startBtn");
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

	}
}
