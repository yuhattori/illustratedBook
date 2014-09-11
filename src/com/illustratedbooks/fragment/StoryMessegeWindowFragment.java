package com.illustratedbooks.fragment;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.illustratedbooks.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class StoryMessegeWindowFragment extends Fragment {
	private static final String TAG = StoryMessegeWindowFragment.class
			.getSimpleName();
	private View mView;
	private String mText;// 表示するテキスト
	private Canvas mCanvas;

	/* レイアウトの情報 */
	private float mPointX;// レイアウトのx座標
	private float mPointY;// レイアウトのy座標
	private int mWidth;// レイアウトの幅
	private int mHeight;// レイアウトの高さ

	/* 描写関連 */
	private ScheduledExecutorService mDrowTask;// 表示用スレッド
	private static int MSG_ALL = -1;// 文字送りをせずすべてを表示させるときに使用
	public static final Boolean ON = true;// オートモードON
	public static final Boolean OFF = false;// オートモードOFF
	private int mMsgSpd = 100;// 文字送りする速度(=画面更新速度)(ms)
	private static final int FASTEST_MSG_SPEED = 10;// 文字送りする速度=画面の更新速度の最速値
	private int mNowPrintMsgNum = 0;// 現在表示しているメッセージの文字数

	// メッセージウィンドウ
	public final static String MSGWIN_PATH = "window.jpg";// ウィンドウのレイアウト

	// テキスト
	private int FONT_SIZE = 32;// フォントのサイズ

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_story_messege_window,
				container, false);

		// Bundleから情報を取得する
		mText = getArguments().getString("Text");// 表示テキストを設定
		mPointX = getArguments().getFloat("pointX");// レイアウトのx座標の設定
		mPointY = getArguments().getFloat("pointY");// レイアウトのy座標の設定
		mWidth = getArguments().getInt("width");// レイアウトの幅の設定
		mHeight = getArguments().getInt("height");// レイアウトの高さの設定

		FrameLayout layout = (FrameLayout) mView
				.findViewById(R.id.messege_window_framelayout);

		// メッセージウィンドウの背景を設定
		ImageView imgV = new ImageView(getActivity());
		imgV.setScaleType(ScaleType.FIT_XY);
		imgV.setImageResource(R.drawable.window);
		layout.addView(imgV);

		// SurfaceViewを設定
		MessegeSurfaceView sf = new MessegeSurfaceView(getActivity());
		layout.addView(sf);
		return mView;
	}

	private class MessegeSurfaceView extends SurfaceView implements
			SurfaceHolder.Callback {

		public MessegeSurfaceView(Context context) {
			super(context);
			SurfaceHolder surfaceHolder = getHolder();
			// 背景を透明に
			surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
			// コールバックを設定
			surfaceHolder.addCallback(this);
			// このViewをトップにする
			setZOrderOnTop(true);
		}

		@Override
		public void surfaceCreated(final SurfaceHolder holder) {

			// SingleThreadScheduledExecutor による単一 Thread のインターバル実行
			mDrowTask = Executors.newSingleThreadScheduledExecutor();
			mDrowTask.scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {
					// Canvas取得しロックする
					mCanvas = holder.lockCanvas();
					// 描画処理
					if (!mText.equals("null")) {
						if (mNowPrintMsgNum != mText.length()
								&& mMsgSpd != FASTEST_MSG_SPEED) {
							// 文字送り途中の場合
							setMessege(mNowPrintMsgNum++);// メッセージウィンドウにテキストを表示
						} else {
							// 文字送り終了の場合　or 最速表示の場合
							setMessege();
							// スレッドをシャットダウン
							mDrowTask.shutdown();
						}
					}
					// LockしたCanvasを解放する
					holder.unlockCanvasAndPost(mCanvas);
				}

			}, 0, mMsgSpd, TimeUnit.MILLISECONDS);// mMsgSpdの間隔で更新
		}

		/**
		 * メッセージウィンドウにCSVに書かれているテキストをすべて表示させる
		 */
		private void setMessege() {
			mNowPrintMsgNum = mText.length();// すべて表示させるため、現在表示しているメッセージの文字数を合わせる
			setMessege(MSG_ALL);
		}

		/**
		 * メッセージウィンドウにCSVに書かれているテキストを指定字数表示させる
		 * 
		 * @param charNo
		 *            表示する文字数
		 */
		private void setMessege(int charNum) {
			int padding = mWidth / 30;
			Paint paintf = new Paint();
			paintf.setColor(Color.WHITE);
			paintf.setTextSize(FONT_SIZE);
			String message = mText;
			if (charNum == MSG_ALL)
				charNum = message.length();// 全文字表示の場合
			int maxWidth = mWidth - padding;// メッセージウィンドウの幅で改行する。
			int lineBreakPoint = Integer.MAX_VALUE;// 仮に、最大値を入れておく
			int currentIndex = 0;// 現在、原文の何文字目まで改行が入るか確認したかを保持する
			float linePointY = mPointY + padding + FONT_SIZE;// 文字を描画するY位置。改行の度にインクリメントする。
			while (charNum != 0) {
				String mesureString = message.substring(currentIndex);// 未だ表示されていない文字列のみ抽出
				lineBreakPoint = paintf.breakText(mesureString, true, maxWidth,
						null);// 表示する文字列の幅
				if (lineBreakPoint != 0) {
					String line = message.substring(currentIndex, currentIndex
							+ lineBreakPoint);// 表示する一文を抽出
					if (charNum < line.length())
						line = line.substring(0, charNum);
					charNum -= line.length();
					mCanvas.drawText(line, mPointX + padding, linePointY,
							paintf);
					linePointY += FONT_SIZE;// 改行後の位置
					currentIndex += lineBreakPoint;// 次の表示する一文の開始位置
				}
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO 自動生成されたメソッド・スタブ
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO 自動生成されたメソッド・スタブ

		}

	}

	public String getmText() {
		return mText;
	}

	public float getmPointX() {
		return mPointX;
	}

	public float getmPointY() {
		return mPointY;
	}

	public int getmWidth() {
		return mWidth;
	}

	public int getmHeight() {
		return mHeight;
	}

	public int getmMsgSpd() {
		return mMsgSpd;
	}
}
