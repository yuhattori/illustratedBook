package com.illustratedbooks.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
	private SharedPreferences mPref;

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

		mPref = getActivity().getSharedPreferences(
				"savedata" + StorySurfaceView.CSV_FILE_NAME,
				Context.MODE_PRIVATE);

		continueBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "on click continueBtn");
				if (!mPref.getString("savedRowNo", "noData").equals("noData")) {
					((MainActivity) getActivity())
							.changeActivity("continueBtn");
				} else {
					// 初回起動でsavedataが存在しないとき
					Log.d(TAG, "first boot");
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(
							(MainActivity) getActivity());

					// ダイアログの設定
					alertDialog.setTitle("えほんをはじめからよみます"); // タイトル設定
					alertDialog.setMessage("つづき　が　ないので　はじめから　えほん　を　よみます。"); // 内容(メッセージ)設定

					// [はい]ボタンの設定
					alertDialog.setPositiveButton("はい",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// [はい]ボタン押下時の処理
									Log.d(TAG, "えほんをはじめからよみますか？　＞　はい");
									// [はじめから]ボタンと同じ動きをする
									((MainActivity) getActivity())
											.changeActivity("startBtn");
								}
							});
					alertDialog.show();
				}
			}
		});

		// [あそびかた]ボタン
		Button configBtn = (Button) getActivity().findViewById(R.id.configBtn);
		configBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "on click configBtn");
				ConfigDialogFragment cDialog = new ConfigDialogFragment();
				cDialog.show(getFragmentManager(), "ConfigDialogFragment");
				
			}
		});

	}
}
