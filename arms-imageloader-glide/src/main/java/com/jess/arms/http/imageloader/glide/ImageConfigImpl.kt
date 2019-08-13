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

import android.widget.ImageView

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.jess.arms.http.imageloader.BaseImageLoaderStrategy
import com.jess.arms.http.imageloader.ImageConfig
import com.jess.arms.http.imageloader.ImageLoader

/**
 * ================================================
 * 这里存放图片请求的配置信息,可以一直扩展字段,如果外部调用时想让图片加载框架
 * 做一些操作,比如清除缓存或者切换缓存策略,则可以定义一个 int 类型的变量,内部根据 switch(int) 做不同的操作
 * 其他操作同理
 *
 *
 * Created by JessYan on 8/5/16 15:19
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
class ImageConfigImpl private constructor(builder: Builder) : ImageConfig() {
    @CacheStrategy.Strategy
    @get:CacheStrategy.Strategy
    val cacheStrategy: Int//0对应DiskCacheStrategy.all,1对应DiskCacheStrategy.NONE,2对应DiskCacheStrategy.SOURCE,3对应DiskCacheStrategy.RESULT
    val fallback: Int //请求 url 为空,则使用此图片作为占位符
    val imageRadius: Int//图片每个圆角的大小
    val blurValue: Int//高斯模糊值, 值越大模糊效果越大
    /**
     * @see {@link Builder.transformation
     */
    @Deprecated("")
    val transformation: BitmapTransformation?//glide用它来改变图形的形状
    val imageViews: Array<ImageView>?
    val isCrossFade: Boolean//是否使用淡入淡出过渡动画
    val isCenterCrop: Boolean//是否将图片剪切为 CenterCrop
    val isCircle: Boolean//是否将图片剪切为圆形
    val isClearMemory: Boolean//清理内存缓存
    val isClearDiskCache: Boolean//清理本地缓存

    val isBlurImage: Boolean
        get() = blurValue > 0

    val isImageRadius: Boolean
        get() = imageRadius > 0

    init {
        this.url = builder.url
        this.imageView = builder.imageView
        this.placeholder = builder.placeholder
        this.errorPic = builder.errorPic
        this.fallback = builder.fallback
        this.cacheStrategy = builder.cacheStrategy
        this.imageRadius = builder.imageRadius
        this.blurValue = builder.blurValue
        this.transformation = builder.transformation
        this.imageViews = builder.imageViews
        this.isCrossFade = builder.isCrossFade
        this.isCenterCrop = builder.isCenterCrop
        this.isCircle = builder.isCircle
        this.isClearMemory = builder.isClearMemory
        this.isClearDiskCache = builder.isClearDiskCache
    }

    class Builder private constructor() {
        private var url: String? = null
        private var imageView: ImageView? = null
        private var placeholder: Int = 0
        private var errorPic: Int = 0
        private var fallback: Int = 0 //请求 url 为空,则使用此图片作为占位符
        @CacheStrategy.Strategy
        private var cacheStrategy: Int = 0//0对应DiskCacheStrategy.all,1对应DiskCacheStrategy.NONE,2对应DiskCacheStrategy.SOURCE,3对应DiskCacheStrategy.RESULT
        private var imageRadius: Int = 0//图片每个圆角的大小
        private var blurValue: Int = 0//高斯模糊值, 值越大模糊效果越大
        /**
         * @see {@link Builder.transformation
         */
        @Deprecated("")
        private var transformation: BitmapTransformation? = null//glide用它来改变图形的形状
        private var imageViews: Array<ImageView>? = null
        private var isCrossFade: Boolean = false//是否使用淡入淡出过渡动画
        private var isCenterCrop: Boolean = false//是否将图片剪切为 CenterCrop
        private var isCircle: Boolean = false//是否将图片剪切为圆形
        private var isClearMemory: Boolean = false//清理内存缓存
        private var isClearDiskCache: Boolean = false//清理本地缓存

        fun url(url: String): Builder {
            this.url = url
            return this
        }

        fun placeholder(placeholder: Int): Builder {
            this.placeholder = placeholder
            return this
        }

        fun errorPic(errorPic: Int): Builder {
            this.errorPic = errorPic
            return this
        }

        fun fallback(fallback: Int): Builder {
            this.fallback = fallback
            return this
        }

        fun imageView(imageView: ImageView): Builder {
            this.imageView = imageView
            return this
        }

        fun cacheStrategy(@CacheStrategy.Strategy cacheStrategy: Int): Builder {
            this.cacheStrategy = cacheStrategy
            return this
        }

        fun imageRadius(imageRadius: Int): Builder {
            this.imageRadius = imageRadius
            return this
        }

        fun blurValue(blurValue: Int): Builder { //blurValue 建议设置为 15
            this.blurValue = blurValue
            return this
        }

        /**
         * 给图片添加 Glide 独有的 BitmapTransformation
         *
         *
         * 因为 BitmapTransformation 是 Glide 独有的类, 所以如果 BitmapTransformation 出现在 [ImageConfigImpl] 中
         * 会使 [ImageLoader] 难以切换为其他图片加载框架, 在 [ImageConfigImpl] 中只能配置基础类型和 Android 包里的类
         * 此 API 会在后面的版本中被删除, 请使用其他 API 替代
         *
         * @param transformation [BitmapTransformation]
         */
        @Deprecated("请使用 {@link #isCircle()}, {@link #isCenterCrop()}, {@link #isImageRadius()} 替代\n" +
                "          如果有其他自定义 BitmapTransformation 的需求, 请自行扩展 {@link BaseImageLoaderStrategy}")
        fun transformation(transformation: BitmapTransformation): Builder {
            this.transformation = transformation
            return this
        }

        fun imageViews(vararg imageViews: ImageView): Builder {
            this.imageViews = imageViews
            return this
        }

        fun isCrossFade(isCrossFade: Boolean): Builder {
            this.isCrossFade = isCrossFade
            return this
        }

        fun isCenterCrop(isCenterCrop: Boolean): Builder {
            this.isCenterCrop = isCenterCrop
            return this
        }

        fun isCircle(isCircle: Boolean): Builder {
            this.isCircle = isCircle
            return this
        }

        fun isClearMemory(isClearMemory: Boolean): Builder {
            this.isClearMemory = isClearMemory
            return this
        }

        fun isClearDiskCache(isClearDiskCache: Boolean): Builder {
            this.isClearDiskCache = isClearDiskCache
            return this
        }

        fun build(): ImageConfigImpl {
            return ImageConfigImpl(this)
        }
    }

    companion object {

        fun builder(): Builder {
            return Builder()
        }
    }
}
