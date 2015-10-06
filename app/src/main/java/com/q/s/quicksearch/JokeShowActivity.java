package com.q.s.quicksearch;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

import com.q.s.quicksearch.models.Joke;

public class JokeShowActivity extends ActionBarActivity {
    private Joke joke;
    private TextView novelText;
    private ScrollView parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel_show);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        joke = getIntent().getParcelableExtra("joke");
        setTitle(joke.getTitle());
        parent = (ScrollView) findViewById(R.id.id_novel_show_container);
        novelText = (TextView) findViewById(R.id.id_novel_show_text);
        parent.post(new Runnable() {
            @Override
            public void run() {
                novelText.setText(Html.fromHtml(joke.getContent()));
            }
        });
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
