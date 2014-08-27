package com.illustratedbooks.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;

import com.illustratedbooks.story.StorySurfaceView;

public class StoryActivity extends FragmentActivity {

	private static final String TAG = StoryActivity.class.getSimpleName();
	private Activity mAct;
	private StorySurfaceView mStSf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mAct=this;
		Intent intent = getIntent();
		String clickedBtn = intent.getStringExtra("clickedBtn");
		if (clickedBtn.equalsIgnoreCase("startBtn")) {
			// はじめからボタンを押してStoryActivityに来たとき
			mStSf=new StorySurfaceView(this);
			setContentView(mStSf);
		} else {
			// つづきからボタンを押してStoryActivityに来たとき
			mStSf=new StorySurfaceView(this, clickedBtn);
			setContentView(mStSf);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			// バックボタンが押されたとき
			if(mStSf.isAutoMode())
				//オートモード中の場合は解除
				mStSf.autoMode(StorySurfaceView.OFF);
			
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

			// ダイアログの設定
			alertDialog.setTitle("えほんをとじてはじめにもどる？"); // タイトル設定
			alertDialog.setMessage("えほんをとじてはじめにもどりますか？"); // 内容(メッセージ)設定

			// はいボタンの設定
			alertDialog.setPositiveButton("はい",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// はいボタン押下時の処理
							Log.d(TAG, "えほんをとじてはじめにもどりますか？　＞　はい");
							Intent intent = new Intent();
							intent.setClass(mAct, MainActivity.class);
							startActivity(intent);
							mAct.finish();//Activityの終了
						}
					});

			// いいえボタンの設定
			alertDialog.setNegativeButton("いいえ",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// いいえボタン押下時の処理
							Log.d(TAG, "えほんをとじてはじめにもどりますか？　＞　いいえ");
						}
					});
			alertDialog.show();
			return true;
		default:
			return false;
		}
	}
}
