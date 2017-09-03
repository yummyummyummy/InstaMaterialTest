package com.example.woong.instamaterialtest.ui.activity

/**
 * Created by woong on 2017. 8. 29..
 */

import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.BindDimen;
import butterknife.BindString;

import com.example.woong.instamaterialtest.R
import com.example.woong.instamaterialtest.ui.utils.CircleTransformation

import kotlinx.android.synthetic.main.activity_drawer.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_global_menu_header.view.*


open class BaseDrawerActivity: BaseActivity() {
    protected lateinit var ivMenuUserProfilePhoto: ImageView

    override fun setContentView(layoutResID: Int) {
        super.setContentViewWithoutInject(R.layout.activity_drawer)
        val viewGroup: ViewGroup = flContentRoot
        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true)
        setupToolbar()
        setupHeader()
    }

    override fun setupToolbar() {
        super.setupToolbar()
        if (getToolbar() != null) {
            getToolbar()!!.setNavigationOnClickListener {
                drawerLayout.openDrawer(Gravity.LEFT)
            }
        }
    }

    fun setupHeader() {
        var headerView: View = vNavigation.getHeaderView(0)
        ivMenuUserProfilePhoto = headerView.ivMenuUserProfilePhoto as ImageView
        headerView.vGlobalMenuHeader.setOnClickListener {
            onGlobalMenuHeaderClick(headerView)
        }
        Picasso.with(this)
                .load(R.string.user_profile_photo)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(R.dimen.global_menu_avatar_size,R.dimen.global_menu_avatar_size)
                .centerCrop()
                .transform(CircleTransformation())
                .into(ivMenuUserProfilePhoto)
    }

    fun onGlobalMenuHeaderClick(v: View) {
        drawerLayout.closeDrawer(Gravity.LEFT)
        Handler().postDelayed(Runnable() {
            var startingLocation: IntArray = intArrayOf(0,0)
            v.getLocationOnScreen(startingLocation)
            startingLocation[0] += v.getWidth() / 2
            //UserProfileActivity.startUserProfileFromLocation(startingLocation, BaseDrawerActivity.this)
        }, 200)
    }
}