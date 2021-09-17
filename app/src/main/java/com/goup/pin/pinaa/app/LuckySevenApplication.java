package com.goup.pin.pinaa.app;

import android.app.Application;
import android.util.Base64;

import com.appsflyer.AppsFlyerLib;
import com.onesignal.OneSignal;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

public class LuckySevenApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LuckySeven();
    }

    private void LuckySeven() {
        AppsFlyerLib.getInstance().init(
                new String(Base64.decode(getApplicationContext().getResources().getString(R.string.appflyer), Base64.DEFAULT)),
                null, getApplicationContext());
        AppsFlyerLib.getInstance().start(getApplicationContext());

        YandexMetricaConfig luckySevenConfig = YandexMetricaConfig.newConfigBuilder(new String
                (Base64.decode(getApplicationContext().getResources().getString(R.string.yandex), Base64.DEFAULT))).build();
        YandexMetrica.activate(getApplicationContext(), luckySevenConfig);
        YandexMetrica.enableActivityAutoTracking(this);

        OneSignal.setAppId(new String(Base64.decode(getApplicationContext().getResources().getString(R.string.onesignal), Base64.DEFAULT)));
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(getApplicationContext());
    }
}
