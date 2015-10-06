package com.q.s.quicksearch.models;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.q.s.quicksearch.models.ThunderAVSource;
import com.q.s.quicksearch.models.Novel;
import com.q.s.quicksearch.models.Joke;
import com.q.s.quicksearch.models.Image;

import com.q.s.quicksearch.models.ThunderAVSourceDao;
import com.q.s.quicksearch.models.NovelDao;
import com.q.s.quicksearch.models.JokeDao;
import com.q.s.quicksearch.models.ImageDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig thunderAVSourceDaoConfig;
    private final DaoConfig novelDaoConfig;
    private final DaoConfig jokeDaoConfig;
    private final DaoConfig imageDaoConfig;

    private final ThunderAVSourceDao thunderAVSourceDao;
    private final NovelDao novelDao;
    private final JokeDao jokeDao;
    private final ImageDao imageDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        thunderAVSourceDaoConfig = daoConfigMap.get(ThunderAVSourceDao.class).clone();
        thunderAVSourceDaoConfig.initIdentityScope(type);

        novelDaoConfig = daoConfigMap.get(NovelDao.class).clone();
        novelDaoConfig.initIdentityScope(type);

        jokeDaoConfig = daoConfigMap.get(JokeDao.class).clone();
        jokeDaoConfig.initIdentityScope(type);

        imageDaoConfig = daoConfigMap.get(ImageDao.class).clone();
        imageDaoConfig.initIdentityScope(type);

        thunderAVSourceDao = new ThunderAVSourceDao(thunderAVSourceDaoConfig, this);
        novelDao = new NovelDao(novelDaoConfig, this);
        jokeDao = new JokeDao(jokeDaoConfig, this);
        imageDao = new ImageDao(imageDaoConfig, this);

        registerDao(ThunderAVSource.class, thunderAVSourceDao);
        registerDao(Novel.class, novelDao);
        registerDao(Joke.class, jokeDao);
        registerDao(Image.class, imageDao);
    }
    
    public void clear() {
        thunderAVSourceDaoConfig.getIdentityScope().clear();
        novelDaoConfig.getIdentityScope().clear();
        jokeDaoConfig.getIdentityScope().clear();
        imageDaoConfig.getIdentityScope().clear();
    }

    public ThunderAVSourceDao getThunderAVSourceDao() {
        return thunderAVSourceDao;
    }

    public NovelDao getNovelDao() {
        return novelDao;
    }

    public JokeDao getJokeDao() {
        return jokeDao;
    }

    public ImageDao getImageDao() {
        return imageDao;
    }

}