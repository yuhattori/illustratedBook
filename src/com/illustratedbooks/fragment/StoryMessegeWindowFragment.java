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

public class StoryMessegeWindowFragment extends Fragment {
	private View mView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView =inflater.inflate(R.layout.fragment_story_messege_window, container, false);
		MessegeSurfaceView sf = new MessegeSurfaceView(getActivity());
		return mView;
	}

	@Override
	public void onStart() {
		super.onStart();
	}
	
	private class MessegeSurfaceView extends SurfaceView implements
	SurfaceHolder.Callback{

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
			
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO 自動生成されたメソッド・スタブ
			
		}
		
	}
}
