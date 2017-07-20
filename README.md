#Android检测程序崩溃框架CustomActivityOnCrash
在Android程序中，程序有时会遇到各种之前没有遇到的问题，这时如果能够对程序进行额外判断就好的，CustomActivityOnCrash框架就实现的这个功能。如果你的程序出现崩溃，它会检测到（各种崩溃，比如空指针），会弹出一个页面，提示你程序崩溃，你是否要关闭程序，还是重新启动程序！
效果：
![1](http://i.imgur.com/XKh1Dan.gif)
错误报告，可以直接显示在页面上或吐司出来！
崩溃后显示的页面既可以是框架默认的，也可以是自己定义的。

原文章：https://github.com/Ereza/CustomActivityOnCrash
文章全是英文，而且是在Studio中运行，我的运行环境是IDEA！不能依赖，只能把文件复制进来，其实文件不多，就几个java文件！还有些资源文件！和一个jar包：support-annotations-26.0.0-alpha1.jar，框架中有一个注解类是V4包下没有，只能用上面那个包！

使用方法，我的和原作者的是一样的，但是我的讲解更详细，而且是中文！

##步骤一
导入框架类和jar包（IDEA不能依赖，使用可以直接把那些文件加入到你的src文件加下就可以）
如图所示：
![2](http://i.imgur.com/FG3zz32.png)
可以看到这个框架总共就四个java文件！
这个上面导入的jar包，可能会跟V4的冲突！在Studio中是完全没有问题的！
框架内也是有一个DefaultErrorActivity，要在AndroidManifest中注册！
##步骤二
在继承Application的类中进行基本设置
```
package com.lwz.oncrash;

import android.app.Application;
import android.util.Log;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.activity.DefaultErrorActivity;
import cat.ereza.customactivityoncrash.config.CaocConfig;

/**
 * Application 
 * 程序崩溃的基本设置，请看注解！
 */

public class App extends Application {
    private static final String TAG = "TAG";

    @Override
    public void onCreate() {
        super.onCreate();

        //整个配置属性，可以设置一个或多个，也可以一个都不设置
        CaocConfig.Builder.create()
                //程序在后台时，发生崩溃的三种处理方式
                //BackgroundMode.BACKGROUND_MODE_SHOW_CUSTOM: //当应用程序处于后台时崩溃，也会启动错误页面，
                //BackgroundMode.BACKGROUND_MODE_CRASH:      //当应用程序处于后台崩溃时显示默认系统错误（一个系统提示的错误对话框），
                //BackgroundMode.BACKGROUND_MODE_SILENT:     //当应用程序处于后台时崩溃，默默地关闭程序！这种模式我感觉最好
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)
                .enabled(true)     //这阻止了对崩溃的拦截,false表示阻止。用它来禁用customactivityoncrash框架
                .showErrorDetails(false) //这将隐藏错误活动中的“错误详细信息”按钮，从而隐藏堆栈跟踪。
                .showRestartButton(false)    //是否可以重启页面
                .trackActivities(true)     //错误页面中显示错误详细信息
             .minTimeBetweenCrashesMs(2000)      //定义应用程序崩溃之间的最短时间，以确定我们不在崩溃循环中。比如：在规定的时间内再次崩溃，框架将不处理，让系统处理！
                .errorDrawable(R.drawable.ic_launcher)     //崩溃页面显示的图标
                .restartActivity(MainActivity.class)      //重新启动后的页面
                .errorActivity(DefaultErrorActivity.class) //程序崩溃后显示的页面
                .eventListener(new CustomEventListener())//设置监听
                .apply();
        //如果没有任何配置，程序崩溃显示的是默认的设置
        CustomActivityOnCrash.install(this);

    }

    /**
     * 监听程序崩溃/重启
     */
    private static class CustomEventListener implements CustomActivityOnCrash.EventListener {
        //程序崩溃回调
        @Override
        public void onLaunchErrorActivity() {
            Log.e(TAG, "onLaunchErrorActivity()");
        }

        //重启程序时回调
        @Override
        public void onRestartAppFromErrorActivity() {
            Log.e(TAG, "onRestartAppFromErrorActivity()");
        }

        //在崩溃提示页面关闭程序时回调
        @Override
        public void onCloseAppFromErrorActivity() {
            Log.e(TAG, "onCloseAppFromErrorActivity()");
        }

    }
}

```
上面很多语句都是可以不设置的，但是你至少要在onCreate中设置一句话：
```
 //如果没有任何配置，程序崩溃显示的是默认的设置
 CustomActivityOnCrash.install(this);

```
最后记得在AndroidManifest中注册下App！

做完前面两个，其实是已经可以实现程序崩溃后的简单处理。
但是框架崩溃页面只能选择关闭页面或重启程序的其中一种，并且要在config对象里面设置好。

```
 .showRestartButton(false)    //默认false，是否可以重启页面，
```

如果设置true，默认的程序崩溃页面只能选择重启程序，如果设置false，默认的程序崩溃页面只能选择关闭程序！

##步骤三
如果你想自己设置，程序崩溃后显示的页面，也是可以的，并且可以同时显示关闭程序和重新启动程序
下面就是自定义的程序崩溃后的Activity
```

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


```
上面可以看到无论是程序关闭还是重启都是使用了CustomActivityOnCrash的静态方法，但是重启要判断是否设置了重新启动后显示的页面！
```
  .restartActivity(MainActivity.class)      //设置重新启动后的页面
  .errorActivity(DefaultErrorActivity.class) //设置程序崩溃后显示的页面


```
这里重启不一定要显示到程序的主界面，可以是程序的任意Activity！只是看你在config中是怎么设置的，
但是要注意，如果那个非主界面页面是从其他页面跳转过去的，并且还携带了不少数据，重启后页面是无法获取到数据的。

##设置程序在后台崩溃后的处理情况
在上面演示动画中可以看到有一个5秒后程序崩溃的情况，就是为了测试，后台崩溃的处理情况

```
  				 //程序在后台时，发生崩溃的三种处理方式
                //BackgroundMode.BACKGROUND_MODE_SHOW_CUSTOM: //当应用程序处于后台时崩溃，也会启动错误页面，
                //BackgroundMode.BACKGROUND_MODE_CRASH:      //当应用程序处于后台崩溃时显示默认系统错误（一个系统提示的错误对话框），
                //BackgroundMode.BACKGROUND_MODE_SILENT:     //当应用程序处于后台时崩溃，默默地关闭程序！这种模式我感觉最好
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)

```
看上面解释应该是很清楚了吧！
###1.依然显示崩溃页面： 
```

.backgroundMode(CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM)//即使退到后台，崩溃页面依然显示

```
效果：
![4](http://i.imgur.com/BEbw0Sz.gif)

###2.显示的是系统的崩溃页面： 
```

.backgroundMode(CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM)//即使退到后台，崩溃页面依然显示

```
效果：
![5](http://i.imgur.com/U8cEO13.gif)

###3.不显示崩溃页面： 
```

.backgroundMode(CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM)//即使退到后台，崩溃页面依然显示

```
效果：
![6](http://i.imgur.com/SUsggoD.gif)