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
package me.jessyan.mvparms.demo.mvp.model.entity

/**
 * ================================================
 * User 实体类
 *
 *
 * Created by JessYan on 04/09/2016 17:14
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
class User(val id: Int, val login: String, private val avatar_url: String) {

    val avatarUrl: String
        get() = if (avatar_url.isEmpty()) avatar_url else avatar_url.split("\\?".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0]

    override fun toString(): String {
        return "id -> $id login -> $login"
    }
}
