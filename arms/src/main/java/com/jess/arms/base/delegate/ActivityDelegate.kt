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
package com.jess.arms.base.delegate

import android.app.Activity
import android.os.Bundle

/**
 * ================================================
 * [Activity] 代理类,用于框架内部在每个 [Activity] 的对应生命周期中插入需要的逻辑
 *
 * @see ActivityDelegateImpl
 *
 * @see [ActivityDelegate wiki 官方文档](https://github.com/JessYanCoding/MVPArms/wiki.3.13)
 * Created by JessYan on 26/04/2017 20:23
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
interface ActivityDelegate {

    fun onCreate(savedInstanceState: Bundle?)

    fun onStart()

    fun onResume()

    fun onPause()

    fun onStop()

    fun onSaveInstanceState(outState: Bundle)

    fun onDestroy()

    companion object {
        val LAYOUT_LINEARLAYOUT = "LinearLayout"
        val LAYOUT_FRAMELAYOUT = "FrameLayout"
        val LAYOUT_RELATIVELAYOUT = "RelativeLayout"
        val ACTIVITY_DELEGATE = "ACTIVITY_DELEGATE"
    }
}
