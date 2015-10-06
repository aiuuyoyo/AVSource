package com.q.s.quicksearch.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Environment;

public class DatabaseContext extends ContextWrapper {

    private File mDBDir;
    private File mDBFile;

    public DatabaseContext(Context base) {
        super(base);
    }

    @Override
    public File getDatabasePath(String name) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (mDBFile != null) {
                return mDBFile.getAbsoluteFile();
            }
        }
        return super.getDatabasePath(name);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return openOrCreateDatabase(name, mode, factory);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory) {

        String state = Environment.getExternalStorageState();
        /* 初次运行、初始化文件夹 */
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            if (mDBDir == null) {
                /* 数据句酷所在的文件夹对象 */
                mDBDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                        + "AVSource" + File.separator + getPackageName());
				/* 如果数据库文件夹不存在，则新建并创建ReadMe.txt文件 */
                if (!mDBDir.exists()) {
                    mDBDir.mkdirs();

                    File readMeFile = new File(mDBDir, "ReadMe.txt");
                    try {
                        OutputStream stream = new FileOutputStream(readMeFile);
                        OutputStreamWriter writer = new OutputStreamWriter(stream);
                        writer.write("Please do never delete any files in this directory.Important data will fade, otherwise!");
                        writer.flush();
                        writer.close();
                        stream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

			/* 数据库文件对象 */
            if (mDBFile == null) {
                mDBFile = new File(mDBDir, "avsource-db");
                return SQLiteDatabase.openOrCreateDatabase(mDBFile, null);
            } else {
                return SQLiteDatabase.openOrCreateDatabase(mDBFile, null);
            }
        }
        return super.openOrCreateDatabase(name, mode, factory);
    }

}
