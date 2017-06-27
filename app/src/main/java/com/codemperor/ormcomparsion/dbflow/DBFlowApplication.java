package com.codemperor.ormcomparsion.dbflow;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by feng on 2017/6/21.
 */

public class DBFlowApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    public static String getUserId (){
        return System.currentTimeMillis() + "";
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
