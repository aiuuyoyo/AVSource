package com.q.s.quicksearch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.q.s.quicksearch.fragment.BlankFragment;
import com.q.s.quicksearch.fragment.ImageFragment;
import com.q.s.quicksearch.fragment.JokeFragment;
import com.q.s.quicksearch.fragment.NavigationDrawerFragment;
import com.q.s.quicksearch.fragment.NovelFragment;
import com.q.s.quicksearch.fragment.ThunderAVSFragment;
import com.q.s.quicksearch.models.Image;
import com.q.s.quicksearch.models.ImageDao;
import com.q.s.quicksearch.models.Joke;
import com.q.s.quicksearch.models.JokeDao;
import com.q.s.quicksearch.models.Novel;
import com.q.s.quicksearch.models.NovelDao;
import com.q.s.quicksearch.models.ThunderAVSource;
import com.q.s.quicksearch.models.ThunderAVSourceDao;
import com.q.s.quicksearch.utils.GZIP;

import net.youmi.android.AdManager;
import net.youmi.android.offers.OffersManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private static final String APP_ID = "243652715a41a984";
    private static final String APP_SECRET = "16952faea5cd166a";

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isInited = prefs.getBoolean("is_inited", false);
        if (!isInited) {
            new AsyncInitTask().execute();
        }

        AdManager.getInstance(this).init(APP_ID, APP_SECRET);
        OffersManager.getInstance(this).onAppLaunch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OffersManager.getInstance(this).onAppExit();
    }

    int currentPosition = 0;

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        currentPosition = position;
        onSectionAttached(position + 1);
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                ThunderAVSFragment fragment = ThunderAVSFragment.newInstance();
                setSearchLitener(fragment);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment).commit();
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ImageFragment.newInstance()).commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, JokeFragment.newInstance()).commit();
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, NovelFragment.newInstance()).commit();
                break;
            case 4:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, BlankFragment.newInstance()).commit();
                OffersManager.getInstance(this).showOffersWall();
                break;
            default:
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
            default:
                break;
        }
        restoreActionBar();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_search);
        if (currentPosition == 0) {
            item.setVisible(true);
        } else {
            item.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            Intent search = new Intent(this, SearchActivity.class);
            startActivityForResult(search, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            String query = data.getStringExtra("query");
            litener.onSearch(query);
        }
    }

    class AsyncInitTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                    + "AVSource" + File.separator + getPackageName();
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            final AVApplication app = (AVApplication) getApplication();
            ExecutorService service = Executors.newFixedThreadPool(4);
            Future<?>[] futures = new Future[4];
            futures[0] = service.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        writeImagesToDb(app);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            futures[1] = service.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        writeQingganToDb(app);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            futures[2] = service.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        writeThunderAVSToDb(app);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            futures[3] = service.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        writeXiaohuaToDb(app);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            for (Future<?> f : futures) {
                try {
                    f.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("初始化数据...");
            progressDialog.setCancelable(false);
            progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    prefs.edit().putBoolean("is_inited", true).commit();
                    mNavigationDrawerFragment.selectItem(0);
                    Toast.makeText(MainActivity.this, "恭喜！你可以使用了", Toast.LENGTH_SHORT).show();
                }
            });
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            progressDialog = null;
        }

    }

    private void writeImagesToDb(AVApplication app) throws IOException {
        InputStream in = getAssets().open("images2.txt");
        GZIPInputStream zin = new GZIPInputStream(in);
        ObjectMapper om = new ObjectMapper();
        List<Image> data = om.readValue(zin, new TypeReference<List<Image>>() {
        });
        ImageDao imageDao = app.getImageDao();
        imageDao.deleteAll();
        for (Image m : data) {
            imageDao.insert(m);
        }
        System.out.println("Congratulations ! Images Insert");
    }

    private void writeXiaohuaToDb(AVApplication app) throws IOException {
        InputStream in = getAssets().open("jokes2.txt");
        GZIPInputStream zin = new GZIPInputStream(in);
        ObjectMapper om = new ObjectMapper();
        List<Joke> data = om.readValue(zin, new TypeReference<List<Joke>>() {
        });
        JokeDao jokeDao = app.getJokeDao();
        jokeDao.deleteAll();
        jokeDao.insertInTx(data);
        System.out.println("Congratulations ! Jokes Insert");
    }

    private void writeQingganToDb(AVApplication app) throws IOException {
        InputStream in = getAssets().open("novels2.txt");
        GZIPInputStream zin = new GZIPInputStream(in);
        ObjectMapper om = new ObjectMapper();
        List<Novel> data = om.readValue(zin, new TypeReference<List<Novel>>() {
        });
        NovelDao novelDao = app.getNovelDao();
        novelDao.deleteAll();
        novelDao.insertInTx(data);
        System.out.println("Congratulations ! Novels Insert");
    }

    private void writeThunderAVSToDb(AVApplication app) throws IOException {
        InputStream in = getAssets().open("thunders2.txt");
        File tmp = new File(Environment.getExternalStorageDirectory(), "thunders.txt");
        GZIP.readGzip(in, tmp);
        FileInputStream inputStream = new FileInputStream(tmp);

        ObjectMapper om = new ObjectMapper();
        List<ThunderAVSource> data = om.readValue(inputStream, new TypeReference<List<ThunderAVSource>>() {
        });
        inputStream.close();

        Pattern p = Pattern.compile("thunder:[a-zA-Z0-9=/+]*");
        ThunderAVSourceDao thunderAVSourceDao = app.getThunderAVSourceDao();
        thunderAVSourceDao.deleteAll();
        for (ThunderAVSource th : data) {
            StringBuilder sb = new StringBuilder(th.getContent());
            Matcher m = p.matcher(th.getContent());
            while (m.find()) {
                String url = m.group();
                int start = sb.indexOf(url);
                int end = start + url.length();
                if (start > 0) {
                    sb.replace(start, end, new StringBuilder("<a href='")
                            .append(url).append("'>").append("点击复制迅雷链接").append("</a>").toString());
                }
            }
            th.setContent(sb.toString());
            thunderAVSourceDao.insert(th);
        }
        tmp.delete();
        System.out.println("Congratulations !Tunders Insert");
    }

    private SearchLitener litener;

    public void setSearchLitener(SearchLitener litener) {
        this.litener = litener;
    }

    public interface SearchLitener {
        void onSearch(String query);
    }

}
