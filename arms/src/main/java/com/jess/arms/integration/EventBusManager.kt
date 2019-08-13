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
package com.jess.arms.integration

import java.lang.reflect.Method

import com.jess.arms.base.Platform.DEPENDENCY_ANDROID_EVENTBUS
import com.jess.arms.base.Platform.DEPENDENCY_EVENTBUS

/**
 * ================================================
 * EventBus 的管理类, Arms 核心库并不会依赖某个 EventBus, 如果您想使用 EventBus, 则请在项目中自行依赖对应的 EventBus, 如果不想使用则不依赖
 * 支持 greenrobot 的 EventBus 和畅销书 《Android源码设计模式解析与实战》的作者 何红辉 所作的 AndroidEventBus
 * 这个类并不能完全做到 EventBus 对外界的零耦合, 只能降低耦合, 因为两个 EventBus 的部分功能使用方法差别太大, 做到完全解耦代价太大
 * 允许同时使用两个 EventBus 但不建议这样做, 建议使用 AndroidEventBus, 特别是组件化项目, 原因请看 https://github.com/hehonghui/AndroidEventBus/issues/49
 *
 *
 * Created by JessYan on 2018/8/1 15:28
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
class EventBusManager private constructor() {

    /**
     * 注册订阅者, 允许在项目中同时依赖两个 EventBus, 只要您喜欢
     *
     * @param subscriber 订阅者
     */
    fun register(subscriber: Any) {
        if (DEPENDENCY_ANDROID_EVENTBUS) {
            org.simple.eventbus.EventBus.getDefault().register(subscriber)
        }
        if (DEPENDENCY_EVENTBUS) {
            if (haveAnnotation(subscriber)) {
                org.greenrobot.eventbus.EventBus.getDefault().register(subscriber)
            }
        }
    }

    /**
     * 注销订阅者, 允许在项目中同时依赖两个 EventBus, 只要您喜欢
     *
     * @param subscriber 订阅者
     */
    fun unregister(subscriber: Any) {
        if (DEPENDENCY_ANDROID_EVENTBUS) {
            org.simple.eventbus.EventBus.getDefault().unregister(subscriber)
        }
        if (DEPENDENCY_EVENTBUS) {
            if (haveAnnotation(subscriber)) {
                org.greenrobot.eventbus.EventBus.getDefault().unregister(subscriber)
            }
        }
    }

    /**
     * 发送事件, 如果您在项目中同时依赖了两个 EventBus, 请自己使用想使用的 EventBus 的 Api 发送事件
     *
     * @param event 事件
     */
    fun post(event: Any) {
        if (DEPENDENCY_ANDROID_EVENTBUS) {
            org.simple.eventbus.EventBus.getDefault().post(event)
        } else if (DEPENDENCY_EVENTBUS) {
            org.greenrobot.eventbus.EventBus.getDefault().post(event)
        }
    }

    /**
     * 发送黏性事件, 如果您在项目中同时依赖了两个 EventBus, 请自己使用想使用的 EventBus 的 Api 发送黏性事件
     *
     * @param event 事件
     */
    fun postSticky(event: Any) {
        if (DEPENDENCY_ANDROID_EVENTBUS) {
            org.simple.eventbus.EventBus.getDefault().postSticky(event)
        } else if (DEPENDENCY_EVENTBUS) {
            org.greenrobot.eventbus.EventBus.getDefault().postSticky(event)
        }
    }

    /**
     * 注销黏性事件, 如果您在项目中同时依赖了两个 EventBus, 请自己使用想使用的 EventBus 的 Api 注销黏性事件
     *
     * @param eventType
     * @param <T>
     * @return
    </T> */
    fun <T> removeStickyEvent(eventType: Class<T>): T? {
        if (DEPENDENCY_ANDROID_EVENTBUS) {
            org.simple.eventbus.EventBus.getDefault().removeStickyEvent(eventType)
            return null
        } else if (DEPENDENCY_EVENTBUS) {
            return org.greenrobot.eventbus.EventBus.getDefault().removeStickyEvent(eventType)
        }
        return null
    }

    /**
     * 清除订阅者和事件的缓存, 如果您在项目中同时依赖了两个 EventBus, 请自己使用想使用的 EventBus 的 Api 清除订阅者和事件的缓存
     */
    fun clear() {
        if (DEPENDENCY_ANDROID_EVENTBUS) {
            org.simple.eventbus.EventBus.getDefault().clear()
        } else if (DEPENDENCY_EVENTBUS) {
            org.greenrobot.eventbus.EventBus.clearCaches()
        }
    }

    /**
     * [org.greenrobot.eventbus.EventBus] 要求注册之前, 订阅者必须含有一个或以上声明 [org.greenrobot.eventbus.Subscribe]
     * 注解的方法, 否则会报错, 所以如果要想完成在基类中自动注册, 避免报错就要先检查是否符合注册资格
     *
     * @param subscriber 订阅者
     * @return 返回 `true` 则表示含有 [org.greenrobot.eventbus.Subscribe] 注解, `false` 为不含有
     */
    private fun haveAnnotation(subscriber: Any): Boolean {
        var skipSuperClasses = false
        var clazz: Class<*>? = subscriber.javaClass
        //查找类中符合注册要求的方法, 直到Object类
        while (clazz != null && !isSystemCalss(clazz.name) && !skipSuperClasses) {
            var allMethods: Array<Method>
            try {
                allMethods = clazz.declaredMethods
            } catch (th: Throwable) {
                try {
                    allMethods = clazz.methods
                } catch (th2: Throwable) {
                    continue
                } finally {
                    skipSuperClasses = true
                }
            }

            for (i in allMethods.indices) {
                val method = allMethods[i]
                val parameterTypes = method.parameterTypes
                //查看该方法是否含有 Subscribe 注解
                if (method.isAnnotationPresent(org.greenrobot.eventbus.Subscribe::class.java) && parameterTypes.size == 1) {
                    return true
                }
            } //end for
            //获取父类, 以继续查找父类中符合要求的方法
            clazz = clazz.superclass
        }
        return false
    }

    private fun isSystemCalss(name: String): Boolean {
        return name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")
    }

    companion object {
        @Volatile
        private var sInstance: EventBusManager? = null

        val instance: EventBusManager?
            get() {
                if (sInstance == null) {
                    synchronized(EventBusManager::class.java) {
                        if (sInstance == null) {
                            sInstance = EventBusManager()
                        }
                    }
                }
                return sInstance
            }
    }
}
