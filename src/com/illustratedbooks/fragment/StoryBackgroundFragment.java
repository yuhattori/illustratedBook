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
import android.widget.Toast;

public class StoryBackgroundFragment extends Fragment {
	private View mView;
	private Bitmap mBitmap;
	private String mm ="反映されていません";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_story_background, container, false);
		ImageView bg = (ImageView) mView.findViewById(R.id.story_background);
		bg.setImageResource(R.drawable.ic_launcher);

		return mView;
	}

	@Override
	public void onStart() {
		super.onStart();
	}
	 
	

	public Bitmap getBackgroundFile() {
		return mBitmap;
	}

	public void setbackground(Bitmap mBitmap) {
		this.mBitmap = mBitmap;
	}
	
	public void settest(String a){
		this.mm = a;
	}
}
