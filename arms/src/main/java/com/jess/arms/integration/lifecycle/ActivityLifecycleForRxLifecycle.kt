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
package com.jess.arms.integration.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

import com.trello.rxlifecycle2.RxLifecycle
import com.trello.rxlifecycle2.android.ActivityEvent

import javax.inject.Inject
import javax.inject.Singleton

import dagger.Lazy
import io.reactivex.subjects.Subject

/**
 * ================================================
 * 配合 [ActivityLifecycleable] 使用,使 [Activity] 具有 [RxLifecycle] 的特性
 *
 *
 * Created by JessYan on 25/08/2017 18:56
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
@Singleton
class ActivityLifecycleForRxLifecycle @Inject
constructor() : Application.ActivityLifecycleCallbacks {
    @Inject
    internal var mFragmentLifecycle: Lazy<FragmentLifecycleForRxLifecycle>? = null

    /**
     * 通过桥梁对象 `BehaviorSubject<ActivityEvent> mLifecycleSubject`
     * 在每个 Activity 的生命周期中发出对应的生命周期事件
     */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle) {
        if (activity is ActivityLifecycleable) {
            obtainSubject(activity).onNext(ActivityEvent.CREATE)
            if (activity is FragmentActivity) {
                (activity as FragmentActivity).supportFragmentManager.registerFragmentLifecycleCallbacks(mFragmentLifecycle!!.get(), true)
            }
        }
    }

    override fun onActivityStarted(activity: Activity) {
        if (activity is ActivityLifecycleable) {
            obtainSubject(activity).onNext(ActivityEvent.START)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        if (activity is ActivityLifecycleable) {
            obtainSubject(activity).onNext(ActivityEvent.RESUME)
        }
    }

    override fun onActivityPaused(activity: Activity) {
        if (activity is ActivityLifecycleable) {
            obtainSubject(activity).onNext(ActivityEvent.PAUSE)
        }
    }

    override fun onActivityStopped(activity: Activity) {
        if (activity is ActivityLifecycleable) {
            obtainSubject(activity).onNext(ActivityEvent.STOP)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        if (activity is ActivityLifecycleable) {
            obtainSubject(activity).onNext(ActivityEvent.DESTROY)
        }
    }

    /**
     * 从 [com.jess.arms.base.BaseActivity] 中获得桥梁对象 `BehaviorSubject<ActivityEvent> mLifecycleSubject`
     *
     * @see [BehaviorSubject 官方中文文档](https://mcxiaoke.gitbooks.io/rxdocs/content/Subject.html)
     */
    private fun obtainSubject(activity: Activity): Subject<ActivityEvent> {
        return (activity as ActivityLifecycleable).provideLifecycleSubject()
    }
}
