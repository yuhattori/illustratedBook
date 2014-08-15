//// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
//// Jad home page: http://www.kpdus.com/jad.html
//// Decompiler options: packimports(3) 
//
//package com.illustratedbooks.fragment;
//
//import android.app.Dialog;
//import android.content.SharedPreferences;
//import android.content.res.Resources;
//import android.graphics.drawable.AnimationDrawable;
//import android.os.Bundle;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.FragmentActivity;
//import android.util.DisplayMetrics;
//import android.view.Window;
//import android.widget.*;
//import java.util.ArrayList;
//import java.util.Iterator;
//
//public class ConfigDialogFragment extends DialogFragment
//{
//
//    public ConfigDialogFragment()
//    {
//        MAX_MSG_SPD = 200;
//    }
//
//    public void onActivityCreated(Bundle bundle)
//    {
//        super.onActivityCreated(bundle);
//        Dialog dialog = getDialog();
//        android.view.WindowManager.LayoutParams layoutparams = dialog.getWindow().getAttributes();
//        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
//        int i = (int)(0.5D * (double)displaymetrics.widthPixels);
//        int j = (int)(0.80000000000000004D * (double)displaymetrics.heightPixels);
//        layoutparams.width = i;
//        layoutparams.height = j;
//        dialog.getWindow().setAttributes(layoutparams);
//    }
//
//    public Dialog onCreateDialog(Bundle bundle)
//    {
//        pref = getActivity().getSharedPreferences("configdata", 0);
//        Dialog dialog = new Dialog(getActivity());
//        dialog.getWindow().requestFeature(1);
//        dialog.setContentView(0x7f030003);
//        SeekBar seekbar = (SeekBar)dialog.findViewById(0x7f080013);
//        seekbar.setProgress(MAX_MSG_SPD - pref.getInt("readingSpd", 100));
//        seekbar.setMax(-1 + MAX_MSG_SPD);
//        seekbar.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
//
//            public void onProgressChanged(SeekBar seekbar1, int i, boolean flag)
//            {
//            }
//
//            public void onStartTrackingTouch(SeekBar seekbar1)
//            {
//            }
//
//            public void onStopTrackingTouch(SeekBar seekbar1)
//            {
//                if(pref.getInt("readingSpd", 100) <= MAX_MSG_SPD - seekbar1.getProgress()) goto _L2; else goto _L1
//_L1:
//                Toast.makeText(getActivity(), "\u3088\u3080\u306F\u3084\u3055 \u304C \u306F\u3084\u304F\u306A\u308A\u307E\u3057\u305F", 0).show();
//_L4:
//                android.content.SharedPreferences.Editor editor = pref.edit();
//                editor.putInt("readingSpd", MAX_MSG_SPD - seekbar1.getProgress());
//                editor.commit();
//                return;
//_L2:
//                if(pref.getInt("readingSpd", 100) < MAX_MSG_SPD - seekbar1.getProgress())
//                    Toast.makeText(getActivity(), "\u3088\u3080\u306F\u3084\u3055 \u304C \u304A\u305D\u304F\u306A\u308A\u307E\u3057\u305F", 0).show();
//                if(true) goto _L4; else goto _L3
//_L3:
//            }
//
//            final ConfigDialogFragment this$0;
//
//            
//            {
//                this$0 = ConfigDialogFragment.this;
//                super();
//            }
//        }
//);
//        ArrayList arraylist = new ArrayList();
//        ImageView imageview = (ImageView)dialog.findViewById(0x7f08001b);
//        imageview.setBackgroundResource(0x7f020021);
//        arraylist.add((AnimationDrawable)imageview.getBackground());
//        ImageView imageview1 = (ImageView)dialog.findViewById(0x7f080018);
//        imageview1.setBackgroundResource(0x7f02000a);
//        arraylist.add((AnimationDrawable)imageview1.getBackground());
//        ImageView imageview2 = (ImageView)dialog.findViewById(0x7f080026);
//        imageview2.setBackgroundResource(0x7f020009);
//        arraylist.add((AnimationDrawable)imageview2.getBackground());
//        ImageView imageview3 = (ImageView)dialog.findViewById(0x7f080021);
//        imageview3.setBackgroundResource(0x7f020015);
//        arraylist.add((AnimationDrawable)imageview3.getBackground());
//        Iterator iterator = arraylist.iterator();
//        do
//        {
//            if(!iterator.hasNext())
//                return dialog;
//            ((AnimationDrawable)iterator.next()).start();
//        } while(true);
//    }
//
//    private int MAX_MSG_SPD;
//    private SharedPreferences pref;
//
//
//}
