package com.q.s.quicksearch;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.q.s.quicksearch.models.DaoMaster;
import com.q.s.quicksearch.models.DaoSession;
import com.q.s.quicksearch.models.ImageDao;
import com.q.s.quicksearch.models.JokeDao;
import com.q.s.quicksearch.models.NovelDao;
import com.q.s.quicksearch.models.ThunderAVSourceDao;
import com.q.s.quicksearch.utils.DatabaseContext;

/**
 * Created by Administrator on 2015/7/18.
 */
public class AVApplication extends Application {
    private DaoSession daoSession;
    private SQLiteDatabase db;
    private ThunderAVSourceDao thunderAVSourceDao;
    private NovelDao novelDao;
    private JokeDao jokeDao;
    private ImageDao imageDao;

    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(this);
        init();
    }

    public void init() {
        if (db != null) {
            db.close();
        }
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(new DatabaseContext(this), "avsource=db", null);
        db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        thunderAVSourceDao = daoSession.getThunderAVSourceDao();
        novelDao = daoSession.getNovelDao();
        jokeDao = daoSession.getJokeDao();
        imageDao = daoSession.getImageDao();
    }

    public JokeDao getJokeDao() {
        if (jokeDao == null) {
            init();
        }
        return jokeDao;
    }

    public ThunderAVSourceDao getThunderAVSourceDao() {
        if (thunderAVSourceDao == null) {
            init();
        }
        return thunderAVSourceDao;
    }

    public NovelDao getNovelDao() {
        if (novelDao == null) {
            init();
        }
        return novelDao;
    }

    public ImageDao getImageDao() {
        return imageDao;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        db.close();
        daoSession.clear();
    }
}
