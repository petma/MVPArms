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
package me.jessyan.mvparms.demo.di.module

import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.http.imageloader.ImageLoader
import com.tbruyelle.rxpermissions2.RxPermissions

import java.util.ArrayList

import dagger.Binds
import dagger.Module
import dagger.Provides
import me.jessyan.mvparms.demo.mvp.contract.UserContract
import me.jessyan.mvparms.demo.mvp.model.UserModel
import me.jessyan.mvparms.demo.mvp.model.entity.User
import me.jessyan.mvparms.demo.mvp.ui.adapter.UserAdapter

/**
 * ================================================
 * 展示 Module 的用法
 *
 * @see [Module wiki 官方文档](https://github.com/JessYanCoding/MVPArms/wiki.2.4.5)
 * Created by JessYan on 09/04/2016 11:10
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
@Module
abstract class UserModule {

    @Binds
    internal abstract fun bindUserModel(model: UserModel): UserContract.Model

    companion object {

        @ActivityScope
        @Provides
        internal fun provideRxPermissions(view: UserContract.View): RxPermissions {
            return RxPermissions(view.activity as FragmentActivity)
        }

        @ActivityScope
        @Provides
        internal fun provideLayoutManager(view: UserContract.View): RecyclerView.LayoutManager {
            return GridLayoutManager(view.activity, 2)
        }

        @ActivityScope
        @Provides
        internal fun provideUserList(): List<User> {
            return ArrayList()
        }

        @ActivityScope
        @Provides
        internal fun provideUserAdapter(list: List<User>, mImageLoader: ImageLoader): RecyclerView.Adapter<*> {
            return UserAdapter(list, mImageLoader)
        }
    }
}
