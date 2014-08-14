package com.illustratedbooks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.illustratedbooks.story.StorySurfaceView;

public class StoryActivity extends FragmentActivity {
	
	private static final String TAG = StoryActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		String clickedBtn = intent.getStringExtra("clickedBtn");
		if (clickedBtn.equalsIgnoreCase("startBtn")) {
			//はじめからボタンを押してStoryActivityに来たとき
			setContentView(new StorySurfaceView(this));
		} else {
			//つづきからボタンを押してStoryActivityに来たとき
			setContentView(new StorySurfaceView(this,clickedBtn));
		}

	}
}
