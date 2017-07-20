package com.lwz.oncrash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import cat.ereza.customactivityoncrash.config.CaocConfig;

/**
 * 程序奔溃后重启的框架测试
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 默认的崩溃页面
     */
    public void defaultErrActivity(View view) {
        startActivity(new Intent(this, MakeErrActivity.class));
    }

    /**
     * 自定义的崩溃页面
     */
    public void customErrActivity(View view) {
        //设置一下属性就可以了
        //整个配置属性，可以设置一个或多个，也可以一个都不设置
        CaocConfig.Builder.create()
                //程序在后台时，发生崩溃的三种处理方式
                //BackgroundMode.BACKGROUND_MODE_SHOW_CUSTOM: //当应用程序处于后台时崩溃，也会启动错误页面，
                //BackgroundMode.BACKGROUND_MODE_CRASH:      //当应用程序处于后台崩溃时显示默认系统错误（一个系统提示的错误对话框），
                //BackgroundMode.BACKGROUND_MODE_SILENT:     //当应用程序处于后台时崩溃，默默地关闭程序！这种模式我感觉最好
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)
                .errorActivity(CustomErrorActivity.class) //这种程序崩溃后显示的页面
                .apply();
        startActivity(new Intent(this, MakeErrActivity.class));
    }


}