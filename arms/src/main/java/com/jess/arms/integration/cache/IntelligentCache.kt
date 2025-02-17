/*
 * Copyright 2018 JessYan
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

import com.jess.arms.utils.Preconditions

import java.util.HashMap

/**
 * ================================================
 * [IntelligentCache] 含有可将数据永久存储至内存中的存储容器 [.mMap], 和当达到最大容量时可根据 LRU
 * 算法抛弃不合规数据的存储容器 [.mCache]
 *
 *
 * [IntelligentCache] 可根据您传入的 `key` 智能的判断您需要将数据存储至哪个存储容器, 从而针对数据
 * 的不同特性进行不同的存储优化
 *
 *
 * 调用 [IntelligentCache.put] 方法, 使用 [.KEY_KEEP] + `key` 作为 key 传入的
 * `value` 可存储至 [.mMap] (数据永久存储至内存中, 适合比较重要的数据) 中, 否则储存至 [.mCache]
 *
 *
 * Created by JessYan on 12/04/2018 16:06
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
class IntelligentCache<V>(size: Int) : Cache<String, V> {
    private val mMap: MutableMap<String, V>//可将数据永久存储至内存中的存储容器
    private val mCache: Cache<String, V>//当达到最大容量时可根据 LRU 算法抛弃不合规数据的存储容器

    /**
     * 将 [.mMap] 和 [.mCache] 的 `maxSize` 相加后返回
     *
     * @return 相加后的 `maxSize`
     */
    override val maxSize: Int
        @Synchronized get() = mMap.size + mCache.maxSize

    init {
        this.mMap = HashMap()
        this.mCache = LruCache(size)
    }

    /**
     * 将 [.mMap] 和 [.mCache] 的 `size` 相加后返回
     *
     * @return 相加后的 `size`
     */
    @Synchronized
    override fun size(): Int {
        return mMap.size + mCache.size()
    }

    /**
     * 如果在 `key` 中使用 [.KEY_KEEP] 作为其前缀, 则操作 [.mMap], 否则操作 [.mCache]
     *
     * @param key `key`
     * @return `value`
     */
    @Synchronized
    override fun get(key: String): V? {
        return if (key.startsWith(KEY_KEEP)) {
            mMap[key]
        } else mCache.get(key)
    }

    /**
     * 如果在 `key` 中使用 [.KEY_KEEP] 作为其前缀, 则操作 [.mMap], 否则操作 [.mCache]
     *
     * @param key   `key`
     * @param value `value`
     * @return 如果这个 `key` 在容器中已经储存有 `value`, 则返回之前的 `value` 否则返回 `null`
     */
    @Synchronized
    override fun put(key: String, value: V): V? {
        return if (key.startsWith(KEY_KEEP)) {
            mMap.put(key, value)
        } else mCache.put(key, value)
    }

    /**
     * 如果在 `key` 中使用 [.KEY_KEEP] 作为其前缀, 则操作 [.mMap], 否则操作 [.mCache]
     *
     * @param key `key`
     * @return 如果这个 `key` 在容器中已经储存有 `value` 并且删除成功则返回删除的 `value`, 否则返回 `null`
     */
    @Synchronized
    override fun remove(key: String): V? {
        return if (key.startsWith(KEY_KEEP)) {
            mMap.remove(key)
        } else mCache.remove(key)
    }

    /**
     * 如果在 `key` 中使用 [.KEY_KEEP] 作为其前缀, 则操作 [.mMap], 否则操作 [.mCache]
     *
     * @param key `key`
     * @return `true` 为在容器中含有这个 `key`, 否则为 `false`
     */
    @Synchronized
    override fun containsKey(key: String): Boolean {
        return if (key.startsWith(KEY_KEEP)) {
            mMap.containsKey(key)
        } else mCache.containsKey(key)
    }

    /**
     * 将 [.mMap] 和 [.mCache] 的 `keySet` 合并返回
     *
     * @return 合并后的 `keySet`
     */
    @Synchronized
    override fun keySet(): Set<String> {
        val set = mCache.keySet()
        set.addAll(mMap.keys)
        return set
    }

    /**
     * 清空 [.mMap] 和 [.mCache] 容器
     */
    override fun clear() {
        mCache.clear()
        mMap.clear()
    }

    companion object {
        val KEY_KEEP = "Keep="

        /**
         * 使用此方法返回的值作为 key, 可以将数据永久存储至内存中
         *
         * @param key `key`
         * @return Keep= + `key`
         */
        fun getKeyOfKeep(key: String): String {
            Preconditions.checkNotNull(key, "key == null")
            return IntelligentCache.KEY_KEEP + key
        }
    }
}
