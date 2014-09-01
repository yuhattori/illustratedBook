package com.illustratedbooks.story;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

public class StorySurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {
	private static final String TAG = StorySurfaceView.class.getSimpleName();
	Context mContext;
	Canvas mCanvas;

	/* 描写関連 */
	private ScheduledExecutorService mDrowTask;// 表示用スレッド
	private ScheduledExecutorService mAutoModeTask;// オートモード用スレッド
	private Boolean mAutoModeFlag = false;// オートモード用のフラグ
	private int mAutoModeSp = 1000;// オートモードのスピード
	private static int MSG_ALL = -1;// 文字送りをせずすべてを表示させるときに使用
	public static final Boolean ON = true;// オートモードON
	public static final Boolean OFF = false;// オートモードOFF
	private int mMsgSpd = 100;// 文字送りする速度(=画面更新速度)(ms)
	private final int MAXIMUM_MSG_SPEED = 10;// 文字送りする速度=画面の更新速度の最速値
	private int mNowPrintMsgNum = 0;// 現在表示しているメッセージの文字数

	// 背景
	private String mBgPath; // 背景画像のパス
	private Bitmap mBgFig;// 背景画像
	private int mDp_w;// ディスプレイの幅
	private int mDp_h;// ディスプレイの高さ
	private int mDrow_w;// 背景を実際に表示するときの画像の幅
	private int mDrow_h;// 背景を実際に表示するときの画像の高さ
	private int mDrowPos_l = 0;// 背景のx軸の表示位置
	private int mDrowPos_t = 0;// 背景のy軸の表示位置
	// テキスト
	private Paint mPaintf; // テキストのプロパティ
	private int FONT_SIZE = 32;// フォントのサイズ
	// メッセージウィンドウ
	public final static String MSGWIN_PATH = "window.jpg";// ウィンドウのレイアウト
	private Bitmap mMsgWin;// ウィンドウ画像
	private int mPadding;// 背景のy軸の表示位置
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

	/**
	 * コンストラクタ
	 * 
	 * @param Context
	 *            context
	 */
	public StorySurfaceView(Context context) {
		super(context);
		mContext = context;
		Log.d(TAG, "You tought startBtn to come this screen.");
		init();
	}

	/**
	 * つづきからボタンを押された時のコンストラクタ
	 * 
	 * @param Context
	 *            context
	 * @param String
	 *            clickedBtn　遷移するときに押されたボタン名
	 */
	public StorySurfaceView(Context context, String clickedBtn) {
		super(context);
		mContext = context;
		Log.d(TAG, "You tought " + clickedBtn + " to come this screen.");
		init();
		
		/* 保存データを読み込む。前回の表示していたROWの読み込み */
		SharedPreferences sPref = mContext.getSharedPreferences("savedata"
				+ CSV_FILE_NAME, Context.MODE_PRIVATE);
		mCSVColumnNo = Integer.valueOf(sPref.getString("savedRowNo", "1"));
		Log.d(TAG, "RowNo of Saved data is " + mCSVColumnNo);
		
		
	}

	/**
	 * 共通の初期化処理
	 */
	private void init() {
		SurfaceHolder surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		
		/*設定データの読み込み*/
		SharedPreferences cPref = mContext.getSharedPreferences("configdata", 0);
		setMsgSpd(cPref.getInt("readingSpd", 100));//メッセージスピードの設定
		if(mMsgSpd < MAXIMUM_MSG_SPEED)
			setMsgSpd(MAXIMUM_MSG_SPEED);//最速表示の場合

		// CSVファイルの情報の読み込み
		try {
			readCSV();
		} catch (IOException e) {
			// TODO CSVファイル読み込みの失敗のポップアップを表示する
			Log.e(TAG, "failed reading CSVfile");
			e.printStackTrace();
		}
	}

	// SurfaceView生成時に呼び出される(初期画面の描画)
	public void surfaceCreated(final SurfaceHolder holder) {

		mBgPath = "";// 背景画像のパスを格納するメンバの初期化

		// SingleThreadScheduledExecutor による単一 Thread のインターバル実行
		mDrowTask = Executors.newSingleThreadScheduledExecutor();
		mDrowTask.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				// Canvas取得しロックする
				mCanvas = holder.lockCanvas();

				// *背景画像*//
				if (!mBgPath.equalsIgnoreCase("background/"
						+ mCSVdata.get(mCSVColumnNo)[BACK_GROUND])) {
					// 背景画像が変わっていた場合
					mBgPath = "background/"
							+ mCSVdata.get(mCSVColumnNo)[BACK_GROUND];
					try {
						mBgFig = resizeBg(mContext, BitmapFactory
								.decodeStream(getResources().getAssets().open(
										mBgPath)));// リサイズ済背景画像
					} catch (IOException e) {
						Log.e(TAG, "failed reading background  image file");
						Toast.makeText(getContext(),
								"failed reading background image file", 0)
								.show();
						e.printStackTrace();
					}
				}

				// *メッセージウィンドウの画像設定*//
				if (mMsgWin == null)
					try {
						mMsgWin = BitmapFactory.decodeStream(getResources()
								.getAssets().open(MSGWIN_PATH));
						mPadding = mDrow_w / 30;
						mMsgWin = Bitmap.createScaledBitmap(mMsgWin, mDrow_w
								- mPadding * 2, mDrow_h / 4, true);
						mPaintw = new Paint();
						mPaintw.setAlpha(ALPHA);// 透過度を設定

					} catch (IOException e) {
						// TODO ウィンドウ背景画像の読み込みエラー
						Log.e(TAG, "failed reading messege window file");
						e.printStackTrace();
					}

				// 描画処理
				mCanvas.drawBitmap(mBgFig, mDrowPos_l, mDrowPos_t, null);// 背景を表示

				if (!mCSVdata.get(mCSVColumnNo)[TEXT].equals("null")
						&& mWindowFlag) {
					mCanvas.drawBitmap(mMsgWin, mDrowPos_l + mPadding,
							mDrowPos_t + mDrow_h * 3 / 4 - mPadding * 2,
							mPaintw);// メッセージウィンドウを表示

					if (mNowPrintMsgNum != mCSVdata.get(mCSVColumnNo)[TEXT]
							.length() && mMsgSpd != MAXIMUM_MSG_SPEED) {
						// 文字送り途中の場合
						setMessege(mNowPrintMsgNum++);// メッセージウィンドウにテキストを表示
					} else {
						// 文字送り終了の場合　or 最速表示の場合
						setMessege();
					}
				}
				// LockしたCanvasを解放する
				holder.unlockCanvasAndPost(mCanvas);
			}

		}, 0, mMsgSpd, TimeUnit.MILLISECONDS);// 100ms後にINTERVAL_PERIODの間隔で更新
	}

	/**
	 * メッセージウィンドウにCSVに書かれているテキストをすべて表示させる
	 */
	private void setMessege() {
		mNowPrintMsgNum = mCSVdata.get(mCSVColumnNo)[TEXT].length();
		setMessege(MSG_ALL);
	}

	/**
	 * メッセージウィンドウにCSVに書かれているテキストを指定字数表示させる
	 * 
	 * @param charNo
	 *            表示する文字数
	 * 
	 */
	private void setMessege(int charNum) {
		mPaintf = new Paint();
		mPaintf.setColor(Color.WHITE);
		mPaintf.setTextSize(FONT_SIZE);
		String message = mCSVdata.get(mCSVColumnNo)[TEXT];
		if (charNum == MSG_ALL)
			charNum = message.length();// 全文字表示の場合
		int maxWidth = mDrow_w - mPadding * 2;// paddingを含めたメッセージウィンドウの幅で改行する。
		int lineBreakPoint = Integer.MAX_VALUE;// 仮に、最大値を入れておく
		int currentIndex = 0;// 現在、原文の何文字目まで改行が入るか確認したかを保持する
		int linePointY = mDrowPos_t + mDrow_h * 3 / 4 - mPadding * 2
				+ FONT_SIZE;// 文字を描画するY位置。改行の度にインクリメントする。

		while (charNum != 0) {
			String mesureString = message.substring(currentIndex);// 未だ表示されていない文字列のみ抽出
			lineBreakPoint = mPaintf.breakText(mesureString, true, maxWidth,
					null);// 表示する文字列の幅
			if (lineBreakPoint != 0) {
				String line = message.substring(currentIndex, currentIndex
						+ lineBreakPoint);// 表示する一文を抽出
				if (charNum < line.length())
					line = line.substring(0, charNum);
				charNum -= line.length();
				mCanvas.drawText(line, mDrowPos_l + mPadding, linePointY,
						mPaintf);
				linePointY += FONT_SIZE;// 改行後の位置
				currentIndex += lineBreakPoint;// 次の表示する一文の開始位置
			}
		}
	}

	// (画面の更新処理)SurfaceView変更時に呼び出される
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d(TAG, "surfaceChanged");
		// 処理がない場合も必要
	}

	// (画面の削除、削除後の処理)SurfaceView破棄時に呼び出される。処理がない場合も必要
	public void surfaceDestroyed(SurfaceHolder holder) {
		// データ保存
		SharedPreferences pref = mContext.getSharedPreferences("savedata"
				+ CSV_FILE_NAME, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString("savedRowNo", mCSVdata.get(mCSVColumnNo)[ROW]);
		editor.commit();

		// スレッドをシャットダウン
		mDrowTask.shutdown();
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

	/**
	 * 背景に使用する画像のリサイズを行う
	 * 
	 * @return Bitmap
	 */
	private Bitmap resizeBg(Context context, Bitmap bmp) {
		/* 画像のリサイズ */
		// WindowManager取得
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display dp = wm.getDefaultDisplay();
		// ディスプレイサイズ取得
		Point size = new Point();
		dp.getSize(size);
		mDp_w = size.x;
		mDp_h = size.y;

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
		return Bitmap.createScaledBitmap(bmp, mDrow_w, mDrow_h, true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {

		case MotionEvent.ACTION_DOWN:
			// 画面に初めて触れたとき、その時の情報（位置および時間）を取得
			Log.d(TAG, "DOWN");
			mBefore_x = ev.getX();
			mBefore_y = ev.getY();
			mTouchDownTimeMillis = System.currentTimeMillis();
			break;

		case MotionEvent.ACTION_UP:
			// 指を離したとき
			Log.d(TAG, "UP");
			switch (judgeGesture(ev)) {

			case SINGLE_TAP:
				/* タップを検出した時 */
				Log.d(TAG, "SINGLE_TAP");
				if (mNowPrintMsgNum != mCSVdata.get(mCSVColumnNo)[TEXT]
						.length()) {
					// 文字送り途中の場合
					mNowPrintMsgNum = mCSVdata.get(mCSVColumnNo)[TEXT].length();
				} else {
					// シナリオを一つ進める
					nextCoulumn();
					mNowPrintMsgNum = 0;
				}
				break;

			case LONG_TAP:
				Log.d(TAG, "LONG_TAP");
				/* ロングタップを検出した時 */

				// オートモードに移行するか
				if (!mAutoModeFlag) {
					autoMode(ON);
				} else {
					autoMode(OFF);
				}
				break;

			case FLICK_LEFT:
				/* 左にフリックしたとき */
				// シナリオを一つ戻す
				Log.d(TAG, "FLICK_LEFT");
				backColumn();
				mNowPrintMsgNum = 0;
				break;

			case FLICK_RIGHT:
				/* 右にフリックしたとき */
				// シナリオを一つ進める
				Log.d(TAG, "FLICK_RIGHT");
				nextCoulumn();
				mNowPrintMsgNum = 0;
				break;
			case FLICK_UP:
				/* 上にフリックしたとき */
				Log.d(TAG, "FLICK_UP");
				mWindowFlag = true;
				break;
			case FLICK_DOWN:
				/* 下にフリックしたとき */
				Log.d(TAG, "FLICK_DOWN");
				// ウィンドウを消すかどうかのフラグジャッジ
				if (mWindowFlag) {
					mWindowFlag = false;
				} else {
					mWindowFlag = true;
				}
				break;

			default:
				break;
			}
			break;
		}
		return true;
	}

	/**
	 * 前の行に移動
	 */
	private void backColumn() {
		if (mCSVColumnNo > 1) {
			// 最初でない場合
			mCSVColumnNo -= 1;// シナリオを一つ戻す
			mWindowFlag = true;// ロングタップのウィンドウフラグの初期化
			mNowPrintMsgNum = 0;// 現在表示しているメッセージの文字数を初期化
		} else {
			// TODO 最初の場合
		}
	}

	/**
	 * 次の行へ移動
	 */
	private void nextCoulumn() {
		if (mCSVColumnNo < mCSVdata.size() - 1) {
			// 最後でなかった場合
			mCSVColumnNo += 1;// シナリオを一つ進める
			mWindowFlag = true;// ロングタップのウィンドウフラグの初期化
			mNowPrintMsgNum = 0;// 現在表示しているメッセージの文字数を初期化
		} else {
			// TODO　最後だった場合
		}
	}

	/**
	 * 指を離した時のジェスチャーの種類を判別する
	 * 
	 * @return gesture ジェスチャーの種類 SINGLE_TAP, LONG_TAP, FLICK_LEFT, FLICK_RIGHT
	 */
	private int judgeGesture(MotionEvent ev) {

		int gesture;
		if (Math.abs(mBefore_x - ev.getX()) > 100) {
			// 移動した距離により左右のフリックと判断されたとき
			// 斜めにフリックされた場合こちらが上下のフリックより優先される
			if (mBefore_x < ev.getX()) {
				// 右にフリックした時
				gesture = FLICK_RIGHT;
			} else {
				// 左にフリックした時
				gesture = FLICK_LEFT;
			}
		} else if ((Math.abs(mBefore_y - ev.getY()) > 100)) {
			// 移動した距離により上下のフリックと判断されたとき
			if (mBefore_y > ev.getY()) {
				// 上にフリックした時
				gesture = FLICK_UP;
			} else {
				// 左にフリックした時
				gesture = FLICK_DOWN;
			}
		} else {
			// タップであると判断した時
			if (Math.abs(System.currentTimeMillis() - mTouchDownTimeMillis) > 500) {
				// ロングタップしたとき
				gesture = LONG_TAP;
			} else {
				// シングルタップしたとき
				gesture = SINGLE_TAP;
			}
		}
		return gesture;
	}

	/**
	 * オートモードへ移行するか否かを設定する
	 * 
	 * @param flag
	 *            　ture＝オートモード　false＝オートモード解除
	 */
	public void autoMode(Boolean flag) {
		if (flag == ON) {
			// オートモード開始
			Log.d(TAG, "オートモードON");
			Toast.makeText(getContext(), "オートモードON", Toast.LENGTH_SHORT).show();
			mAutoModeFlag = true;
			mAutoModeTask = Executors.newSingleThreadScheduledExecutor();
			mAutoModeTask.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					if (mNowPrintMsgNum == mCSVdata.get(mCSVColumnNo)[TEXT]
							.length() || mMsgSpd == MAXIMUM_MSG_SPEED)
						// すべてメッセージが表示されている場合
						try {
							Thread.sleep(1000);// 文字送りが全部表示されると同時に次の文字が表示される事を防ぐ
							nextCoulumn();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				}
			}, 0, mAutoModeSp, TimeUnit.MILLISECONDS);
		} else {
			// オートモード解除
			Log.d(TAG, "オートモードOFF");
			Toast.makeText(getContext(), "オートモードOFF", Toast.LENGTH_SHORT)
					.show();
			mAutoModeFlag = false;
			mAutoModeTask.shutdown();
			mAutoModeTask = null;
		}
	}

	public int getMsgSpd() {
		return mMsgSpd;
	}

	public void setMsgSpd(int mMsgSpd) {
		this.mMsgSpd = mMsgSpd;
	}

	/**
	 * 現在オートモード中かを判断する
	 * @return mAutoModeFlag ON :true OFF : false
	 */
	public Boolean isAutoMode() {
		return mAutoModeFlag;
	}
}