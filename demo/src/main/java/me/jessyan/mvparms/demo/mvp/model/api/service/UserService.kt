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
package me.jessyan.mvparms.demo.mvp.model.api.service

import io.reactivex.Observable
import me.jessyan.mvparms.demo.mvp.model.entity.User
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * ================================================
 * 展示 [Retrofit.create] 中需要传入的 ApiService 的使用方式
 * 存放关于用户的一些 API
 *
 *
 * Created by JessYan on 08/05/2016 12:05
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
interface UserService {

    @Headers(HEADER_API_VERSION)
    @GET("/users")
    fun getUsers(@Query("since") lastIdQueried: Int, @Query("per_page") perPage: Int): Observable<List<User>>

    companion object {
        val HEADER_API_VERSION = "Accept: application/vnd.github.v3+json"
    }
}
