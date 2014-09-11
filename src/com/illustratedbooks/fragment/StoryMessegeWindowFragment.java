package com.illustratedbooks.fragment;

import com.illustratedbooks.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class StoryMessegeWindowFragment extends Fragment {
	private View mView;
	private String mText;// 表示するテキスト

	/* レイアウトの情報 */
	private float mPointX;// レイアウトのx座標
	private float mPointY;// レイアウトのy座標
	private int mWidth;// レイアウトの幅
	private int mHeight;// レイアウトの高さ

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

		LinearLayout layout = (LinearLayout) mView
				.findViewById(R.id.messege_window_linearlayout);
		MessegeSurfaceView sf = new MessegeSurfaceView(getActivity());
		layout.addView(sf);
		return mView;
	}

	private class MessegeSurfaceView extends SurfaceView implements
			SurfaceHolder.Callback {

		public MessegeSurfaceView(Context context) {
			super(context);
			// TODO 自動生成されたコンストラクター・スタブ
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO 自動生成されたメソッド・スタブ

		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO 自動生成されたメソッド・スタブ
			int a = width;
			int b = height;
			b = a;
			a = b;
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO 自動生成されたメソッド・スタブ

		}

	}
}
