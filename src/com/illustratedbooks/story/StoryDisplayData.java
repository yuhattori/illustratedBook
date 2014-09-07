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
	private int mDrownBG_w = 0;// 背景を実際に表示するときの画像の幅
	private int mDrownBG_h = 0;// 背景を実際に表示するときの画像の高さ
	private int mDrownPosBG_x = 0;// 背景のx軸の表示位置
	private int mDrownPosBG_y = 0;// 背景のy軸の表示位置

	// MessageWing
	private int mDrownMSG_w = 0;// 背景を実際に表示するときの画像の幅
	private int mDrownMSG_h = 0;// 背景を実際に表示するときの画像の高さ
	private int mDrownPosMSG_x = 0;// 背景のx軸の表示位置
	private int mDrownPosMSG_y = 0;// 背景のy軸の表示位置
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
		setDefaultPaddingMSG();

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
			mDrownBG_w = mDp_h * (bmp.getWidth() / bmp.getHeight()); // 背景画像の幅
			mDrownBG_h = mDp_h;// 背景画像の高さ
			mDrownPosBG_x = (mDp_w - mDrownBG_w) / 2; // 描画始点のx座標
			mDrownPosBG_y = 0;// 描画始点のy座標
		} else {
			// 横長の画像の場合
			mDrownBG_w = mDp_w;// 背景画像の幅
			mDrownBG_h = mDp_w * (bmp.getHeight() / bmp.getWidth()); // 背景画像の高さ
			mDrownPosBG_x = 0;// / 描画始点のx座標
			mDrownPosBG_y = (mDp_h - mDrownBG_h) / 2;// 描画始点のy座標
		}
		/* MessageWindow */
		mDrownMSG_w = mDp_w / 2;// メッセージウィンドウの幅を設定
		mDrownMSG_h = mDp_h / 4;// メッセージウィンドウの高さを設定
		mDrownPosMSG_x = (mDp_w - mDrownMSG_w) / 2;// 画面に対して中央に来るように設定
		mDrownPosMSG_y = (mDp_h - mDrownMSG_h) - mPaddingMSG;// 画面下からpadding分空いた位置に設定
	}

	/**
	 * 横画面に対して　メッセージウィンドウ:背景画像=1:2　の割合で表示させる
	 * 
	 * @param bmp
	 *            背景画像データ
	 */
	public void setHorizontalLayoutMode(Bitmap bmp) {
		double perMwdInDisplay = 1.0d / 3.0d;// 画面に対してのメッセージウィンドが閉める割合
		/* MessageWindow */
		mDrownMSG_w = (int) (mDp_w * perMwdInDisplay);// メッセージウィンドウの幅を設定
		mDrownMSG_h = mDp_h;// メッセージウィンドウの高さを設定
		mDrownPosMSG_x = 0;
		mDrownPosMSG_y = 0;

		/* BackGroud */
		if (bmp.getHeight() >= bmp.getWidth()) {
			// 縦長の画像の場合
			mDrownBG_w = mDp_h * (bmp.getWidth() / bmp.getHeight());// 背景画像の幅
			mDrownBG_h = mDp_h;// 背景画像の高さ
			mDrownPosBG_x = (int) mDrownMSG_w
					+ ((mDp_w - mDrownMSG_w) - mDrownBG_w)/2;// 描画始点のx座標
			mDrownPosBG_y = 0;// 描画始点のy座標
		} else {
			// 横長の画像の場合
			mDrownBG_w = mDp_w;// 背景画像の幅
			mDrownBG_h = mDp_w * (bmp.getHeight() / bmp.getWidth()); // 背景画像の高さ
			mDrownPosBG_x = (int) (mDp_w * perMwdInDisplay);// / 描画始点のx座標
			mDrownPosBG_y = (mDp_h - mDrownBG_h) / 2;// 描画始点のy座標
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

	/**
	 * メッセージウィンドのデフォルトのpaddeingを設定する。
	 */
	public void setDefaultPaddingMSG() {
		this.mPaddingMSG = mDp_w / 30;
	}

	/* setter and getter */
	public int getLayoutMode() {
		return mLayoutMode;
	}

	public void setLayoutMode(int mLayoutMode) {
		this.mLayoutMode = mLayoutMode;
	}

	public int getDrownBGWidth() {
		return mDrownBG_w;
	}

	public void setDrownBGWidth(int mDrownBG_w) {
		this.mDrownBG_w = mDrownBG_w;
	}

	public int getDrownBGHeight() {
		return mDrownBG_h;
	}

	public void setDrownBGHeight(int mDrownBG_h) {
		this.mDrownBG_h = mDrownBG_h;
	}

	public int getDrownPosBG_x() {
		return mDrownPosBG_x;
	}

	public void setDrownPosBG_x(int mDrownPosBG_x) {
		this.mDrownPosBG_x = mDrownPosBG_x;
	}

	public int getDrownPosBG_y() {
		return mDrownPosBG_y;
	}

	public void setDrownPosBG_y(int mDrownPosBG_y) {
		this.mDrownPosBG_y = mDrownPosBG_y;
	}

	public int getDrownMSGWidth() {
		return mDrownMSG_w;
	}

	public void setDrownMSGWidth(int mDrownMSG_w) {
		this.mDrownMSG_w = mDrownMSG_w;
	}

	public int getDrownMSGHeight() {
		return mDrownMSG_h;
	}

	public void setDrownMSGHeight(int mDrownMSG_h) {
		this.mDrownMSG_h = mDrownMSG_h;
	}

	public int getDrownPosMSG_x() {
		return mDrownPosMSG_x;
	}

	public void setDrownPosMSG_x(int mDrownPosMSG_x) {
		this.mDrownPosMSG_x = mDrownPosMSG_x;
	}

	public int getDrownPosMSG_y() {
		return mDrownPosMSG_y;
	}

	public void setDrownPosMSG_y(int mDrownPosMSG_y) {
		this.mDrownPosMSG_y = mDrownPosMSG_y;
	}

	public int getPaddingMSG() {
		return mPaddingMSG;
	}

	public void setPaddingMSG(int mPaddingMSG) {
		this.mPaddingMSG = mPaddingMSG;
	}
}