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
package com.jess.arms.integration

import android.app.Application
import android.content.Context
import com.jess.arms.integration.cache.Cache
import com.jess.arms.integration.cache.CacheType
import com.jess.arms.mvp.IModel
import com.jess.arms.utils.Preconditions
import dagger.Lazy
import io.reactivex.Observable
import io.reactivex.Single
import io.rx_cache2.internal.RxCache
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import javax.inject.Inject
import javax.inject.Singleton
import retrofit2.Retrofit

/**
 * ================================================
 * 用来管理网络请求层,以及数据缓存层,以后可能添加数据库请求层
 * 提供给 [IModel] 层必要的 Api 做数据处理
 *
 * @see [RepositoryManager wiki 官方文档](https://github.com/JessYanCoding/MVPArms/wiki.2.3)
 * Created by JessYan on 13/04/2017 09:52
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
@Singleton
class RepositoryManager @Inject
constructor() : IRepositoryManager {

    @Inject
    internal var mRetrofit: Lazy<Retrofit>? = null
    @Inject
    internal var mRxCache: Lazy<RxCache>? = null
    @Inject
    internal var mApplication: Application? = null
    @Inject
    internal var mCachefactory: Cache.Factory? = null
    private var mRetrofitServiceCache: Cache<String, Any>? = null
    private var mCacheServiceCache: Cache<String, Any>? = null

    override val context: Context
        get() = mApplication

    /**
     * 根据传入的 Class 获取对应的 Retrofit service
     *
     * @param serviceClass ApiService class
     * @param <T> ApiService class
     * @return ApiService
    </T> */
    @Synchronized
    override fun <T> obtainRetrofitService(serviceClass: Class<T>): T {
        return createWrapperService(serviceClass)
    }

    /**
     * 根据 https://zhuanlan.zhihu.com/p/40097338 对 Retrofit 进行的优化
     *
     * @param serviceClass ApiService class
     * @param <T> ApiService class
     * @return ApiService
    </T> */
    private fun <T> createWrapperService(serviceClass: Class<T>): T {
        Preconditions.checkNotNull(serviceClass, "serviceClass == null")

        // 二次代理
        return Proxy.newProxyInstance(serviceClass.classLoader,
                arrayOf<Class<*>>(serviceClass), InvocationHandler { proxy, method, args ->
            // 此处在调用 serviceClass 中的方法时触发

            if (method.returnType == Observable<*>::class.java) {
                // 如果方法返回值是 Observable 的话，则包一层再返回，
                // 只包一层 defer 由外部去控制耗时方法以及网络请求所处线程，
                // 如此对原项目的影响为 0，且更可控。
                return@InvocationHandler Observable.defer {
                    val service = getRetrofitService(serviceClass)
                    // 执行真正的 Retrofit 动态代理的方法
                    getRetrofitMethod<T>(service!!, method)
                            .invoke(service, *args) as Observable<*>
                }
            } else if (method.returnType == Single<*>::class.java) {
                // 如果方法返回值是 Single 的话，则包一层再返回。
                return@Observable.defer Single . defer < Any >{
                    val service = getRetrofitService(serviceClass)
                    // 执行真正的 Retrofit 动态代理的方法
                    getRetrofitMethod<T>(service!!, method)
                            .invoke(service, *args) as Single<*>
                }
            }

            // 返回值不是 Observable 或 Single 的话不处理。
            val service = getRetrofitService(serviceClass)
            getRetrofitMethod<T>(service!!, method).invoke(service, *args)
        }) as T
    }

    /**
     * 根据传入的 Class 获取对应的 Retrofit service
     *
     * @param serviceClass ApiService class
     * @param <T> ApiService class
     * @return ApiService
    </T> */
    private fun <T> getRetrofitService(serviceClass: Class<T>): T? {
        if (mRetrofitServiceCache == null) {
            mRetrofitServiceCache = mCachefactory!!.build(CacheType.RETROFIT_SERVICE_CACHE)
        }
        Preconditions.checkNotNull(mRetrofitServiceCache,
                "Cannot return null from a Cache.Factory#build(int) method")
        var retrofitService = mRetrofitServiceCache!!.get(serviceClass.canonicalName) as T?
        if (retrofitService == null) {
            retrofitService = mRetrofit!!.get().create(serviceClass)
            mRetrofitServiceCache!!.put(serviceClass.canonicalName, retrofitService)
        }
        return retrofitService
    }

    @Throws(NoSuchMethodException::class)
    private fun <T> getRetrofitMethod(service: T, method: Method): Method {
        return service.javaClass.getMethod(method.name, *method.parameterTypes)
    }

    /**
     * 根据传入的 Class 获取对应的 RxCache service
     *
     * @param cacheClass Cache class
     * @param <T> Cache class
     * @return Cache
    </T> */
    @Synchronized
    override fun <T> obtainCacheService(cacheClass: Class<T>): T {
        Preconditions.checkNotNull(cacheClass, "cacheClass == null")
        if (mCacheServiceCache == null) {
            mCacheServiceCache = mCachefactory!!.build(CacheType.CACHE_SERVICE_CACHE)
        }
        Preconditions.checkNotNull(mCacheServiceCache,
                "Cannot return null from a Cache.Factory#build(int) method")
        var cacheService = mCacheServiceCache!!.get(cacheClass.canonicalName) as T?
        if (cacheService == null) {
            cacheService = mRxCache!!.get().using(cacheClass)
            mCacheServiceCache!!.put(cacheClass.canonicalName, cacheService)
        }
        return cacheService
    }

    /**
     * 清理所有缓存
     */
    override fun clearAllCache() {
        mRxCache!!.get().evictAll().subscribe()
    }
}
