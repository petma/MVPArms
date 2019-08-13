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
import android.text.TextUtils

import com.bumptech.glide.Glide
import com.jess.arms.http.BaseUrl
import com.jess.arms.http.GlobalHttpHandler
import com.jess.arms.http.imageloader.BaseImageLoaderStrategy
import com.jess.arms.http.log.DefaultFormatPrinter
import com.jess.arms.http.log.FormatPrinter
import com.jess.arms.http.log.RequestInterceptor
import com.jess.arms.integration.cache.Cache
import com.jess.arms.integration.cache.CacheType
import com.jess.arms.integration.cache.IntelligentCache
import com.jess.arms.integration.cache.LruCache
import com.jess.arms.utils.DataHelper
import com.jess.arms.utils.Preconditions

import java.io.File
import java.util.ArrayList
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import me.jessyan.rxerrorhandler.handler.listener.ResponseErrorListener
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.internal.Util

/**
 * ================================================
 * 框架独创的建造者模式 [Module],可向框架中注入外部配置的自定义参数
 *
 * @see [GlobalConfigModule Wiki 官方文档](https://github.com/JessYanCoding/MVPArms/wiki.3.1)
 * Created by JessYan on 2016/3/14.
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
@Module
class GlobalConfigModule private constructor(builder: Builder) {
    private val mApiUrl: HttpUrl?
    private val mBaseUrl: BaseUrl?
    private val mLoaderStrategy: BaseImageLoaderStrategy<*>?
    private val mHandler: GlobalHttpHandler?
    private val mInterceptors: List<Interceptor>?
    private val mErrorListener: ResponseErrorListener?
    private val mCacheFile: File?
    private val mRetrofitConfiguration: ClientModule.RetrofitConfiguration?
    private val mOkhttpConfiguration: ClientModule.OkhttpConfiguration?
    private val mRxCacheConfiguration: ClientModule.RxCacheConfiguration?
    private val mGsonConfiguration: AppModule.GsonConfiguration?
    private val mPrintHttpLogLevel: RequestInterceptor.Level?
    private val mFormatPrinter: FormatPrinter?
    private val mCacheFactory: Cache.Factory?
    private val mExecutorService: ExecutorService?

    init {
        this.mApiUrl = builder.apiUrl
        this.mBaseUrl = builder.baseUrl
        this.mLoaderStrategy = builder.loaderStrategy
        this.mHandler = builder.handler
        this.mInterceptors = builder.interceptors
        this.mErrorListener = builder.responseErrorListener
        this.mCacheFile = builder.cacheFile
        this.mRetrofitConfiguration = builder.retrofitConfiguration
        this.mOkhttpConfiguration = builder.okhttpConfiguration
        this.mRxCacheConfiguration = builder.rxCacheConfiguration
        this.mGsonConfiguration = builder.gsonConfiguration
        this.mPrintHttpLogLevel = builder.printHttpLogLevel
        this.mFormatPrinter = builder.formatPrinter
        this.mCacheFactory = builder.cacheFactory
        this.mExecutorService = builder.executorService
    }

    @Singleton
    @Provides
    internal fun provideInterceptors(): List<Interceptor>? {
        return mInterceptors
    }

    /**
     * 提供 BaseUrl,默认使用 <"https://api.github.com/">
     *
     * @return
     */
    @Singleton
    @Provides
    internal fun provideBaseUrl(): HttpUrl? {
        if (mBaseUrl != null) {
            val httpUrl = mBaseUrl.url()
            if (httpUrl != null) {
                return httpUrl
            }
        }
        return mApiUrl ?: HttpUrl.parse("https://api.github.com/")
    }

    /**
     * 提供图片加载框架,默认使用 [Glide]
     *
     * @return
     */
    @Singleton
    @Provides
    internal fun provideImageLoaderStrategy(): BaseImageLoaderStrategy<*>? {
        return mLoaderStrategy
    }

    /**
     * 提供处理 Http 请求和响应结果的处理类
     *
     * @return
     */
    @Singleton
    @Provides
    internal fun provideGlobalHttpHandler(): GlobalHttpHandler? {
        return mHandler
    }

    /**
     * 提供缓存文件
     */
    @Singleton
    @Provides
    internal fun provideCacheFile(application: Application): File {
        return mCacheFile ?: DataHelper.getCacheFile(application)
    }

    /**
     * 提供处理 RxJava 错误的管理器的回调
     *
     * @return
     */
    @Singleton
    @Provides
    internal fun provideResponseErrorListener(): ResponseErrorListener {
        return mErrorListener ?: ResponseErrorListener.EMPTY
    }

    @Singleton
    @Provides
    internal fun provideRetrofitConfiguration(): ClientModule.RetrofitConfiguration? {
        return mRetrofitConfiguration
    }

    @Singleton
    @Provides
    internal fun provideOkhttpConfiguration(): ClientModule.OkhttpConfiguration? {
        return mOkhttpConfiguration
    }

    @Singleton
    @Provides
    internal fun provideRxCacheConfiguration(): ClientModule.RxCacheConfiguration? {
        return mRxCacheConfiguration
    }

    @Singleton
    @Provides
    internal fun provideGsonConfiguration(): AppModule.GsonConfiguration? {
        return mGsonConfiguration
    }

    @Singleton
    @Provides
    internal fun providePrintHttpLogLevel(): RequestInterceptor.Level {
        return mPrintHttpLogLevel ?: RequestInterceptor.Level.ALL
    }

    @Singleton
    @Provides
    internal fun provideFormatPrinter(): FormatPrinter {
        return mFormatPrinter ?: DefaultFormatPrinter()
    }

    @Singleton
    @Provides
    internal fun provideCacheFactory(application: Application): Cache.Factory {
        return mCacheFactory ?: Cache.Factory { type ->
            //若想自定义 LruCache 的 size, 或者不想使用 LruCache, 想使用自己自定义的策略
            //使用 GlobalConfigModule.Builder#cacheFactory() 即可扩展
            when (type.cacheTypeId) {
                //Activity、Fragment 以及 Extras 使用 IntelligentCache (具有 LruCache 和 可永久存储数据的 Map)
                CacheType.EXTRAS_TYPE_ID, CacheType.ACTIVITY_CACHE_TYPE_ID, CacheType.FRAGMENT_CACHE_TYPE_ID -> IntelligentCache(type.calculateCacheSize(application))
                //其余使用 LruCache (当达到最大容量时可根据 LRU 算法抛弃不合规数据)
                else -> LruCache(type.calculateCacheSize(application))
            }
        }
    }

    /**
     * 返回一个全局公用的线程池,适用于大多数异步需求。
     * 避免多个线程池创建带来的资源消耗。
     *
     * @return [Executor]
     */
    @Singleton
    @Provides
    internal fun provideExecutorService(): ExecutorService {
        return mExecutorService ?: ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                SynchronousQueue(), Util.threadFactory("Arms Executor", false))
    }

    class Builder private constructor() {
        private var apiUrl: HttpUrl? = null
        private var baseUrl: BaseUrl? = null
        private var loaderStrategy: BaseImageLoaderStrategy<*>? = null
        private var handler: GlobalHttpHandler? = null
        private var interceptors: MutableList<Interceptor>? = null
        private var responseErrorListener: ResponseErrorListener? = null
        private var cacheFile: File? = null
        private var retrofitConfiguration: ClientModule.RetrofitConfiguration? = null
        private var okhttpConfiguration: ClientModule.OkhttpConfiguration? = null
        private var rxCacheConfiguration: ClientModule.RxCacheConfiguration? = null
        private var gsonConfiguration: AppModule.GsonConfiguration? = null
        private var printHttpLogLevel: RequestInterceptor.Level? = null
        private var formatPrinter: FormatPrinter? = null
        private var cacheFactory: Cache.Factory? = null
        private var executorService: ExecutorService? = null

        fun baseurl(baseUrl: String): Builder {//基础url
            if (TextUtils.isEmpty(baseUrl)) {
                throw NullPointerException("BaseUrl can not be empty")
            }
            this.apiUrl = HttpUrl.parse(baseUrl)
            return this
        }

        fun baseurl(baseUrl: BaseUrl): Builder {
            this.baseUrl = Preconditions.checkNotNull(baseUrl, BaseUrl::class.java.canonicalName!! + "can not be null.")
            return this
        }

        fun imageLoaderStrategy(loaderStrategy: BaseImageLoaderStrategy<*>): Builder {//用来请求网络图片
            this.loaderStrategy = loaderStrategy
            return this
        }

        fun globalHttpHandler(handler: GlobalHttpHandler): Builder {//用来处理http响应结果
            this.handler = handler
            return this
        }

        fun addInterceptor(interceptor: Interceptor): Builder {//动态添加任意个interceptor
            if (interceptors == null)
                interceptors = ArrayList()
            this.interceptors!!.add(interceptor)
            return this
        }

        fun responseErrorListener(listener: ResponseErrorListener): Builder {//处理所有RxJava的onError逻辑
            this.responseErrorListener = listener
            return this
        }

        fun cacheFile(cacheFile: File): Builder {
            this.cacheFile = cacheFile
            return this
        }

        fun retrofitConfiguration(retrofitConfiguration: ClientModule.RetrofitConfiguration): Builder {
            this.retrofitConfiguration = retrofitConfiguration
            return this
        }

        fun okhttpConfiguration(okhttpConfiguration: ClientModule.OkhttpConfiguration): Builder {
            this.okhttpConfiguration = okhttpConfiguration
            return this
        }

        fun rxCacheConfiguration(rxCacheConfiguration: ClientModule.RxCacheConfiguration): Builder {
            this.rxCacheConfiguration = rxCacheConfiguration
            return this
        }

        fun gsonConfiguration(gsonConfiguration: AppModule.GsonConfiguration): Builder {
            this.gsonConfiguration = gsonConfiguration
            return this
        }

        fun printHttpLogLevel(printHttpLogLevel: RequestInterceptor.Level): Builder {//是否让框架打印 Http 的请求和响应信息
            this.printHttpLogLevel = Preconditions.checkNotNull(printHttpLogLevel, "The printHttpLogLevel can not be null, use RequestInterceptor.Level.NONE instead.")
            return this
        }

        fun formatPrinter(formatPrinter: FormatPrinter): Builder {
            this.formatPrinter = Preconditions.checkNotNull(formatPrinter, FormatPrinter::class.java.canonicalName!! + "can not be null.")
            return this
        }

        fun cacheFactory(cacheFactory: Cache.Factory): Builder {
            this.cacheFactory = cacheFactory
            return this
        }

        fun executorService(executorService: ExecutorService): Builder {
            this.executorService = executorService
            return this
        }

        fun build(): GlobalConfigModule {
            return GlobalConfigModule(this)
        }
    }

    companion object {

        fun builder(): Builder {
            return Builder()
        }
    }
}
