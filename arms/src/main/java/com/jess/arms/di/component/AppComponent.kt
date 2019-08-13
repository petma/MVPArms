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
package com.jess.arms.di.component

import android.app.Activity
import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.jess.arms.base.delegate.AppDelegate
import com.jess.arms.di.module.AppModule
import com.jess.arms.di.module.ClientModule
import com.jess.arms.di.module.GlobalConfigModule
import com.jess.arms.http.imageloader.BaseImageLoaderStrategy
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.integration.AppManager
import com.jess.arms.integration.ConfigModule
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.integration.cache.Cache
import com.jess.arms.utils.ArmsUtils
import dagger.BindsInstance
import dagger.Component
import java.io.File
import java.util.concurrent.ExecutorService
import javax.inject.Singleton
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import okhttp3.OkHttpClient

/**
 * ================================================
 * 可通过 [ArmsUtils.obtainAppComponentFromContext] 拿到此接口的实现类
 * 拥有此接口的实现类即可调用对应的方法拿到 Dagger 提供的对应实例
 *
 * @see [AppComponent wiki 官方文档](https://github.com/JessYanCoding/MVPArms/wiki.2.2)
 * Created by JessYan on 8/4/2016
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
@Singleton
@Component(modules = [AppModule::class, ClientModule::class, GlobalConfigModule::class])
interface AppComponent {
    fun application(): Application

    /**
     * 用于管理所有 [Activity]
     * 之前 [AppManager] 使用 Dagger 保证单例, 只能使用 [AppComponent.appManager] 访问
     * 现在直接将 AppManager 独立为单例类, 可以直接通过静态方法 [AppManager.getAppManager] 访问, 更加方便
     * 但为了不影响之前使用 [AppComponent.appManager] 获取 [AppManager] 的项目, 所以暂时保留这种访问方式
     *
     * @return [AppManager]
     */
    @Deprecated("Use {@link AppManager#getAppManager()} instead")
    fun appManager(): AppManager

    /**
     * 用于管理网络请求层, 以及数据缓存层
     *
     * @return [IRepositoryManager]
     */
    fun repositoryManager(): IRepositoryManager

    /**
     * RxJava 错误处理管理类
     *
     * @return [RxErrorHandler]
     */
    fun rxErrorHandler(): RxErrorHandler

    /**
     * 图片加载管理器, 用于加载图片的管理类, 使用策略者模式, 可在运行时动态替换任何图片加载框架
     * arms-imageloader-glide 提供 Glide 的策略实现类, 也可以自行实现
     * 需要在 [ConfigModule.applyOptions] 中
     * 手动注册 [BaseImageLoaderStrategy], [ImageLoader] 才能正常使用
     *
     * @return
     */
    fun imageLoader(): ImageLoader

    /**
     * 网络请求框架
     *
     * @return [OkHttpClient]
     */
    fun okHttpClient(): OkHttpClient

    /**
     * Json 序列化库
     *
     * @return [Gson]
     */
    fun gson(): Gson

    /**
     * 缓存文件根目录 (RxCache 和 Glide 的缓存都已经作为子文件夹放在这个根目录下), 应该将所有缓存都统一放到这个根目录下
     * 便于管理和清理, 可在 [ConfigModule.applyOptions] 种配置
     *
     * @return [File]
     */
    fun cacheFile(): File

    /**
     * 用来存取一些整个 App 公用的数据, 切勿大量存放大容量数据, 这里的存放的数据和 [Application] 的生命周期一致
     *
     * @return [Cache]
     */
    fun extras(): Cache<String, Any>

    /**
     * 用于创建框架所需缓存对象的工厂
     *
     * @return [Cache.Factory]
     */
    fun cacheFactory(): Cache.Factory

    /**
     * 返回一个全局公用的线程池,适用于大多数异步需求。
     * 避免多个线程池创建带来的资源消耗。
     *
     * @return [ExecutorService]
     */
    fun executorService(): ExecutorService

    fun inject(delegate: AppDelegate)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun globalConfigModule(globalConfigModule: GlobalConfigModule): Builder

        fun build(): AppComponent
    }
}
