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
package me.jessyan.mvparms.demo.app.utils

import com.jess.arms.mvp.IView
import com.jess.arms.utils.RxLifecycleUtils
import com.trello.rxlifecycle2.LifecycleTransformer
import com.trello.rxlifecycle2.RxLifecycle

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

/**
 * ================================================
 * 放置便于使用 RxJava 的一些工具方法
 *
 *
 * Created by JessYan on 11/10/2016 16:39
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
object RxUtils {

    fun <T> applySchedulers(view: IView): ObservableTransformer<T, T> {
        return ObservableTransformer { observable ->
            observable.subscribeOn(Schedulers.io())
                    .doOnSubscribe {
                        view.showLoading()//显示进度条
                    }
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally {
                        view.hideLoading()//隐藏进度条
                    }.compose(RxLifecycleUtils.bindToLifecycle(view))
        }
    }

    /**
     * 此方法已废弃
     *
     * @param view
     * @param <T>
     * @return
     * @see RxLifecycleUtils 此类可以实现 {@link RxLifecycle} 的所有功能, 使用方法和之前一致
     *
    </T> */
    @Deprecated("Use {@link RxLifecycleUtils#bindToLifecycle(IView)} instead")
    fun <T> bindToLifecycle(view: IView): LifecycleTransformer<T> {
        return RxLifecycleUtils.bindToLifecycle(view)
    }

}
