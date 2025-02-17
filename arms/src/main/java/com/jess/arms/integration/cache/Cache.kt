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
package com.jess.arms.integration.cache

import android.app.Application

import com.jess.arms.di.module.GlobalConfigModule


/**
 * ================================================
 * 用于缓存框架中所必需的组件,开发者可通过 [GlobalConfigModule.Builder.cacheFactory] 为框架提供缓存策略
 * 开发者也可以用于自己日常中的使用
 *
 * @see GlobalConfigModule.provideCacheFactory
 * @see LruCache
 * Created by JessYan on 25/09/2017 16:36
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
interface Cache<K, V> {

    /**
     * 返回当前缓存所能允许的最大 size
     *
     * @return `maxSize`
     */
    val maxSize: Int

    interface Factory {

        /**
         * Returns a new cache
         *
         * @param type 框架中需要缓存的模块类型
         * @return [Cache]
         */
        fun build(type: CacheType): Cache<*, *>
    }

    /**
     * 返回当前缓存已占用的总 size
     *
     * @return `size`
     */
    fun size(): Int

    /**
     * 返回这个 `key` 在缓存中对应的 `value`, 如果返回 `null` 说明这个 `key` 没有对应的 `value`
     *
     * @param key `key`
     * @return `value`
     */
    operator fun get(key: K): V?

    /**
     * 将 `key` 和 `value` 以条目的形式加入缓存,如果这个 `key` 在缓存中已经有对应的 `value`
     * 则此 `value` 被新的 `value` 替换并返回,如果为 `null` 说明是一个新条目
     *
     * @param key `key`
     * @param value `value`
     * @return 如果这个 `key` 在容器中已经储存有 `value`, 则返回之前的 `value` 否则返回 `null`
     */
    fun put(key: K, value: V): V?

    /**
     * 移除缓存中这个 `key` 所对应的条目,并返回所移除条目的 value
     * 如果返回为 `null` 则有可能时因为这个 `key` 对应的 value 为 `null` 或条目不存在
     *
     * @param key `key`
     * @return 如果这个 `key` 在容器中已经储存有 `value` 并且删除成功则返回删除的 `value`, 否则返回 `null`
     */
    fun remove(key: K): V?

    /**
     * 如果这个 `key` 在缓存中有对应的 value 并且不为 `null`, 则返回 `true`
     *
     * @param key `key`
     * @return `true` 为在容器中含有这个 `key`, 否则为 `false`
     */
    fun containsKey(key: K): Boolean

    /**
     * 返回当前缓存中含有的所有 `key`
     *
     * @return `keySet`
     */
    fun keySet(): Set<K>

    /**
     * 清除缓存中所有的内容
     */
    fun clear()
}
