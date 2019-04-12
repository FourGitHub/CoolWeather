package com.fourweather.learn.View;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.fourweather.learn.greendao.db.DaoMaster;
import com.fourweather.learn.greendao.db.DaoSession;

import org.litepal.LitePal;

/**
 * Create on 2019/01/28
 *
 * @author Four
 * @description 每个项目只能有一个Application
 */
public class APP extends Application {
    private static Context mContext;
    private static DaoSession mDaoSession;
    private static final String GREEN_DAO_DB_NAME = "weaInfoJSON.db";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        // 为了练习，同一个项目中使用了两个开源的数据库框架
        initLitePal();
        initGreenDao();
    }

    private void initLitePal(){
        LitePal.initialize(mContext);
    }

    private void initGreenDao(){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, GREEN_DAO_DB_NAME);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
    }

    public static Context getContext(){
        return mContext;
    }

    public static DaoSession getDaoSession(){
        return mDaoSession;
    }
}
