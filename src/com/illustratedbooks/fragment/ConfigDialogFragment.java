package com.illustratedbooks.fragment;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.util.ArrayList;
import java.util.Iterator;

import com.illustratedbooks.R;

public class ConfigDialogFragment extends DialogFragment {

	private final int MAX_MSG_SPD = 200;
	private SharedPreferences pref;
	private int mNowReadingSp;

	public Dialog onCreateDialog(Bundle bundle) {
		pref = getActivity().getSharedPreferences("configdata", 0);// configデータの取得

		Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.config_dialog_layout);

		// SeekBarの設定
		SeekBar seekbar = (SeekBar) dialog.findViewById(R.id.speed_seekbar);
		seekbar.setMax(-1 + MAX_MSG_SPD);
		mNowReadingSp = MAX_MSG_SPD - pref.getInt("readingSpd", 100);
		seekbar.setProgress(mNowReadingSp);
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// ツマミをドラッグしたときに呼ばれる
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// ツマミに触れたときに呼ばれる
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// ツマミを離したときに呼ばれる
				mNowReadingSp = MAX_MSG_SPD - seekBar.getProgress();
				Log.d("ConfigDialogFragment", "設定値:" + mNowReadingSp + " ms");
				Toast.makeText(getActivity(), "設定値:" + mNowReadingSp + " ms",
						Toast.LENGTH_SHORT).show();

				// データ保存
				Editor editor = pref.edit();
				editor.putInt("readingSpd", mNowReadingSp);
				editor.commit();
			}
		});

		// PNGアニメーションの設定
		ArrayList<AnimationDrawable> arraylist = new ArrayList<AnimationDrawable>();
		ImageView iv;
		// 右から左のフリック
		iv = (ImageView) dialog.findViewById(R.id.flick_fig);
		iv.setBackgroundResource(R.drawable.frick_right_to_left_png_anim);
		arraylist.add((AnimationDrawable) iv.getBackground());

		// タップ
		iv = (ImageView) dialog.findViewById(R.id.tap_fig);
		iv.setBackgroundResource(R.drawable.tap_png_anim);
		arraylist.add((AnimationDrawable) iv.getBackground());

		// ロングタップ
		iv = (ImageView) dialog.findViewById(R.id.longtap_fig);
		iv.setBackgroundResource(R.drawable.longtap_png_anim);
		arraylist.add((AnimationDrawable) iv.getBackground());

		// ダウンフリック
		iv = (ImageView) dialog.findViewById(R.id.downflick_fig);
		iv.setBackgroundResource(R.drawable.downfrick_png_anim);
		arraylist.add((AnimationDrawable) iv.getBackground());

		// それぞれのPNGアニメーションを設定
		Iterator<AnimationDrawable> iterator = arraylist.iterator();
		while (iterator.hasNext()) {
			((AnimationDrawable) iterator.next()).start();
		}
		return dialog;
	}

}
