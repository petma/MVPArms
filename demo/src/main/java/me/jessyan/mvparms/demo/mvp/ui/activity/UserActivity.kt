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
package me.jessyan.mvparms.demo.mvp.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.RecyclerView

import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.paginate.Paginate
import com.tbruyelle.rxpermissions2.RxPermissions

import javax.inject.Inject

import butterknife.BindView
import me.jessyan.mvparms.demo.R
import me.jessyan.mvparms.demo.di.component.DaggerUserComponent
import me.jessyan.mvparms.demo.mvp.contract.UserContract
import me.jessyan.mvparms.demo.mvp.presenter.UserPresenter
import timber.log.Timber

import com.jess.arms.utils.Preconditions.checkNotNull


/**
 * ================================================
 * 展示 View 的用法
 *
 * @see [View wiki 官方文档](https://github.com/JessYanCoding/MVPArms/wiki.2.4.2)
 * Created by JessYan on 09/04/2016 10:59
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
class UserActivity : BaseActivity<UserPresenter>(), UserContract.View, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    internal var mRecyclerView: RecyclerView? = null
    @BindView(R.id.swipeRefreshLayout)
    internal var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    @Inject
    override var rxPermissions: RxPermissions? = null
        internal set
    @Inject
    internal var mLayoutManager: RecyclerView.LayoutManager? = null
    @Inject
    internal var mAdapter: RecyclerView.Adapter<*>? = null

    private var mPaginate: Paginate? = null
    private var isLoadingMore: Boolean = false

    override val activity: Activity
        get() = this

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerUserComponent
                .builder()
                .appComponent(appComponent)
                .view(this)
                .build()
                .inject(this)
    }

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_user
    }

    override fun initData(savedInstanceState: Bundle?) {
        initRecyclerView()
        mRecyclerView!!.adapter = mAdapter
        initPaginate()
    }


    override fun onRefresh() {
        mPresenter!!.requestUsers(true)
    }

    /**
     * 初始化RecyclerView
     */
    private fun initRecyclerView() {
        mSwipeRefreshLayout!!.setOnRefreshListener(this)
        ArmsUtils.configRecyclerView(mRecyclerView!!, mLayoutManager)
    }


    override fun showLoading() {
        Timber.tag(TAG).w("showLoading")
        mSwipeRefreshLayout!!.isRefreshing = true
    }

    override fun hideLoading() {
        Timber.tag(TAG).w("hideLoading")
        mSwipeRefreshLayout!!.isRefreshing = false
    }

    override fun showMessage(message: String) {
        checkNotNull(message)
        ArmsUtils.snackbarText(message)
    }

    override fun launchActivity(intent: Intent) {
        checkNotNull(intent)
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
        finish()
    }

    /**
     * 开始加载更多
     */
    override fun startLoadMore() {
        isLoadingMore = true
    }

    /**
     * 结束加载更多
     */
    override fun endLoadMore() {
        isLoadingMore = false
    }

    /**
     * 初始化Paginate,用于加载更多
     */
    private fun initPaginate() {
        if (mPaginate == null) {
            val callbacks = object : Paginate.Callbacks {
                override fun onLoadMore() {
                    mPresenter!!.requestUsers(false)
                }

                override fun isLoading(): Boolean {
                    return isLoadingMore
                }

                override fun hasLoadedAllItems(): Boolean {
                    return false
                }
            }

            mPaginate = Paginate.with(mRecyclerView, callbacks)
                    .setLoadingTriggerThreshold(0)
                    .build()
            mPaginate!!.setHasMoreDataToLoad(false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.rxPermissions = null
        this.mPaginate = null
    }
}
