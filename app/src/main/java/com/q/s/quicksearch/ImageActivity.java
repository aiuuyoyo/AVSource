package com.q.s.quicksearch;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.q.s.quicksearch.adapter.DraweeViewAdapter;
import com.q.s.quicksearch.models.Image;

import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends ActionBarActivity {
    private List<String> data = new ArrayList<>();
    private ListView dataView;
    private DraweeViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Image image = getIntent().getParcelableExtra("image");
        setTitle(image.getTitle());
        Spanned spanned = Html.fromHtml(image.getContent());
        ImageSpan[] imageSpans = spanned.getSpans(0, image.getContent().length(), ImageSpan.class);
        for (ImageSpan span : imageSpans) {
            data.add(span.getSource());
        }
        dataView = (ListView) findViewById(R.id.listViewTupian);
        dataView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String url = data.get(position);
                new AlertDialog.Builder(ImageActivity.this)
                        .setTitle(url)
                        .setPositiveButton("复制图片地址", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ClipboardManager cmb = (ClipboardManager) ImageActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData data = ClipData.newPlainText("text", url);
                                cmb.setPrimaryClip(data);
                                Toast.makeText(ImageActivity.this, "复制成功！", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });
        adapter = new DraweeViewAdapter(this, data);
        dataView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
