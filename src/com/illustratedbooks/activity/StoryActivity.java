package com.illustratedbooks.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.illustratedbooks.R;
import com.illustratedbooks.fragment.StoryBackgroundFragment;
import com.illustratedbooks.fragment.StoryMessegeWindowFragment;
import com.illustratedbooks.fragment.TopFragment;
import com.illustratedbooks.story.StorySurfaceView;

public class StoryActivity extends FragmentActivity {

	private static final String TAG = StoryActivity.class.getSimpleName();
	private static final String BACKGROUND_FLAGMENT = "background_frangment";
	private static final String MESSEGE_WINDOW_FLAGMENT = "messege_window_frangment";

	private LinearLayout mBgLayout;
	private LinearLayout mMsgLayout;

	private Activity mAct;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mAct = this;
		setContentView(R.layout.activity_story);
		Intent intent = getIntent();
		String clickedBtn = intent.getStringExtra("clickedBtn");
		if (clickedBtn.equalsIgnoreCase("startBtn")) {
			// はじめからボタンを押してStoryActivityに来たとき
		} else {
			// つづきからボタンを押してStoryActivityに来たとき
		}

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// フラグメントを入れるレイアウトを取得
		mBgLayout = (LinearLayout) mAct
				.findViewById(R.id.background_layout);
		mMsgLayout = (LinearLayout) mAct
				.findViewById(R.id.messege_window_layout);

		/* onCreateではレンダリングが終わっていないため */
		// Fragmentを管理するFragmentManagerを取得
		FragmentManager manager = getSupportFragmentManager();
		// 追加や削除などを1つの処理としてまとめるためのトランザクションクラスを取得
		FragmentTransaction ft = manager.beginTransaction();

		StoryBackgroundFragment bgf = new StoryBackgroundFragment();
		StoryMessegeWindowFragment mwf = new StoryMessegeWindowFragment();

		// Fragment をスタックに追加する
		ft.add(mBgLayout.getId(), bgf, BACKGROUND_FLAGMENT);
		ft.add(mMsgLayout.getId(), mwf, MESSEGE_WINDOW_FLAGMENT);
		ft.commit();
	}

	public void addFragmentToStack() {
	    // フラグメントのインスタンスを生成する。
		StoryBackgroundFragment newFragment = new StoryBackgroundFragment();
		
		// Fragmentを管理するFragmentManagerを取得
		FragmentManager manager = getSupportFragmentManager();
		// 追加や削除などを1つの処理としてまとめるためのトランザクションクラスを取得(新しく作らないと”commit already called”とエラーが出る)
		FragmentTransaction ft = manager.beginTransaction();
	    // Layout位置先の指定
		ft.replace(mMsgLayout.getId(), newFragment);
	    // Fragmentの変化時のアニメーションを指定
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.addToBackStack(null);
		ft.commit();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			addFragmentToStack();
			// // バックボタンが押されたとき
			// if(mStSf.isAutoMode())
			// //オートモード中の場合は解除
			// mStSf.autoMode(StorySurfaceView.OFF);
			//
			// AlertDialog.Builder alertDialog = new AlertDialog.Builder(mAct);
			//
			// // ダイアログの設定
			// alertDialog.setTitle("えほんをとじてはじめにもどる？"); // タイトル設定
			// alertDialog.setMessage("えほんをとじてはじめにもどりますか？"); // 内容(メッセージ)設定
			//
			// // はいボタンの設定
			// alertDialog.setPositiveButton("はい",
			// new DialogInterface.OnClickListener() {
			// public void onClick(DialogInterface dialog, int which) {
			// // はいボタン押下時の処理
			// Log.d(TAG, "えほんをとじてはじめにもどりますか？　＞　はい");
			// Intent intent = new Intent();
			// intent.setClass(mAct, MainActivity.class);
			// startActivity(intent);
			// mAct.finish();//Activityの終了
			// }
			// });
			//
			// // いいえボタンの設定
			// alertDialog.setNegativeButton("いいえ",
			// new DialogInterface.OnClickListener() {
			// public void onClick(DialogInterface dialog, int which) {
			// // いいえボタン押下時の処理
			// Log.d(TAG, "えほんをとじてはじめにもどりますか？　＞　いいえ");
			// }
			// });
			// alertDialog.show();
			// return true;
		default:
			return false;
		}
	}
}
