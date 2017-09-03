package com.example.woong.instamaterialtest.ui.activity

import android.support.annotation.Nullable
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView

import butterknife.ButterKnife
import butterknife.BindView;

import com.example.woong.instamaterialtest.R

import kotlinx.android.synthetic.main.view_feed_toolbar.*
import com.example.woong.instamaterialtest.R.id.ivLogo
import com.example.woong.instamaterialtest.R.id.toolbar


/**
 * Created by woong on 2017. 8. 29..
 */
open class BaseActivity: AppCompatActivity() {

    //var toolbar: Toolbar = toolbar
    //var ivLogo: ImageView? = ivLogo


    private var inboxMenuItem: MenuItem? = null

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        bindViews()
    }

    fun bindViews() {
        //ButterKnife.bind(this)
        setupToolbar()
    }

    fun setContentViewWithoutInject(layoutResID: Int) {
        super.setContentView(layoutResID)
    }

    open fun setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            toolbar!!.setNavigationIcon(R.drawable.ic_menu_white)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        inboxMenuItem = menu!!.findItem(R.id.action_inbox)
        inboxMenuItem?.setActionView(R.layout.menu_item_view)
        return true
    }


    fun getToolbar(): Toolbar {
        return toolbar
    }

    fun getInboxMenuItem(): MenuItem? {
        return inboxMenuItem
    }

    fun getIvLogo(): ImageView {
        return ivLogo
    }

}