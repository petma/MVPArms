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
package me.jessyan.mvparms.demo.mvp.ui.adapter

import android.view.View

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jess.arms.di.component.AppComponent
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.http.imageloader.glide.ImageConfigImpl
import com.jess.arms.utils.ArmsUtils

import me.jessyan.mvparms.demo.R
import me.jessyan.mvparms.demo.mvp.model.entity.User


/**
 * ================================================
 * 展示 [DefaultAdapter] 的用法
 *
 *
 * Created by JessYan on 09/04/2016 12:57
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
class UserAdapter
/**
 * 用于加载图片的管理类, 默认使用 Glide, 使用策略模式, 可替换框架
 */
(infos: List<User>, internal var mImageLoader: ImageLoader)//可以在任何可以拿到 Context 的地方, 拿到 AppComponent, 从而得到用 Dagger 管理的单例对象
/*      mAppComponent = ArmsUtils.obtainAppComponentFromContext(mContext);
        mImageLoader = mAppComponent.imageLoader();*/ : BaseQuickAdapter<User, BaseViewHolder>(R.layout.recycle_list, infos) {

    override fun convert(helper: BaseViewHolder, item: User) {
        helper.setText(R.id.tv_name, item.login)
        mImageLoader.loadImage(mContext,
                ImageConfigImpl
                        .builder()
                        .url(item.avatarUrl)
                        .imageView(helper.getView(R.id.iv_avatar))
                        .build())
    }

}
