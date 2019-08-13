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
package com.jess.arms.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.appcompat.app.AppCompatActivity
import android.util.AttributeSet
import android.view.InflateException
import android.view.View

import com.jess.arms.base.delegate.IActivity
import com.jess.arms.integration.cache.Cache
import com.jess.arms.integration.cache.CacheType
import com.jess.arms.integration.lifecycle.ActivityLifecycleable
import com.jess.arms.mvp.IPresenter
import com.jess.arms.utils.ArmsUtils
import com.trello.rxlifecycle2.android.ActivityEvent

import javax.inject.Inject

import butterknife.ButterKnife
import butterknife.Unbinder
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject


/**
 * ================================================
 * 因为 Java 只能单继承, 所以如果要用到需要继承特定 [Activity] 的三方库, 那你就需要自己自定义 [Activity]
 * 继承于这个特定的 [Activity], 然后再按照 [BaseActivity] 的格式, 将代码复制过去, 记住一定要实现[IActivity]
 *
 * @see [请配合官方 Wiki 文档学习本框架](https://github.com/JessYanCoding/MVPArms/wiki)
 *
 * @see [更新日志, 升级必看!](https://github.com/JessYanCoding/MVPArms/wiki/UpdateLog)
 *
 * @see [常见 Issues, 踩坑必看!](https://github.com/JessYanCoding/MVPArms/wiki/Issues)
 *
 * @see [MVPArms 官方组件化方案 ArmsComponent, 进阶指南!](https://github.com/JessYanCoding/ArmsComponent/wiki)
 * Created by JessYan on 22/03/2016
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
abstract class BaseActivity<P : IPresenter> : AppCompatActivity(), IActivity, ActivityLifecycleable {
    protected val TAG = this.javaClass.simpleName
    private val mLifecycleSubject = BehaviorSubject.create<ActivityEvent>()
    private var mCache: Cache<String, Any>? = null
    private var mUnbinder: Unbinder? = null
    @Inject
    var mPresenter: P? = null//如果当前页面逻辑简单, Presenter 可以为 null

    @Synchronized
    override fun provideCache(): Cache<String, Any> {
        if (mCache == null) {
            mCache = ArmsUtils.obtainAppComponentFromContext(this).cacheFactory().build(CacheType.ACTIVITY_CACHE)
        }
        return mCache
    }

    override fun provideLifecycleSubject(): Subject<ActivityEvent> {
        return mLifecycleSubject
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            val layoutResID = initView(savedInstanceState)
            //如果initView返回0,框架则不会调用setContentView(),当然也不会 Bind ButterKnife
            if (layoutResID != 0) {
                setContentView(layoutResID)
                //绑定到butterknife
                mUnbinder = ButterKnife.bind(this)
            }
        } catch (e: Exception) {
            if (e is InflateException) throw e
            e.printStackTrace()
        }

        initData(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mUnbinder != null && mUnbinder !== Unbinder.EMPTY)
            mUnbinder!!.unbind()
        this.mUnbinder = null
        if (mPresenter != null)
            mPresenter!!.onDestroy()//释放资源
        this.mPresenter = null
    }

    /**
     * 是否使用 EventBus
     * Arms 核心库现在并不会依赖某个 EventBus, 要想使用 EventBus, 还请在项目中自行依赖对应的 EventBus
     * 现在支持两种 EventBus, greenrobot 的 EventBus 和畅销书 《Android源码设计模式解析与实战》的作者 何红辉 所作的 AndroidEventBus
     * 确保依赖后, 将此方法返回 true, Arms 会自动检测您依赖的 EventBus, 并自动注册
     * 这种做法可以让使用者有自行选择三方库的权利, 并且还可以减轻 Arms 的体积
     *
     * @return 返回 `true` (默认为 `true`), Arms 会自动注册 EventBus
     */
    override fun useEventBus(): Boolean {
        return true
    }

    /**
     * 这个 [Activity] 是否会使用 [Fragment], 框架会根据这个属性判断是否注册 [FragmentManager.FragmentLifecycleCallbacks]
     * 如果返回 `false`, 那意味着这个 [Activity] 不需要绑定 [Fragment], 那你再在这个 [Activity] 中绑定继承于 [BaseFragment] 的 [Fragment] 将不起任何作用
     *
     * @return 返回 `true` (默认为 `true`), 则需要使用 [Fragment]
     */
    override fun useFragment(): Boolean {
        return true
    }
}
