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
package me.jessyan.mvparms.demo.mvp.contract

import android.app.Activity

import com.jess.arms.mvp.IModel
import com.jess.arms.mvp.IView
import com.tbruyelle.rxpermissions2.RxPermissions

import io.reactivex.Observable
import me.jessyan.mvparms.demo.mvp.model.entity.User

/**
 * ================================================
 * 展示 Contract 的用法
 *
 * @see [Contract wiki 官方文档](https://github.com/JessYanCoding/MVPArms/wiki.2.4.1)
 * Created by JessYan on 09/04/2016 10:47
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
interface UserContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        val activity: Activity
        //申请权限
        val rxPermissions: RxPermissions

        fun startLoadMore()
        fun endLoadMore()
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,如是否使用缓存
    interface Model : IModel {
        fun getUsers(lastIdQueried: Int, update: Boolean): Observable<List<User>>
    }
}
