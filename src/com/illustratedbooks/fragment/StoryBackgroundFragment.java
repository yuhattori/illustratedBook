package com.illustratedbooks.fragment;

import com.illustratedbooks.R;

import android.R.string;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils.StringSplitter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class StoryBackgroundFragment extends Fragment {
	private View mView;
	/*レイアウトの情報*/
	private float mPointX;//レイアウトのx座標
	private float mPointY;//レイアウトのy座標
	private int mWidth;//レイアウトの幅
	private int mHeight;//レイアウトの高さ
	
	/*画像ファイル*/
	private Bitmap mBgFig;//画像ファイル
	private int mDrownBG_w;
	private int mDrownBG_h;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_story_background, container,
				false);
		//Bundleから情報を取得する
		mBgFig=getArguments().getParcelable("BgFig");//画像ファイルを設定
		mPointX=getArguments().getFloat("pointX");//レイアウトのx座標の設定
		mPointY=getArguments().getFloat("pointY");//レイアウトのy座標の設定
		mWidth=getArguments().getInt("width");//レイアウトの幅の設定
		mHeight=getArguments().getInt("height");//レイアウトの高さの設定
		
		//画像ファイルのサイズを変更する
		resizeFig();
		
		//画像を登録する
		ImageView bg = (ImageView) mView.findViewById(R.id.story_background);
		bg.setImageBitmap(mBgFig);
		
		return mView;
	}
	
	/**
	 * 画像ファイルのサイズを変更する
	 */
	public void resizeFig(){
		if (mBgFig.getHeight() >= mBgFig.getWidth()) {
			// 縦長の画像の場合
			mDrownBG_w = mHeight * (mBgFig.getWidth() / mBgFig.getHeight());// 背景画像の幅
			mDrownBG_h = mHeight;// 背景画像の高さ
		} else {
			// 横長の画像の場合
			mDrownBG_w = mWidth;// 背景画像の幅
			mDrownBG_h = mWidth * (mBgFig.getHeight() / mBgFig.getWidth()); // 背景画像の高さ
		}
		mBgFig=	Bitmap.createScaledBitmap(mBgFig,mDrownBG_w,mDrownBG_h,true);
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}
}
