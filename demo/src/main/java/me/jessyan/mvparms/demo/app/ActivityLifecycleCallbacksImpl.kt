/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.jessyan.mvparms.demo.app

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.widget.TextView

import me.jessyan.mvparms.demo.R
import timber.log.Timber

/**
 * ================================================
 * 展示 [Application.ActivityLifecycleCallbacks] 的用法
 *
 *
 * Created by JessYan on 04/09/2017 17:14
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
class ActivityLifecycleCallbacksImpl : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle) {
        Timber.i("${activity} - onActivityCreated")
    }

    override fun onActivityStarted(activity: Activity) {
        Timber.i("${activity} - onActivityStarted")
        if (!activity.intent.getBooleanExtra("isInitToolbar", false)) {
            //由于加强框架的兼容性,故将 setContentView 放到 onActivityCreated 之后,onActivityStarted 之前执行
            //而 findViewById 必须在 Activity setContentView() 后才有效,所以将以下代码从之前的 onActivityCreated 中移动到 onActivityStarted 中执行
            activity.intent.putExtra("isInitToolbar", true)
            //这里全局给Activity设置toolbar和title,你想象力有多丰富,这里就有多强大,以前放到BaseActivity的操作都可以放到这里
            if (activity.findViewById<View>(R.id.toolbar) != null) {
                if (activity is AppCompatActivity) {
                    activity.setSupportActionBar(activity.findViewById<View>(R.id.toolbar) as Toolbar)
                    activity.supportActionBar!!.setDisplayShowTitleEnabled(false)
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        activity.setActionBar(activity.findViewById<View>(R.id.toolbar) as android.widget.Toolbar)
                        activity.actionBar!!.setDisplayShowTitleEnabled(false)
                    }
                }
            }
            if (activity.findViewById<View>(R.id.toolbar_title) != null) {
                (activity.findViewById<View>(R.id.toolbar_title) as TextView).text = activity.title
            }
            if (activity.findViewById<View>(R.id.toolbar_back) != null) {
                activity.findViewById<View>(R.id.toolbar_back).setOnClickListener({ v -> activity.onBackPressed() })
            }
        }
    }

    override fun onActivityResumed(activity: Activity) {
        Timber.i("${activity} - onActivityResumed")
    }

    override fun onActivityPaused(activity: Activity) {
        Timber.i("${activity} - onActivityPaused")
    }

    override fun onActivityStopped(activity: Activity) {
        Timber.i("${activity} - onActivityStopped")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Timber.i("${activity} - onActivitySaveInstanceState")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Timber.i("${activity} - onActivityDestroyed")
        //横竖屏切换或配置改变时, Activity 会被重新创建实例, 但 Bundle 中的基础数据会被保存下来,移除该数据是为了保证重新创建的实例可以正常工作
        activity.intent.removeExtra("isInitToolbar")
    }
}
