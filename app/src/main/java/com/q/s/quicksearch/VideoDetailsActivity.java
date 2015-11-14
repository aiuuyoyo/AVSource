package com.q.s.quicksearch;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.q.s.quicksearch.models.ThunderAVSource;
import com.q.s.quicksearch.utils.HandlerUtils;
import com.q.s.quicksearch.utils.HtmlMovementMethod;
import com.q.s.quicksearch.utils.SpanUtils;
import com.squareup.okhttp.OkHttpClient;

public class VideoDetailsActivity extends ActionBarActivity {
    private TextView detailsText;
    private ThunderAVSource src;
    private ScrollView parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        parent = (ScrollView) findViewById(R.id.id_details_container);
        detailsText = (TextView) findViewById(R.id.id_details_text);
        src = getIntent().getParcelableExtra("source");
        detailsText.setMovementMethod(HtmlMovementMethod.getInstance());
        setTitle(src.getTitle());

        load();
    }

    private void load() {
        parent.post(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        totalCount = SpanUtils.toSpannableString(VideoDetailsActivity.this, src.getContent(),
                                parent.getMeasuredWidth(), parent.getMeasuredHeight());
                        HandlerUtils.HANDLER.post(new Runnable() {
                            @Override
                            public void run() {
                                detailsText.setText(SpanUtils.spannableString);
                            }
                        });
                    }
                }).start();
            }
        });
    }

    public void refresh() {
        count();
        parent.post(new Runnable() {
            @Override
            public void run() {
                HandlerUtils.HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        detailsText.setText("");
                        detailsText.setText(SpanUtils.spannableString);
                    }
                });
            }
        });
    }

    private int totalCount = 0;
    private int completeCount = 0;

    /**
     * 对加载成功或失败的次数计数，当等于总图片数目时，停止菜单栏加载动画
     */
    public void count() {
        completeCount++;
        if (completeCount == totalCount) {
            loadImageView.clearAnimation();
        }
    }

    @Override
    protected void onDestroy() {
        loadImageView.clearAnimation();
        SpanUtils.spannableString.clear();
        super.onDestroy();
    }

    private ImageView loadImageView;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (loadImageView == null) {
            MenuItem item = menu.findItem(R.id.actiont_refresh);
            loadImageView = new ImageView(this);
            loadImageView.setImageResource(R.mipmap.ic_autorenew_black);
            loadImageView.setAdjustViewBounds(true);
            int padding = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
            loadImageView.setPadding(padding, padding, padding, padding);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.roate);
            animation.setInterpolator(new LinearInterpolator());
            loadImageView.startAnimation(animation);
            loadImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (totalCount == completeCount) {
                        refresh();
                    }
                }
            });
            item.setActionView(loadImageView);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_video_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
