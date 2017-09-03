package com.example.woong.instamaterialtest.ui.adapter

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.woong.instamaterialtest.R;
import com.example.woong.instamaterialtest.Utils;

import kotlinx.android.synthetic.main.item_feed.view.*

/**
 * Created by woong on 2017. 8. 30..
 */
class FeedItemAnimator: DefaultItemAnimator() {
    private val DECCELERATE_INTERPOLATOR = DecelerateInterpolator()
    private val ACCELERATE_INTERPOLATOR = AccelerateInterpolator()
    private val OVERSHOOT_INTERPOLATOR = OvershootInterpolator(4f)

    var likeAnimationsMap: MutableMap<RecyclerView.ViewHolder, AnimatorSet> = mutableMapOf<RecyclerView.ViewHolder, AnimatorSet>()
    var heartAnimationsMap: MutableMap<RecyclerView.ViewHolder, AnimatorSet> = mutableMapOf<RecyclerView.ViewHolder, AnimatorSet>()

    private var lastAddAnimatedItem: Int = -2

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun recordPreLayoutInformation(state: RecyclerView.State, viewHolder: RecyclerView.ViewHolder, changeFlags: Int, payloads: MutableList<Any>): ItemHolderInfo {
        if (changeFlags == FLAG_CHANGED) {
            for (payload in payloads) {
                if (payload is String) {
                    return FeedItemHolderInfo(payload.toString())
                }
            }
        }
        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads)
    }

    override fun animateAdd(viewHolder: RecyclerView.ViewHolder): Boolean {
        if (viewHolder.itemViewType == FeedAdapter.VIEW_TYPE_DEFAULT) {
            if (viewHolder.layoutPosition > lastAddAnimatedItem) {
                lastAddAnimatedItem++
                runEnterAnimation(viewHolder as (FeedAdapter.CellFeedViewHolder))
                return false
            }
        }
        dispatchAddFinished(viewHolder)
        return false
    }

    private fun runEnterAnimation(holder: FeedAdapter.CellFeedViewHolder) {
        val screenHeight = Utils.getScreenHeight(holder.itemView.context)
        holder.itemView.setTranslationY(screenHeight.toFloat())
        holder.itemView.animate()
                .translationY(0f)
                .setInterpolator(DecelerateInterpolator(3f))
                .setDuration(700)
                .setListener(object: AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        dispatchAddFinished(holder)
                    }
                })
                .start()
    }

    override fun animateChange(oldHolder: RecyclerView.ViewHolder,
                               newHolder: RecyclerView.ViewHolder,
                               preInfo: ItemHolderInfo,
                               postInfo: ItemHolderInfo): Boolean {
        cancelCurrentAnimationIfExists (newHolder)

        if (preInfo is FeedItemHolderInfo) {
            var feedItemHolderInfo: FeedItemHolderInfo = preInfo
            var holder: FeedAdapter.CellFeedViewHolder = newHolder as FeedAdapter.CellFeedViewHolder

            animateHeartButton(holder)
            updateLikesCounter(holder, holder.feedItem.likesCount)
            if (FeedAdapter.ACTION_LIKE_IMAGE_CLICKED.equals(feedItemHolderInfo.updateAction)) {
                animatePhotoLike(holder)
            }
        }
        return false
    }

    private fun cancelCurrentAnimationIfExists(item: RecyclerView.ViewHolder) {
        if (likeAnimationsMap.containsKey(item)) {
            likeAnimationsMap.get(item)?.cancel()
        }
        if (heartAnimationsMap.containsKey(item)) {
            heartAnimationsMap.get(item)?.cancel()
        }
    }

    private fun animateHeartButton(holder: FeedAdapter.CellFeedViewHolder) {
        var animatorSet: AnimatorSet = AnimatorSet()

        var rotationAnim: ObjectAnimator = ObjectAnimator.ofFloat(holder.itemView.btnLike, "rotation", 0f, 360f)
        rotationAnim.setDuration(300)
        rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR)

        var bounceAnimX: ObjectAnimator = ObjectAnimator.ofFloat(holder.itemView.btnLike, "scaleX", 0.2f, 1f)
        bounceAnimX.setDuration(300)
        bounceAnimX.setInterpolator (OVERSHOOT_INTERPOLATOR)

        var bounceAnimY: ObjectAnimator = ObjectAnimator.ofFloat(holder.itemView.btnLike, "scaleY", 0.2f, 1f)
        bounceAnimY.setDuration(300)
        bounceAnimY.setInterpolator (OVERSHOOT_INTERPOLATOR)
        bounceAnimY.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                holder.itemView.btnLike.setImageResource(R.drawable.ic_heart_red)
            }

            override fun onAnimationEnd(animation: Animator?) {
                heartAnimationsMap.remove(holder)
                dispatchChangeFinishedIfAllAnimationsEnded(holder)
            }
        })

        animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim)
        animatorSet.start()

        heartAnimationsMap.put(holder, animatorSet)

    }

    private fun updateLikesCounter(holder: FeedAdapter.CellFeedViewHolder, toValue: Int) {
        var likesCountTextFrom:String = holder.itemView.tsLikesCounter.getResources().getQuantityString(
                R.plurals.likes_count, toValue, toValue
        )
        holder.itemView.tsLikesCounter.setCurrentText(likesCountTextFrom)

        var likesCountTextTo:String = holder.itemView.tsLikesCounter.getResources().getQuantityString(
                R.plurals.likes_count, toValue, toValue
        )
        holder.itemView.tsLikesCounter.setCurrentText(likesCountTextTo)
    }

    private fun animatePhotoLike(holder: FeedAdapter.CellFeedViewHolder) {
        holder.itemView.vBgLike.setVisibility(View.VISIBLE)
        holder.itemView.ivLike.setVisibility(View.VISIBLE)

        holder.itemView.vBgLike.setScaleY(0.1f)
        holder.itemView.vBgLike.setScaleX(0.1f)
        holder.itemView.vBgLike.setAlpha(1f)
        holder.itemView.ivLike.setScaleY(0.1f)
        holder.itemView.ivLike.setScaleX(0.1f)

        var animatorSet: AnimatorSet = AnimatorSet()

        var bgScaleYAnim: ObjectAnimator = ObjectAnimator.ofFloat(holder.itemView.vBgLike, "scaleY", 0.1f, 1f)
        bgScaleYAnim.setDuration(200)
        bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR)
        var bgScaleXAnim: ObjectAnimator = ObjectAnimator.ofFloat(holder.itemView.vBgLike, "scaleX", 0.1f, 1f)
        bgScaleXAnim.setDuration(200)
        bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR)
        var bgAlphaAnim: ObjectAnimator = ObjectAnimator.ofFloat(holder.itemView.vBgLike, "alpha", 1f, 0f)
        bgAlphaAnim.setDuration(200)
        bgAlphaAnim.setStartDelay(150)
        bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR)

        var imgScaleUpYAnim: ObjectAnimator = ObjectAnimator.ofFloat(holder.itemView.ivLike, "scaleY", 0.1f, 1f)
        imgScaleUpYAnim.setDuration(300)
        imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR)
        var imgScaleUpXAnim: ObjectAnimator = ObjectAnimator.ofFloat(holder.itemView.ivLike, "scaleX", 0.1f, 1f)
        imgScaleUpXAnim.setDuration(300)
        imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR)

        var imgScaleDownYAnim: ObjectAnimator = ObjectAnimator.ofFloat(holder.itemView.ivLike, "scaleY", 0.1f, 0f)
        imgScaleDownYAnim.setDuration(300)
        imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR)
        var imgScaleDownXAnim: ObjectAnimator = ObjectAnimator.ofFloat(holder.itemView.ivLike, "scaleX", 0.1f, 0f)
        imgScaleDownXAnim.setDuration(300)
        imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR)

        animatorSet.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim, imgScaleUpYAnim, imgScaleUpXAnim)
        animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim)

        animatorSet.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                likeAnimationsMap.remove(holder)
                resetLikeAnimationState(holder)
                dispatchChangeFinishedIfAllAnimationsEnded(holder)
            }
        })
        animatorSet.start()

        likeAnimationsMap.put(holder, animatorSet)
    }

    private fun dispatchChangeFinishedIfAllAnimationsEnded(holder: FeedAdapter.CellFeedViewHolder) {
        if (likeAnimationsMap.containsKey(holder) || heartAnimationsMap.containsKey(holder)) {
            return
        }
        dispatchAnimationFinished(holder)
    }

    private fun resetLikeAnimationState(holder: FeedAdapter.CellFeedViewHolder) {
        holder.itemView.vBgLike.setVisibility(View.INVISIBLE)
        holder.itemView.ivLike.setVisibility(View.INVISIBLE)
    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {
        super.endAnimation(item)
        cancelCurrentAnimationIfExists(item)
    }

    override fun endAnimations() {
        super.endAnimations()
        for (animatorSet in likeAnimationsMap.values) {
            animatorSet.cancel()
        }
    }

    class FeedItemHolderInfo: ItemHolderInfo {
        var updateAction: String = String()
        constructor (updateAction: String) {
            this.updateAction = updateAction
        }
    }
}