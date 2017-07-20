/*
 * Copyright 2014-2017 Eduard Ereza Martínez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lwz.oncrash;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;

/**
 * 程序崩溃后显示的崩溃页面
 * 自定义的错误显示页面
 */
public class CustomErrorActivity extends Activity implements View.OnClickListener {

    TextView errorDetailsText;//显示崩溃提示的文本
    Button btn_restart;//关闭页面的按钮
    Button btn_close;//关闭页面的按钮
    CaocConfig config;//配置对象

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_error);
        initView();
        initData();
        initEvent();

    }

    private void initView() {
        errorDetailsText = (TextView) findViewById(R.id.error_details);
        btn_restart = (Button) findViewById(R.id.btn_restart);
        btn_close = (Button) findViewById(R.id.btn_close);
    }

    private void initData() {
        //可以获取到的四个信息:
        String stackString = CustomActivityOnCrash.getStackTraceFromIntent(getIntent());//将堆栈跟踪作为字符串获取。
        String logString = CustomActivityOnCrash.getActivityLogFromIntent(getIntent()); //获取错误报告的Log信息
        String allString = CustomActivityOnCrash.getAllErrorDetailsFromIntent(this, getIntent());// 获取所有的信息
        config = CustomActivityOnCrash.getConfigFromIntent(getIntent());//获得配置信息,比如设置的程序崩溃显示的页面和重新启动显示的页面等等信息

        errorDetailsText.setText("程序崩溃了！");

        //吐司
        Toast.makeText(this, stackString, Toast.LENGTH_SHORT).show();
    }

    private void initEvent() {
        btn_restart.setOnClickListener(this);
        btn_close.setOnClickListener(this);

    }

    public void onClick(View v) {
        if (config != null && config.getRestartActivityClass() != null) {
            //重启程序
            if (v.getId() == R.id.btn_restart) {
                CustomActivityOnCrash.restartApplication(CustomErrorActivity.this, config);
            }

        }
        if (config != null) {
            //关闭页面
            if (v.getId() == R.id.btn_close) {
                CustomActivityOnCrash.closeApplication(CustomErrorActivity.this, config);
            }
        }
    }
}
