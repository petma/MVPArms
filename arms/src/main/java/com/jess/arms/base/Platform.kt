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
package com.jess.arms.base

/**
 * ================================================
 * Created by JessYan on 2018/7/27 15:32
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
object Platform {
    val DEPENDENCY_AUTO_LAYOUT: Boolean
    val DEPENDENCY_SUPPORT_DESIGN: Boolean
    val DEPENDENCY_GLIDE: Boolean
    val DEPENDENCY_ANDROID_EVENTBUS: Boolean
    val DEPENDENCY_EVENTBUS: Boolean

    init {
        DEPENDENCY_AUTO_LAYOUT = findClassByClassName("com.zhy.autolayout.AutoLayoutInfo")
        DEPENDENCY_SUPPORT_DESIGN = findClassByClassName("android.support.design.widget.Snackbar")
        DEPENDENCY_GLIDE = findClassByClassName("com.bumptech.glide.Glide")
        DEPENDENCY_ANDROID_EVENTBUS = findClassByClassName("org.simple.eventbus.EventBus")
        DEPENDENCY_EVENTBUS = findClassByClassName("org.greenrobot.eventbus.EventBus")
    }

    private fun findClassByClassName(className: String): Boolean {
        var hasDependency: Boolean
        try {
            Class.forName(className)
            hasDependency = true
        } catch (e: ClassNotFoundException) {
            hasDependency = false
        }

        return hasDependency
    }
}
