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
import androidx.fragment.app.Fragment

import com.jess.arms.utils.RxLifecycleUtils
import com.trello.rxlifecycle2.RxLifecycle

import io.reactivex.subjects.Subject

/**
 * ================================================
 * 让 [Activity]/[Fragment] 实现此接口,即可正常使用 [RxLifecycle]
 * 无需再继承 [RxLifecycle] 提供的 Activity/Fragment ,扩展性极强
 *
 * @see RxLifecycleUtils 详细用法请查看此类
 * Created by JessYan on 25/08/2017 18:39
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
interface Lifecycleable<E> {
    fun provideLifecycleSubject(): Subject<E>
}
