package com.illustratedbooks.story;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class StoryDisplayData {
	public final static int HORIZONTAL_LAYOUT = 1;
	public final static int VERTICAL_LAYOUT = 2;

	private int mLayoutMode;// 背景とメッセージウィンドウの配置の種類

	// Display
	private int mDp_w = 0;// ディスプレイの幅
	private int mDp_h = 0;// ディスプレイの高さ

	// Background
	private int mDrowBG_w = 0;// 背景を実際に表示するときの画像の幅
	private int mDrowBG_h = 0;// 背景を実際に表示するときの画像の高さ
	private int mDrowPosBG_x = 0;// 背景のx軸の表示位置
	private int mDrowPosBG_y = 0;// 背景のy軸の表示位置

	// MessageWing
	private int mDrowMSG_w = 0;// 背景を実際に表示するときの画像の幅
	private int mDrowMSG_h = 0;// 背景を実際に表示するときの画像の高さ
	private int mDrowPosMSG_x = 0;// 背景のx軸の表示位置
	private int mDrowPosMSG_y = 0;// 背景のy軸の表示位置
	private int mPaddingMSG = 0;

	/**
	 * ディスプレイデータのみを設定
	 * 
	 * @param context
	 */
	public StoryDisplayData(Context context) {
		// ディスプレイデータを取得
		setDisplayData(context);
	}

	/**
	 * ディスプレイデータ、この画面に最適な画像の大きさ、背景画像の描写開始位置を設定
	 * 
	 * @param context
	 * @param bmp
	 *            背景画像データ
	 */
	public StoryDisplayData(Context context, Bitmap bmp, int layout) {
		this.mLayoutMode = layout;

		// ディスプレイデータを取得
		setDisplayData(context);

		switch (layout) {
		case VERTICAL_LAYOUT:
			setVerticalLayoutMode(bmp);
			break;
		case HORIZONTAL_LAYOUT:
			setHorizontalLayoutMode(bmp);
		default:
			break;
		}
	}

	/**
	 * 背景の中にメッセージウィンドウが表示されるモード
	 * 
	 * @param bmp
	 *            背景画像データ
	 */
	public void setVerticalLayoutMode(Bitmap bmp) {
		/* BackGroud */
		// 背景画像のサイズを画面サイズに初期化
		if (bmp.getHeight() >= bmp.getWidth()) {
			/* 縦長の画像の場合 */
			mDrowBG_w = mDp_h * (bmp.getWidth() / bmp.getHeight()); // 背景画像の幅
			mDrowBG_h = mDp_h;// 背景画像の高さ
			mDrowPosBG_x = (mDp_w - mDrowBG_w) / 2; // 描画始点のx座標
			mDrowPosBG_y = 0;// 描画始点のy座標
		} else {
			// 横長の画像の場合
			mDrowBG_w = mDp_w;// 背景画像の幅
			mDrowBG_h = mDp_w * (bmp.getHeight() / bmp.getWidth()); // 背景画像の高さ
			mDrowPosBG_x = 0;// / 描画始点のx座標
			mDrowPosBG_y = (mDp_h - mDrowBG_h) / 2;// 描画始点のy座標
		}
		/* MessageWindow */
		mDrowMSG_w = mDp_w / 2;// メッセージウィンドウの幅を設定
		mDrowMSG_h = mDp_h / 5;// メッセージウィンドウの高さを設定
		mDrowPosMSG_x = (mDp_w - mDrowMSG_w) / 2;// 画面に対して中央に来るように設定
		mDrowPosMSG_y = (mDp_h - mDrowMSG_h) - mPaddingMSG;// 画面下からpadding分空いた位置に設定
	}

	/**
	 * 横画面に対して　メッセージウィンドウ:背景画像=1:2　の割合で表示させる
	 * 
	 * @param bmp
	 *            背景画像データ
	 */
	public void setHorizontalLayoutMode(Bitmap bmp) {
		double perMwdInDisplay = 1 / 3;// 画面に対してのメッセージウィンドが閉める割合
		/* BackGroud */
		if (bmp.getHeight() >= bmp.getWidth()) {
			// 縦長の画像の場合
			mDrowBG_w = mDp_h * (bmp.getWidth() / bmp.getHeight());// 背景画像の幅
			mDrowBG_h = mDp_h;// 背景画像の高さ
			mDrowPosBG_x = (int) (mDp_w * perMwdInDisplay)
					+ ((mDp_w - mDrowMSG_w) / 2);// 描画始点のx座標
			mDrowPosBG_y = 0;// 描画始点のy座標
		} else {
			// 横長の画像の場合
			mDrowBG_w = mDp_w;// 背景画像の幅
			mDrowBG_h = mDp_w * (bmp.getHeight() / bmp.getWidth()); // 背景画像の高さ
			mDrowPosBG_x = (int) (mDp_w * perMwdInDisplay);// / 描画始点のx座標
			mDrowPosBG_y = (mDp_h - mDrowBG_h) / 2;// 描画始点のy座標
		}
		/* MessageWindow */
		mDrowMSG_w = (int) (mDp_w * perMwdInDisplay);// メッセージウィンドウの幅を設定
		mDrowMSG_h = mDp_h;// メッセージウィンドウの高さを設定
		mDrowPosMSG_x = 0;
		mDrowPosMSG_y = 0;
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

	/**
	 * メッセージウィンドのデフォルトのpaddeingを設定する。
	 */
	public void setDefaultPaddingMSG() {
		this.mPaddingMSG = mDp_w / 50;
	}
}