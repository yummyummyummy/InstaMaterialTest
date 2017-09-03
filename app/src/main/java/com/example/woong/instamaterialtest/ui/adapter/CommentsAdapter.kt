package com.example.woong.instamaterialtest.ui.adapter

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.BindView;
import com.example.woong.instamaterialtest.R
import com.example.woong.instamaterialtest.ui.utils.RoundedTransformation

import kotlinx.android.synthetic.main.item_comment.*
import kotlinx.android.synthetic.main.item_comment.view.*
/**
 * Created by woong on 2017. 8. 30..
 */
class CommentsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private var context: Context? = null
    private var itemsCount = 0
    private var lastAnimatedPosition = -1
    private var avatarSize: Int = 0

    private var animationsLocked = false
    private var delayEnterAnimation = true

    constructor (context: Context) {
        this.context = context
        avatarSize = context.resources.getDimensionPixelSize(R.dimen.comment_avatar_size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        runEnterAnimation(viewHolder.itemView, position)
        val holder = viewHolder as CommentViewHolder
        when (position % 3) {
            0 -> holder.itemView.tvComment!!.text = "Lorem ipsum dolor sit amet, consectetur adipisicing elit."
            1 -> holder.itemView.tvComment!!.text = "Cupcake ipsum dolor sit amet bear claw."
            2 -> holder.itemView.tvComment!!.text = "Cupcake ipsum dolor sit. Amet gingerbread cupcake. Gummies ice cream dessert icing marzipan apple pie dessert sugar plum."
        }

        Picasso.with(context)
                .load(R.drawable.ic_launcher)
                .centerCrop()
                .resize(avatarSize, avatarSize)
                .transform(RoundedTransformation())
                .into(holder.itemView.ivUserAvatar)
    }

    private fun runEnterAnimation(view: View, position: Int) {
        if (animationsLocked) return

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position
            view.translationY = 100f
            view.alpha = 0f
            view.animate()
                    .translationY(0f).alpha(1f)
                    .setStartDelay((if (delayEnterAnimation) 20 * position else 0).toLong())
                    .setInterpolator(DecelerateInterpolator(2f))
                    .setDuration(300)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            animationsLocked = true
                        }
                    })
                    .start()
        }
    }

    override fun getItemCount(): Int {
        return itemsCount
    }

    fun updateItems() {
        itemsCount = 10
        notifyDataSetChanged()
    }

    fun addItem() {
        itemsCount++
        notifyItemInserted(itemsCount - 1)
    }

    fun setAnimationsLocked(animationsLocked: Boolean) {
        this.animationsLocked = animationsLocked
    }

    fun setDelayEnterAnimation(delayEnterAnimation: Boolean) {
        this.delayEnterAnimation = delayEnterAnimation
    }


    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            ButterKnife.bind(this, view)
        }
    }

}