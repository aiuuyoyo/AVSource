package com.q.s.quicksearch.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class HandlerUtils {
	
	public static final Handler HANDLER = new Handler(Looper.getMainLooper());
	
	public static void showToast(Context context, int id) {
		final Context localContext = context;
		final int localId = id;
		HANDLER.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(localContext, localId, Toast.LENGTH_SHORT).show();
			}
		});
	}
	public static void showToast(Context context, String text) {
		final Context localContext = context;
		final String localtext = text;
		HANDLER.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(localContext, localtext, Toast.LENGTH_SHORT).show();
			}
		});
	}

}
