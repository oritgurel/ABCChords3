package com.oritmalki.mymusicapp2;

import android.app.Application;

import com.oritmalki.mymusicapp2.database.AppDataBase;
import com.oritmalki.mymusicapp2.database.MeasureRepository;

/**
 * Created by Orit on 27.1.2018.
 */

public class BasicApp extends Application {

    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mAppExecutors = new AppExecutors();
    }
    public AppDataBase getDatabase() {
        return AppDataBase.getInstance(this, mAppExecutors);
    }

    public MeasureRepository getRepository() {
        return MeasureRepository.getInstance(getDatabase());
    }
}
