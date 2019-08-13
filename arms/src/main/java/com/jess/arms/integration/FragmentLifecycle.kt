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
package com.jess.arms.integration

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import android.view.View
import com.jess.arms.base.delegate.FragmentDelegate
import com.jess.arms.base.delegate.FragmentDelegateImpl
import com.jess.arms.base.delegate.IFragment
import com.jess.arms.integration.cache.Cache
import com.jess.arms.integration.cache.IntelligentCache
import com.jess.arms.utils.Preconditions
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ================================================
 * [FragmentManager.FragmentLifecycleCallbacks] 默认实现类
 * 通过 [FragmentDelegate] 管理 [Fragment]
 *
 *
 * Created by JessYan on 04/09/2017 16:04
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
@Singleton
class FragmentLifecycle @Inject
constructor() : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
        if (f is IFragment) {
            var fragmentDelegate = fetchFragmentDelegate(f)
            if (fragmentDelegate == null || !fragmentDelegate.isAdded) {
                val cache = getCacheFromFragment(f as IFragment)
                fragmentDelegate = FragmentDelegateImpl(fm, f)
                //使用 IntelligentCache.KEY_KEEP 作为 key 的前缀, 可以使储存的数据永久存储在内存中
                //否则存储在 LRU 算法的存储空间中, 前提是 Fragment 使用的是 IntelligentCache (框架默认使用)
                cache.put(IntelligentCache.getKeyOfKeep(FragmentDelegate.FRAGMENT_DELEGATE), fragmentDelegate)
            }
            fragmentDelegate.onAttach(context)
        }
    }

    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        val fragmentDelegate = fetchFragmentDelegate(f)
        fragmentDelegate?.onCreate(savedInstanceState)
    }

    override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
        val fragmentDelegate = fetchFragmentDelegate(f)
        fragmentDelegate?.onCreateView(v, savedInstanceState)
    }

    override fun onFragmentActivityCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        val fragmentDelegate = fetchFragmentDelegate(f)
        fragmentDelegate?.onActivityCreate(savedInstanceState)
    }

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
        val fragmentDelegate = fetchFragmentDelegate(f)
        fragmentDelegate?.onStart()
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        val fragmentDelegate = fetchFragmentDelegate(f)
        fragmentDelegate?.onResume()
    }

    override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
        val fragmentDelegate = fetchFragmentDelegate(f)
        fragmentDelegate?.onPause()
    }

    override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
        val fragmentDelegate = fetchFragmentDelegate(f)
        fragmentDelegate?.onStop()
    }

    override fun onFragmentSaveInstanceState(fm: FragmentManager, f: Fragment, outState: Bundle) {
        val fragmentDelegate = fetchFragmentDelegate(f)
        fragmentDelegate?.onSaveInstanceState(outState)
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
        val fragmentDelegate = fetchFragmentDelegate(f)
        fragmentDelegate?.onDestroyView()
    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        val fragmentDelegate = fetchFragmentDelegate(f)
        fragmentDelegate?.onDestroy()
    }

    override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
        val fragmentDelegate = fetchFragmentDelegate(f)
        fragmentDelegate?.onDetach()
    }

    private fun fetchFragmentDelegate(fragment: Fragment): FragmentDelegate? {
        if (fragment is IFragment) {
            val cache = getCacheFromFragment(fragment as IFragment)
            return cache.get(IntelligentCache.getKeyOfKeep(FragmentDelegate.FRAGMENT_DELEGATE)) as FragmentDelegate?
        }
        return null
    }

    private fun getCacheFromFragment(fragment: IFragment): Cache<String, Any> {
        val cache = fragment.provideCache()
        Preconditions.checkNotNull(cache, "%s cannot be null on Fragment", Cache<*, *>::class.java.name)
        return cache
    }
}
