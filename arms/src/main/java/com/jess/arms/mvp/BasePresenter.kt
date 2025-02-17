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
package com.jess.arms.mvp

import android.app.Activity
import android.app.Service
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.fragment.app.Fragment
import androidx.core.app.ComponentActivity
import android.view.View

import com.jess.arms.integration.EventBusManager
import com.jess.arms.utils.Preconditions
import com.trello.rxlifecycle2.RxLifecycle

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action

/**
 * ================================================
 * 基类 Presenter
 *
 * @see [Presenter wiki 官方文档](https://github.com/JessYanCoding/MVPArms/wiki.2.4.4)
 * Created by JessYan on 4/28/2016
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
open class BasePresenter<M : IModel, V : IView> : IPresenter, LifecycleObserver {
    protected val TAG = this.javaClass.simpleName
    protected var mCompositeDisposable: CompositeDisposable? = null
    protected var mModel: M? = null
    protected var mRootView: V? = null

    /**
     * 如果当前页面同时需要 Model 层和 View 层,则使用此构造函数(默认)
     *
     * @param model
     * @param rootView
     */
    constructor(model: M, rootView: V) {
        Preconditions.checkNotNull(model, "%s cannot be null", IModel::class.java.name)
        Preconditions.checkNotNull(rootView, "%s cannot be null", IView::class.java.name)
        this.mModel = model
        this.mRootView = rootView
        onStart()
    }

    /**
     * 如果当前页面不需要操作数据,只需要 View 层,则使用此构造函数
     *
     * @param rootView
     */
    constructor(rootView: V) {
        Preconditions.checkNotNull(rootView, "%s cannot be null", IView::class.java.name)
        this.mRootView = rootView
        onStart()
    }

    constructor() {
        onStart()
    }

    override fun onStart() {
        //将 LifecycleObserver 注册给 LifecycleOwner 后 @OnLifecycleEvent 才可以正常使用
        if (mRootView != null && mRootView is LifecycleOwner) {
            (mRootView as LifecycleOwner).lifecycle.addObserver(this)
            if (mModel != null && mModel is LifecycleObserver) {
                (mRootView as LifecycleOwner).lifecycle.addObserver((mModel as LifecycleObserver?)!!)
            }
        }
        if (useEventBus())
        //如果要使用 EventBus 请将此方法返回 true
            EventBusManager.instance!!.register(this)//注册 EventBus
    }

    /**
     * 在框架中 [Activity.onDestroy] 时会默认调用 [IPresenter.onDestroy]
     */
    override fun onDestroy() {
        if (useEventBus())
        //如果要使用 EventBus 请将此方法返回 true
            EventBusManager.instance!!.unregister(this)//注销 EventBus
        unDispose()//解除订阅
        if (mModel != null)
            mModel!!.onDestroy()
        this.mModel = null
        this.mRootView = null
        this.mCompositeDisposable = null
    }

    /**
     * 只有当 `mRootView` 不为 null, 并且 `mRootView` 实现了 [LifecycleOwner] 时, 此方法才会被调用
     * 所以当您想在 [Service] 以及一些自定义 [View] 或自定义类中使用 `Presenter` 时
     * 您也将不能继续使用 [OnLifecycleEvent] 绑定生命周期
     *
     * @param owner link [ComponentActivity] and [Fragment]
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    internal fun onDestroy(owner: LifecycleOwner) {
        /**
         * 注意, 如果在这里调用了 [.onDestroy] 方法, 会出现某些地方引用 `mModel` 或 `mRootView` 为 null 的情况
         * 比如在 [RxLifecycle] 终止 [Observable] 时, 在 [io.reactivex.Observable.doFinally] 中却引用了 `mRootView` 做一些释放资源的操作, 此时会空指针
         * 或者如果你声明了多个 @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY) 时在其他 @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
         * 中引用了 `mModel` 或 `mRootView` 也可能会出现此情况
         */
        owner.lifecycle.removeObserver(this)
    }

    /**
     * 是否使用 EventBus
     * Arms 核心库现在并不会依赖某个 EventBus, 要想使用 EventBus, 还请在项目中自行依赖对应的 EventBus
     * 现在支持两种 EventBus, greenrobot 的 EventBus 和畅销书 《Android源码设计模式解析与实战》的作者 何红辉 所作的 AndroidEventBus
     * 确保依赖后, 将此方法返回 true, Arms 会自动检测您依赖的 EventBus, 并自动注册
     * 这种做法可以让使用者有自行选择三方库的权利, 并且还可以减轻 Arms 的体积
     *
     * @return 返回 `true` (默认为使用 `true`), Arms 会自动注册 EventBus
     */
    fun useEventBus(): Boolean {
        return true
    }

    /**
     * 将 [Disposable] 添加到 [CompositeDisposable] 中统一管理
     * 可在 [Activity.onDestroy] 中使用 [.unDispose] 停止正在执行的 RxJava 任务,避免内存泄漏
     * 目前框架已使用 [RxLifecycle] 避免内存泄漏,此方法作为备用方案
     *
     * @param disposable
     */
    fun addDispose(disposable: Disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable!!.add(disposable)//将所有 Disposable 放入容器集中处理
    }

    /**
     * 停止集合中正在执行的 RxJava 任务
     */
    fun unDispose() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable!!.clear()//保证 Activity 结束时取消所有正在执行的订阅
        }
    }
}
