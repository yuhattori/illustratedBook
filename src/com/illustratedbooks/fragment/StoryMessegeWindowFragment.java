package com.illustratedbooks.fragment;

import com.illustratedbooks.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class StoryMessegeWindowFragment extends Fragment {
	private View mView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView =inflater.inflate(R.layout.fragment_story_messege_window, container, false);
		return mView;
	}

	@Override
	public void onStart() {
		super.onStart();
	}
}
