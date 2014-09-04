package com.illustratedbooks.story;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class StoryDisplayData {
	private int mDp_w;// ディスプレイの幅
	private int mDp_h;// ディスプレイの高さ
	private int mDrow_w;// 背景を実際に表示するときの画像の幅
	private int mDrow_h;// 背景を実際に表示するときの画像の高さ
	private int mDrowPos_l = 0;// 背景のx軸の表示位置
	private int mDrowPos_t = 0;// 背景のy軸の表示位置

	/**
	 * ディスプレイデータのみを設定
	 * @param context
	 */
	public StoryDisplayData(Context context){
		//ディスプレイデータを取得
		setDisplayData(context);
	}
	
	/**
	 * ディスプレイデータ、この画面に最適な画像の大きさ、背景画像の描写開始位置を設定
	 * @param context
	 * @param bmp
	 * 背景に使う画像
	 */
	public StoryDisplayData(Context context, Bitmap bmp){
		//ディスプレイデータを取得
		setDisplayData(context);

		// 背景画像のサイズを画面サイズに初期化
		mDrow_w = mDp_w;
		mDrow_h = mDp_h;
		
		if (bmp.getHeight() >= bmp.getWidth()) {
			// 縦長の画像の場合
			mDrow_w = mDp_h * bmp.getWidth() / bmp.getHeight(); // リサイズ画像の幅
			mDrowPos_l = (mDp_w - mDrow_w) / 2; // 描画始点のx座標
			mDrowPos_t = 0;// 描写するx軸を初期化
		} else {
			// 横長の画像の場合
			mDrow_h = mDp_w * bmp.getHeight() / bmp.getWidth(); // リサイズ画像の幅
			mDrowPos_t = (mDp_h - mDrow_h) / 2; // 描画始点のx座標
			mDrowPos_l = 0;// 描写するx軸を初期化
		}
	}

	public void setDisplayData(Context context) {
		// WindowManager取得
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display dp = wm.getDefaultDisplay();
		// ディスプレイサイズ取得
		Point size = new Point();
		dp.getSize(size);
		mDp_w = size.x;
		mDp_h = size.y;
	}
}