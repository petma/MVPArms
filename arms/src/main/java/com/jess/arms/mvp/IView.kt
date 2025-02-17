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
package com.jess.arms.mvp

import android.app.Activity
import android.content.Intent

import com.jess.arms.utils.ArmsUtils

import com.jess.arms.utils.Preconditions.checkNotNull

/**
 * ================================================
 * 框架要求框架中的每个 View 都需要实现此类, 以满足规范
 *
 *
 * 为了满足部分人的诉求以及向下兼容, [IView] 中的部分方法使用 JAVA 1.8 的默认方法实现, 这样实现类可以按实际需求选择是否实现某些方法
 * 不实现则使用默认方法中的逻辑, 不清楚默认方法的请自行学习
 *
 * @see [View wiki 官方文档](https://github.com/JessYanCoding/MVPArms/wiki.2.4.2)
 * Created by JessYan on 4/22/2016
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
interface IView {

    /**
     * 显示加载
     */
    open fun showLoading() {

    }

    /**
     * 隐藏加载
     */
    open fun hideLoading() {

    }

    /**
     * 显示信息
     *
     * @param message 消息内容, 不能为 `null`
     */
    fun showMessage(message: String)

    /**
     * 跳转 [Activity]
     *
     * @param intent `intent` 不能为 `null`
     */
    open fun launchActivity(intent: Intent) {
        checkNotNull(intent)
        ArmsUtils.startActivity(intent)
    }

    /**
     * 杀死自己
     */
    open fun killMyself() {

    }
}
