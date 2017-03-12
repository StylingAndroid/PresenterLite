package com.stylingandroid.presenter.engine.presentation;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.stylingandroid.presenter.R;

/**
 * Created by markallison on 01/06/2013.
 */
public class AnimatedImageView extends ImageView implements Phaseable {
    private Phaser phaser = null;

    private int animId = 0;
    private Animation anim = null;
    private int animatorId = 0;
    private Animator animator = null;
    private boolean synced = false;

    public AnimatedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public AnimatedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (!isInEditMode()) {
            TypedArray ta = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.AnimatedImageView
            );

            animId = ta.getResourceId(R.styleable.AnimatedImageView_anim, 0);
            animatorId = ta.getResourceId(R.styleable.AnimatedImageView_animator, 0);
            synced = ta.getBoolean(R.styleable.AnimatedImageView_synchronised, false);

            ta.recycle();
            phaser = new Phaser(context, attrs);
            phaser.setInitialVisibility(this);
            if (synced) {
                beginAnimation();
            }
        }
    }

    public boolean setPhase(final int phase) {
        boolean done = true;
        if (!isInEditMode()) {
            int visibility = getVisibility();
            if (!synced && phaser.willHide(phase)) {
                if (anim != null) {
                    anim.cancel();
                    anim = null;
                }
                if (animator != null) {
                    animator.cancel();
                    animator = null;
                }
            }
            done = phaser.setPhase(phase, this, true);
            int newVisibility = getVisibility();
            if (!synced && visibility != newVisibility && newVisibility == VISIBLE) {
                beginAnimation();
            }
        }
        return done;
    }

    public int getLastPhase() {
        return isInEditMode() ? 0 : phaser.getLastPhase();
    }

    private void beginAnimation() {
        if (animId > 0) {
            anim = AnimationUtils.loadAnimation(getContext(), animId);
            startAnimation(anim);
        }
        if (animatorId > 0) {
            animator = AnimatorInflater.loadAnimator(getContext(), animatorId);
            animator.setTarget(this);
            animator.start();
        }
        Drawable drawable = getDrawable();
        if (drawable instanceof AnimatedVectorDrawable) {
            ((AnimatedVectorDrawable) drawable).start();
        }
    }
}
