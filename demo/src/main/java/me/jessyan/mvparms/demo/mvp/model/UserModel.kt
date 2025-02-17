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
package me.jessyan.mvparms.demo.mvp.model

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent

import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel

import javax.inject.Inject

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.annotations.NonNull
import io.reactivex.functions.Function
import io.rx_cache2.DynamicKey
import io.rx_cache2.EvictDynamicKey
import me.jessyan.mvparms.demo.mvp.contract.UserContract
import me.jessyan.mvparms.demo.mvp.model.api.cache.CommonCache
import me.jessyan.mvparms.demo.mvp.model.api.service.UserService
import me.jessyan.mvparms.demo.mvp.model.entity.User
import timber.log.Timber

/**
 * ================================================
 * 展示 Model 的用法
 *
 * @see [Model wiki 官方文档](https://github.com/JessYanCoding/MVPArms/wiki.2.4.3)
 * Created by JessYan on 09/04/2016 10:56
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
@ActivityScope
class UserModel @Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), UserContract.Model {

    override fun getUsers(lastIdQueried: Int, update: Boolean): Observable<List<User>> {
        //使用rxcache缓存,上拉刷新则不读取缓存,加载更多读取缓存
        return Observable.just<Observable<List<User>>>(mRepositoryManager
                .obtainRetrofitService<UserService>(UserService::class.java!!)
                .getUsers(lastIdQueried, USERS_PER_PAGE))
                .flatMap { listObservable ->
                    mRepositoryManager.obtainCacheService<CommonCache>(CommonCache::class.java!!)
                            .getUsers(listObservable, DynamicKey(lastIdQueried), EvictDynamicKey(update))
                            .map({ listReply -> listReply.getData() })
                }

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    internal fun onPause() {
        Timber.d("Release Resource")
    }

    companion object {
        val USERS_PER_PAGE = 10
    }

}
