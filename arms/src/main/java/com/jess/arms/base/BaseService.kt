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

import android.app.Service
import android.content.Intent
import android.os.IBinder

import com.jess.arms.integration.EventBusManager

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * ================================================
 * 基类 [Service]
 *
 *
 * Created by jess on 2016/5/6.
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
abstract class BaseService : Service() {
    protected val TAG = this.javaClass.simpleName
    protected var mCompositeDisposable: CompositeDisposable? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        if (useEventBus())
            EventBusManager.instance!!.register(this)
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (useEventBus())
            EventBusManager.instance!!.unregister(this)
        unDispose()//解除订阅
        this.mCompositeDisposable = null
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
    fun useEventBus(): Boolean {
        return true
    }

    protected fun addDispose(disposable: Disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable!!.add(disposable)//将所有 Disposable 放入容器集中处理
    }

    protected fun unDispose() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable!!.clear()//保证 Activity 结束时取消所有正在执行的订阅
        }
    }

    /**
     * 初始化
     */
    abstract fun init()
}
