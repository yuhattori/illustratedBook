package com.illustratedbooks.fragment;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.illustratedbooks.R;

public class StoryFragment extends Fragment  {

	private static final String TAG = StoryFragment.class.getSimpleName();
	private Field[] mFields = R.drawable.class.getFields();
	private int mNowPrintingFg; // 表示するバックグラウンドの番号
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.story_layout, container, false);
		mNowPrintingFg = getArguments().getInt("NowPrinting");
		
		return v;
	}
}
