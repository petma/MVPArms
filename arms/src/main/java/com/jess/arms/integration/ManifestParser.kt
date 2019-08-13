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
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

import java.util.ArrayList

/**
 * ================================================
 * 用于解析 AndroidManifest 中的 Meta 属性
 * 配合 [ConfigModule] 使用
 *
 *
 * Created by JessYan on 12/04/2017 14:41
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
class ManifestParser(private val context: Context) {

    fun parse(): List<ConfigModule> {
        val modules = ArrayList<ConfigModule>()
        try {
            val appInfo = context.packageManager.getApplicationInfo(
                    context.packageName, PackageManager.GET_META_DATA)
            if (appInfo.metaData != null) {
                for (key in appInfo.metaData.keySet()) {
                    if (MODULE_VALUE == appInfo.metaData.get(key)) {
                        modules.add(parseModule(key))
                    }
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException("Unable to find metadata to parse ConfigModule", e)
        }

        return modules
    }

    companion object {
        private val MODULE_VALUE = "ConfigModule"

        private fun parseModule(className: String): ConfigModule {
            val clazz: Class<*>
            try {
                clazz = Class.forName(className)
            } catch (e: ClassNotFoundException) {
                throw IllegalArgumentException("Unable to find ConfigModule implementation", e)
            }

            val module: Any
            try {
                module = clazz.newInstance()
            } catch (e: InstantiationException) {
                throw RuntimeException("Unable to instantiate ConfigModule implementation for $clazz", e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Unable to instantiate ConfigModule implementation for $clazz", e)
            }

            if (module !is ConfigModule) {
                throw RuntimeException("Expected instanceof ConfigModule, but found: $module")
            }
            return module
        }
    }
}