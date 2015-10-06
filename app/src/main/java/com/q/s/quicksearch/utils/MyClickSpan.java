package com.q.s.quicksearch.utils;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

import net.youmi.android.offers.OffersManager;
import net.youmi.android.offers.PointsManager;

import java.util.Calendar;
import java.util.Date;

/**
 * 自定义超链接的点击处理方式
 */
public class MyClickSpan extends ClickableSpan {

    private Context mContext;
    private String mUrl;

    public MyClickSpan(String url, Context context) {
        this.mUrl = url;
        this.mContext = context;
    }

    @Override
    public void onClick(View widget) {
        if (!NetUtils.checkNetConnectivityAvailable(mContext)) {
            Toast.makeText(mContext, "网络不可用，请检查网络", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean isSuccess = PointsManager.getInstance(mContext).spendPoints(10);
        if (isSuccess) {
            ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData data = ClipData.newPlainText("text", mUrl);
            cmb.setPrimaryClip(data);
            Toast.makeText(mContext, "复制成功！消耗10积分", Toast.LENGTH_SHORT).show();
        } else {
            int points = PointsManager.getInstance(mContext).queryPoints();
            if (points < 10) {
                alertTips(points);
            } else {
                Toast.makeText(mContext, "获取积分失败！请检查网络", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(Color.parseColor("#2e8ece"));
        ds.setUnderlineText(false);
    }

    private static final String DAY_OF_WEEK = "day_" + Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

    private void alertTips(int points) {
        new AlertDialog.Builder(mContext)
                .setMessage("积分余额（" + points + "）不足！赚取积分后可复制！或者签到可获取50积分！")
                .setNegativeButton("签到", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                        boolean hasMarked = preferences.getBoolean(DAY_OF_WEEK, false);
                        if (hasMarked) {
                            Toast.makeText(mContext, "您已经签到！请明天再来", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        boolean isSuccess = PointsManager.getInstance(mContext).awardPoints(50);
                        if (isSuccess) {
                            Toast.makeText(mContext, "签到成功！获取50积分", Toast.LENGTH_SHORT).show();
                            preferences.edit().putBoolean(DAY_OF_WEEK, true).commit();
                            int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                            String lastDay = null;
                            if (day == 1) {
                                lastDay = "day_7";
                            } else {
                                lastDay = "day_" + (day - 1);
                            }
                            if (preferences.contains(lastDay)) {
                                preferences.edit().remove(lastDay).commit();
                            }
                        } else {
                            Toast.makeText(mContext, "签到失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setPositiveButton("赚积分", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OffersManager.getInstance(mContext).showOffersWall();
                    }
                }).create().show();
    }

}
