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

import android.util.Log

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.HttpException
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.util.ContentLengthInputStream
import com.bumptech.glide.util.Preconditions

import java.io.IOException
import java.io.InputStream

import okhttp3.Call
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody

/**
 * Fetches an [InputStream] using the okhttp library.
 */
class OkHttpStreamFetcher// Public API.
(private val client: Call.Factory, private val url: GlideUrl) : DataFetcher<InputStream>, okhttp3.Callback {
    private var stream: InputStream? = null
    private var responseBody: ResponseBody? = null
    private var callback: DataFetcher.DataCallback<in InputStream>? = null
    // call may be accessed on the main thread while the object is in use on other threads. All other
    // accesses to variables may occur on different threads, but only one at a time.
    @Volatile
    private var call: Call? = null

    override fun loadData(priority: Priority,
                          callback: DataFetcher.DataCallback<in InputStream>) {
        val requestBuilder = Request.Builder().url(url.toStringUrl())
        for ((key, value) in url.headers) {
            requestBuilder.addHeader(key, value)
        }
        val request = requestBuilder.build()
        this.callback = callback

        call = client.newCall(request)
        call!!.enqueue(this)
    }

    override fun onFailure(call: Call, e: IOException) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "OkHttp failed to obtain result", e)
        }

        callback!!.onLoadFailed(e)
    }

    override fun onResponse(call: Call, response: Response) {
        responseBody = response.body()
        if (response.isSuccessful) {
            val contentLength = Preconditions.checkNotNull(responseBody).contentLength()
            stream = ContentLengthInputStream.obtain(responseBody!!.byteStream(), contentLength)
            callback!!.onDataReady(stream)
        } else {
            callback!!.onLoadFailed(HttpException(response.message(), response.code()))
        }
    }

    override fun cleanup() {
        try {
            if (stream != null) {
                stream!!.close()
            }
        } catch (e: IOException) {
            // Ignored
        }

        if (responseBody != null) {
            responseBody!!.close()
        }
        callback = null
    }

    override fun cancel() {
        val local = call
        local?.cancel()
    }

    override fun getDataClass(): Class<InputStream> {
        return InputStream::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.REMOTE
    }

    companion object {
        private val TAG = "OkHttpFetcher"
    }
}