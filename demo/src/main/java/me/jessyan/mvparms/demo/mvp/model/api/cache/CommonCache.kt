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
package me.jessyan.mvparms.demo.mvp.model.api.cache

import java.util.concurrent.TimeUnit

import io.reactivex.Observable
import io.rx_cache2.DynamicKey
import io.rx_cache2.EvictProvider
import io.rx_cache2.LifeCache
import io.rx_cache2.Reply
import io.rx_cache2.internal.RxCache
import me.jessyan.mvparms.demo.mvp.model.entity.User

/**
 * ================================================
 * 展示 [RxCache.using] 中需要传入的 Providers 的使用方式
 *
 *
 * Created by JessYan on 08/30/2016 13:53
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
interface CommonCache {

    @LifeCache(duration = 2, timeUnit = TimeUnit.MINUTES)
    fun getUsers(users: Observable<List<User>>, idLastUserQueried: DynamicKey, evictProvider: EvictProvider): Observable<Reply<List<User>>>
}
