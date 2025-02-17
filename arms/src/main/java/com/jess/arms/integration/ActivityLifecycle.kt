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
package com.jess.arms.integration

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

import com.jess.arms.base.BaseFragment
import com.jess.arms.base.delegate.ActivityDelegate
import com.jess.arms.base.delegate.ActivityDelegateImpl
import com.jess.arms.base.delegate.FragmentDelegate
import com.jess.arms.base.delegate.IActivity
import com.jess.arms.integration.cache.Cache
import com.jess.arms.integration.cache.IntelligentCache
import com.jess.arms.utils.Preconditions

import javax.inject.Inject
import javax.inject.Singleton

import dagger.Lazy


/**
 * ================================================
 * [Application.ActivityLifecycleCallbacks] 默认实现类
 * 通过 [ActivityDelegate] 管理 [Activity]
 *
 * @see [ActivityLifecycleCallbacks 分析文章](http://www.jianshu.com/p/75a5c24174b2)
 * Created by JessYan on 21/02/2017 14:23
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
@Singleton
class ActivityLifecycle @Inject
constructor() : Application.ActivityLifecycleCallbacks {

    @Inject
    internal var mAppManager: AppManager? = null
    @Inject
    internal var mApplication: Application? = null
    @Inject
    internal var mExtras: Cache<String, Any>? = null
    @Inject
    internal var mFragmentLifecycle: Lazy<FragmentManager.FragmentLifecycleCallbacks>? = null
    @Inject
    internal var mFragmentLifecycles: Lazy<List<FragmentManager.FragmentLifecycleCallbacks>>? = null

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle) {
        //如果 intent 包含了此字段,并且为 true 说明不加入到 list 进行统一管理
        var isNotAdd = false
        if (activity.intent != null)
            isNotAdd = activity.intent.getBooleanExtra(AppManager.IS_NOT_ADD_ACTIVITY_LIST, false)

        if (!isNotAdd)
            mAppManager!!.addActivity(activity)

        //配置ActivityDelegate
        if (activity is IActivity) {
            var activityDelegate = fetchActivityDelegate(activity)
            if (activityDelegate == null) {
                val cache = getCacheFromActivity(activity as IActivity)
                activityDelegate = ActivityDelegateImpl(activity)
                //使用 IntelligentCache.KEY_KEEP 作为 key 的前缀, 可以使储存的数据永久存储在内存中
                //否则存储在 LRU 算法的存储空间中, 前提是 Activity 使用的是 IntelligentCache (框架默认使用)
                cache.put(IntelligentCache.getKeyOfKeep(ActivityDelegate.ACTIVITY_DELEGATE), activityDelegate)
            }
            activityDelegate.onCreate(savedInstanceState)
        }

        registerFragmentCallbacks(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        val activityDelegate = fetchActivityDelegate(activity)
        activityDelegate?.onStart()
    }

    override fun onActivityResumed(activity: Activity) {
        mAppManager!!.currentActivity = activity

        val activityDelegate = fetchActivityDelegate(activity)
        activityDelegate?.onResume()
    }

    override fun onActivityPaused(activity: Activity) {
        val activityDelegate = fetchActivityDelegate(activity)
        activityDelegate?.onPause()
    }

    override fun onActivityStopped(activity: Activity) {
        if (mAppManager!!.currentActivity === activity) {
            mAppManager!!.currentActivity = null
        }

        val activityDelegate = fetchActivityDelegate(activity)
        activityDelegate?.onStop()
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        val activityDelegate = fetchActivityDelegate(activity)
        activityDelegate?.onSaveInstanceState(outState)
    }

    override fun onActivityDestroyed(activity: Activity) {
        mAppManager!!.removeActivity(activity)

        val activityDelegate = fetchActivityDelegate(activity)
        if (activityDelegate != null) {
            activityDelegate.onDestroy()
            getCacheFromActivity(activity as IActivity).clear()
        }
    }

    /**
     * 给每个 Activity 的所有 Fragment 设置监听其生命周期, Activity 可以通过 [IActivity.useFragment]
     * 设置是否使用监听,如果这个 Activity 返回 false 的话,这个 Activity 下面的所有 Fragment 将不能使用 [FragmentDelegate]
     * 意味着 [BaseFragment] 也不能使用
     *
     * @param activity
     */
    private fun registerFragmentCallbacks(activity: Activity) {

        val useFragment = if (activity is IActivity) (activity as IActivity).useFragment() else true
        if (activity is FragmentActivity && useFragment) {

            //mFragmentLifecycle 为 Fragment 生命周期实现类, 用于框架内部对每个 Fragment 的必要操作, 如给每个 Fragment 配置 FragmentDelegate
            //注册框架内部已实现的 Fragment 生命周期逻辑
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(mFragmentLifecycle!!.get(), true)

            if (mExtras!!.containsKey(IntelligentCache.getKeyOfKeep(ConfigModule::class.java.name))) {
                val modules = mExtras!!.get(IntelligentCache.getKeyOfKeep(ConfigModule::class.java.name)) as List<ConfigModule>?
                for (module in modules!!) {
                    module.injectFragmentLifecycle(mApplication!!, mFragmentLifecycles!!.get())
                }
                mExtras!!.remove(IntelligentCache.getKeyOfKeep(ConfigModule::class.java.name))
            }

            //注册框架外部, 开发者扩展的 Fragment 生命周期逻辑
            for (fragmentLifecycle in mFragmentLifecycles!!.get()) {
                activity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycle, true)
            }
        }
    }

    private fun fetchActivityDelegate(activity: Activity): ActivityDelegate? {
        var activityDelegate: ActivityDelegate? = null
        if (activity is IActivity) {
            val cache = getCacheFromActivity(activity as IActivity)
            activityDelegate = cache.get(IntelligentCache.getKeyOfKeep(ActivityDelegate.ACTIVITY_DELEGATE)) as ActivityDelegate?
        }
        return activityDelegate
    }

    private fun getCacheFromActivity(activity: IActivity): Cache<String, Any> {
        val cache = activity.provideCache()
        Preconditions.checkNotNull(cache, "%s cannot be null on Activity", Cache<*, *>::class.java.name)
        return cache
    }
}
