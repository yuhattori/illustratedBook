package com.illustratedbooks.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.illustratedbooks.R;
import com.illustratedbooks.fragment.StoryBackgroundFragment;
import com.illustratedbooks.fragment.StoryMessegeWindowFragment;
import com.illustratedbooks.fragment.TopFragment;
import com.illustratedbooks.story.StoryDisplayData;
import com.illustratedbooks.story.StorySurfaceView;

public class StoryActivity extends FragmentActivity {

	private static final String TAG = StoryActivity.class.getSimpleName();
	private static final String BACKGROUND_FLAGMENT = "background_frangment";
	private static final String MESSEGE_WINDOW_FLAGMENT = "messege_window_frangment";

	private Activity mAct;

	private LinearLayout mBgLayout;
	private LinearLayout mMsgLayout;

	/* 描写関連 */
	private ScheduledExecutorService mDrowTask;// 表示用スレッド
	private ScheduledExecutorService mAutoModeTask;// オートモード用スレッド
	private Boolean mAutoModeFlag = false;// オートモード用のフラグ
	private int mAutoModeSp = 1000;// オートモードのスピード
	private static int MSG_ALL = -1;// 文字送りをせずすべてを表示させるときに使用
	public static final Boolean ON = true;// オートモードON
	public static final Boolean OFF = false;// オートモードOFF
	private int mMsgSpd = 100;// 文字送りする速度(=画面更新速度)(ms)
	private static final int FASTEST_MSG_SPEED = 10;// 文字送りする速度=画面の更新速度の最速値
	private int mNowPrintMsgNum = 0;// 現在表示しているメッセージの文字数

	// 背景
	private String mBgPath; // 背景画像のパス
	private Bitmap mBgFig;// 背景画像

	// テキスト
	private Paint mPaintf; // テキストのプロパティ
	private int FONT_SIZE = 32;// フォントのサイズ
	private String mText;// 表示するテキスト

	// メッセージウィンドウ
	public final static String MSGWIN_PATH = "window.jpg";// ウィンドウのレイアウト
	private Bitmap mMsgWin;// ウィンドウ画像
	private Paint mPaintw; // メッセージウィンドウのプロパティ
	private final int ALPHA = 140;// 透過度

	/* CSVファイル関連 */

	public final static String CSV_FILE_NAME = "scenarios.csv";// CSVファイル名
	private ArrayList<String[]> mCSVdata = new ArrayList<String[]>(); // CSVファイルの二次元データ
	private int mCSVColumnNo = 1;// CSVファイルにはヘッダーがあるため0行目は飛ばす
	public final int ROW = 0;
	public final int BACK_GROUND = 1;
	public final int BGM = 2;
	public final int SE = 3;
	public final int TEXT = 4;

	/* タッチイベント関係 */
	final int SINGLE_TAP = 1;
	final int LONG_TAP = 2;
	final int DOUBLE_TAP = 3;// 未実装
	final int FLICK_RIGHT = 4;
	final int FLICK_LEFT = 5;
	final int FLICK_UP = 6;
	final int FLICK_DOWN = 7;

	private float mBefore_x;// MOVE後のUPでｘ座標と比較する
	private float mBefore_y;// MOVE後のUPでｙ座標と比較する
	private long mTouchDownTimeMillis;// 画面にタッチした瞬間の時間
	private Boolean mWindowFlag = true;// ロングタップ時のウィンドウを消すかどうかの判断

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mAct = this;
		init();// 初期化
		setContentView(R.layout.activity_story);// ビューを設定

		Intent intent = getIntent();
		String clickedBtn = intent.getStringExtra("clickedBtn");
		if (clickedBtn.equalsIgnoreCase("startBtn")) {
			// はじめからボタンを押してStoryActivityに来たとき
		} else {
			// つづきからボタンを押してStoryActivityに来たとき
			/* 保存データを読み込む。前回の表示していたROWの読み込み */
			SharedPreferences sPref = mAct.getSharedPreferences("savedata"
					+ CSV_FILE_NAME, Context.MODE_PRIVATE);
			mCSVColumnNo = Integer.valueOf(sPref.getString("savedRowNo", "1"));
			Log.d(TAG, "RowNo of Saved data is " + mCSVColumnNo);
		}
		/* 背景画像を取得 */
		try {
			// 背景の画像のPATHを取得
			mBgPath = "background/" + mCSVdata.get(mCSVColumnNo)[BACK_GROUND];
			// 背景画像を取得
			mBgFig = BitmapFactory.decodeStream(getResources().getAssets()
					.open(mBgPath));
		} catch (IOException e) {
			Log.e(TAG, "failed reading background  image file");
			Toast.makeText(mAct, "failed reading background image file",
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	/**
	 * 共通の初期化処理
	 */
	private void init() {
		/* 設定データの読み込み */
		SharedPreferences cPref = mAct.getSharedPreferences("configdata", 0);
		mMsgSpd = (cPref.getInt("readingSpd", 100));// メッセージスピードの設定
		if (mMsgSpd < FASTEST_MSG_SPEED)
			mMsgSpd = FASTEST_MSG_SPEED;// 最速表示の場合

		/* CSVファイルの情報の読み込み */
		try {
			readCSV();
		} catch (IOException e) {
			// TODO CSVファイル読み込みの失敗のポップアップを表示する
			Log.e(TAG, "failed reading CSVfile");
			e.printStackTrace();
		}
	}

	/**
	 * assetsフォルダ直下におかれたCSVファイルの読み込みを行う
	 * 
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private void readCSV() throws IOException, UnsupportedEncodingException {
		AssetManager assets = getResources().getAssets();
		InputStream in = assets.open(CSV_FILE_NAME);
		BufferedReader br = new BufferedReader(
				new InputStreamReader(in, "SJIS"));// S-JISに変換
		String str;

		while ((str = br.readLine()) != null) {
			String[] array = str.split(",");
			mCSVdata.add(array);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		/* テキストを取得 */
		mText = mCSVdata.get(mCSVColumnNo)[TEXT];

		/* onCreateではレンダリングが終わっていないためここで行う */
		// フラグメントを入れるレイアウトを取得
		mBgLayout = (LinearLayout) mAct.findViewById(R.id.background_layout);
		mMsgLayout = (LinearLayout) mAct
				.findViewById(R.id.messege_window_layout);
		// Fragmentを管理するFragmentManagerを取得
		FragmentManager manager = getSupportFragmentManager();
		// 追加や削除などを1つの処理としてまとめるためのトランザクションクラスを取得
		FragmentTransaction ft = manager.beginTransaction();

		/* StoryBackgroundFragmentを設定 */
		StoryBackgroundFragment bgf = new StoryBackgroundFragment();
		// 背景画像を登録
		Bundle bgBundle = new Bundle();
		bgBundle.putParcelable("BgFig", mBgFig);
		bgBundle.putFloat("pointX", mBgLayout.getX());// backgroundのX座標
		bgBundle.putFloat("pointY", mBgLayout.getY());// backgroundのY座標
		bgBundle.putInt("width", mBgLayout.getWidth());// レイアウトの幅
		bgBundle.putInt("height", mBgLayout.getHeight());// レイアウトの高さ
		bgf.setArguments(bgBundle);

		/* StoryMessegeWindowFragmentを設定 */
		StoryMessegeWindowFragment mwf = new StoryMessegeWindowFragment();
		Bundle mwBundle = new Bundle();
		mwBundle.putString("Text", mText);
		mwBundle.putFloat("pointX", mMsgLayout.getX());// MessegeWindowのX座標
		mwBundle.putFloat("pointY", mMsgLayout.getY());// MessegeWindowのY座標
		mwBundle.putInt("width", mMsgLayout.getWidth());// レイアウトの幅
		mwBundle.putInt("height", mMsgLayout.getHeight());// レイアウトの高さ
		mwf.setArguments(mwBundle);

		// Fragment をスタックに追加する
		ft.add(mBgLayout.getId(), bgf, BACKGROUND_FLAGMENT);
		ft.add(mMsgLayout.getId(), mwf, MESSEGE_WINDOW_FLAGMENT);
		ft.commit();
	}

	/**
	 * フラグメントを変更する
	 * @param fragment
	 *            スタックへ登録するフラグメント
	 */
	public void replaceFragmentToStack(Fragment fragment) {
		// フラグメントのクラス名を取得
		String fragmentName = fragment.getClass().getSimpleName();

		// Fragmentを管理するFragmentManagerを取得
		FragmentManager manager = getSupportFragmentManager();
		// 追加や削除などを1つの処理としてまとめるためのトランザクションクラスを取得(新しく作らないと”commit already
		// called”とエラーが出る)
		FragmentTransaction ft = manager.beginTransaction();

		if (fragmentName.equals("StoryBackgroundFragment")) {
			/* StoryBackgroundFragmentの場合 */
			// Layout位置先の指定
			ft.replace(mBgLayout.getId(), fragment);
		} else if (fragmentName.equals("StoryMessegeWindowFragment")) {
			/* StoryMessegeWindowFragmentの場合 */
			// Layout位置先の指定
			ft.replace(mMsgLayout.getId(), fragment);
		} else {
			// それ以外のフラグメントの時
			Log.e(TAG, "not exist " + fragmentName);
			Toast.makeText(mAct, "エラーが発生しました", Toast.LENGTH_SHORT).show();
			ft.commit();
			return;
		}
		// Fragmentの変化時のアニメーションを指定
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.addToBackStack(null);
		ft.commit();
	}

	public void replaceBackground(Bitmap bmp) {
		StoryBackgroundFragment bgf = new StoryBackgroundFragment();
		replaceFragmentToStack(bgf);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			// バックボタンが押されたとき
			
			
//			if (mStSf.isAutoMode())
//				// オートモード中の場合は解除
//				mStSf.autoMode(StorySurfaceView.OFF);

			AlertDialog.Builder alertDialog = new AlertDialog.Builder(mAct);

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
							mAct.finish();// Activityの終了
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 画面が閉じる時にデータを保存
		SharedPreferences pref = mAct.getSharedPreferences("savedata"
				+ CSV_FILE_NAME, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString("savedRowNo", mCSVdata.get(mCSVColumnNo)[ROW]);
		editor.commit();
	}
}
