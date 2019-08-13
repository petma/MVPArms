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
package com.jess.arms.http.imageloader.glide

import android.graphics.Bitmap
import androidx.annotation.IntRange

import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.jess.arms.utils.FastBlur

import java.security.MessageDigest

/**
 * ================================================
 * 高斯模糊
 *
 *
 * Created by JessYan on 03/04/2018 15:14
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
class BlurTransformation(@IntRange(from = 0) radius: Int) : BitmapTransformation() {
    private var mRadius = DEFAULT_RADIUS

    init {
        mRadius = radius
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)

    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap? {
        return FastBlur.doBlur(toTransform, mRadius, true)
    }

    override fun equals(o: Any?): Boolean {
        return o is BlurTransformation
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }

    companion object {
        private val ID = BlurTransformation::class.java.name
        private val ID_BYTES = ID.toByteArray(Key.CHARSET)
        val DEFAULT_RADIUS = 15
    }
}
