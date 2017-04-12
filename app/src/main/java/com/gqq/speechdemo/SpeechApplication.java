package com.gqq.speechdemo;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by gqq on 2017/4/12.
 */

public class SpeechApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=58edd27e");
    }
}
