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
package com.jess.arms.di.module

import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentManager

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jess.arms.di.component.AppComponent
import com.jess.arms.integration.ActivityLifecycle
import com.jess.arms.integration.AppManager
import com.jess.arms.integration.FragmentLifecycle
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.integration.RepositoryManager
import com.jess.arms.integration.cache.Cache
import com.jess.arms.integration.cache.CacheType
import com.jess.arms.integration.lifecycle.ActivityLifecycleForRxLifecycle

import java.util.ArrayList

import javax.inject.Named
import javax.inject.Singleton

import dagger.Binds
import dagger.Module
import dagger.Provides

/**
 * ================================================
 * 提供一些框架必须的实例的 [Module]
 *
 *
 * Created by JessYan on 8/4/2016.
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
@Module
abstract class AppModule {

    @Binds
    internal abstract fun bindRepositoryManager(repositoryManager: RepositoryManager): IRepositoryManager

    @Binds
    @Named("ActivityLifecycle")
    internal abstract fun bindActivityLifecycle(activityLifecycle: ActivityLifecycle): Application.ActivityLifecycleCallbacks

    @Binds
    @Named("ActivityLifecycleForRxLifecycle")
    internal abstract fun bindActivityLifecycleForRxLifecycle(activityLifecycleForRxLifecycle: ActivityLifecycleForRxLifecycle): Application.ActivityLifecycleCallbacks

    @Binds
    internal abstract fun bindFragmentLifecycle(fragmentLifecycle: FragmentLifecycle): FragmentManager.FragmentLifecycleCallbacks

    interface GsonConfiguration {
        fun configGson(context: Context, builder: GsonBuilder)
    }

    companion object {

        @Singleton
        @Provides
        internal fun provideGson(application: Application, configuration: GsonConfiguration?): Gson {
            val builder = GsonBuilder()
            configuration?.configGson(application, builder)
            return builder.create()
        }

        /**
         * 之前 [AppManager] 使用 Dagger 保证单例, 只能使用 [AppComponent.appManager] 访问
         * 现在直接将 AppManager 独立为单例类, 可以直接通过静态方法 [AppManager.getAppManager] 访问, 更加方便
         * 但为了不影响之前使用 [AppComponent.appManager] 获取 [AppManager] 的项目, 所以暂时保留这种访问方式
         *
         * @param application
         * @return
         */
        @Singleton
        @Provides
        internal fun provideAppManager(application: Application): AppManager? {
            return AppManager.appManager!!.init(application)
        }

        @Singleton
        @Provides
        internal fun provideExtras(cacheFactory: Cache.Factory): Cache<String, Any> {
            return cacheFactory.build(CacheType.EXTRAS)
        }

        @Singleton
        @Provides
        internal fun provideFragmentLifecycles(): List<FragmentManager.FragmentLifecycleCallbacks> {
            return ArrayList()
        }
    }
}
