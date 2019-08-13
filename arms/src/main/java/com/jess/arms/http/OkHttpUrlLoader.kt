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
package com.jess.arms.http

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import java.io.InputStream
import okhttp3.Call
import okhttp3.OkHttpClient

/**
 * A simple model loader for fetching media over http/https using OkHttp.
 */
class OkHttpUrlLoader// Public API.
(private val client: Call.Factory) : ModelLoader<GlideUrl, InputStream> {

    override fun handles(url: GlideUrl): Boolean {
        return true
    }

    override fun buildLoadData(model: GlideUrl, width: Int, height: Int,
                               options: Options): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(model, OkHttpStreamFetcher(client, model))
    }

    /**
     * The default factory for [OkHttpUrlLoader]s.
     */
    // Public API.
    class Factory
    /**
     * Constructor for a new Factory that runs requests using given client.
     *
     * @param client this is typically an instance of `OkHttpClient`.
     */
    @JvmOverloads constructor(private val client: Call.Factory = getInternalClient()) : ModelLoaderFactory<GlideUrl, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlideUrl, InputStream> {
            return OkHttpUrlLoader(client)
        }

        override fun teardown() {
            // Do nothing, this instance doesn't own the client.
        }

        companion object {
            @Volatile
            private var internalClient: Call.Factory? = null

            private fun getInternalClient(): Call.Factory? {
                if (internalClient == null) {
                    synchronized(Factory::class.java) {
                        if (internalClient == null) {
                            internalClient = OkHttpClient()
                        }
                    }
                }
                return internalClient
            }
        }
    }
    /**
     * Constructor for a new Factory that runs requests using a static singleton client.
     */
}
