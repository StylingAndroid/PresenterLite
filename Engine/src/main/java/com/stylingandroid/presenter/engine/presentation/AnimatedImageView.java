package com.stylingandroid.presenter.engine.presentation;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.AnimRes;
import android.support.annotation.AnimatorRes;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.stylingandroid.presenter.R;

import java.lang.ref.WeakReference;

/**
 * Created by markallison on 01/06/2013.
 */
public class AnimatedImageView extends AppCompatImageView implements Phaseable {
    private Phaser phaser = null;

    @AnimRes
    private int animId = 0;
    private Animation anim = null;

    @AnimatorRes
    private int animatorId = 0;
    private Animator animator = null;

    private boolean synced = false;

    @DrawableRes
    private int reverseId = 0;
    private Drawable otherDrawable = null;

    private Callback callback;

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
            reverseId = ta.getResourceId(R.styleable.AnimatedImageView_reverse, 0);
            int pauseId = ta.getResourceId(R.styleable.AnimatedImageView_pause, 0);
            int pause;
            if (pauseId > 0) {
                pause = context.getResources().getInteger(pauseId);
            } else {
                pause = ta.getInteger(R.styleable.AnimatedImageView_pause, 0);
            }

            if (reverseId > 0) {
                otherDrawable = context.getDrawable(reverseId);
                Handler handler = new Handler(context.getMainLooper());
                callback = new Callback(new WeakReference<>(this), handler, pause);
            }
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
        if (drawable instanceof Animatable2) {
            if (callback != null) {
                ((Animatable2) drawable).registerAnimationCallback(callback);
            }
            ((Animatable2) drawable).start();
        }
    }

    private void reverse() {
        Drawable current = getDrawable();
        if (current instanceof Animatable2) {
            ((Animatable2) current).unregisterAnimationCallback(callback);
        }
        if (otherDrawable instanceof Animatable2) {
            ((Animatable2) otherDrawable).registerAnimationCallback(callback);
            ((Animatable2) otherDrawable).start();
        }
        setImageDrawable(otherDrawable);
        otherDrawable = current;
    }

    @Override
    protected void onAnimationStart() {
        super.onAnimationStart();
    }

    private static final class Callback extends Animatable2.AnimationCallback {
        private final WeakReference<AnimatedImageView> weakReference;
        private final Handler handler;
        private final int pause;

        private Callback(WeakReference<AnimatedImageView> weakReference, Handler handler, int pause) {
            this.weakReference = weakReference;
            this.handler = handler;
            this.pause = pause;
        }

        @Override
        public void onAnimationEnd(Drawable drawable) {
            super.onAnimationEnd(drawable);
            handler.postDelayed(animationReverser, pause);
        }

        private Runnable animationReverser = new Runnable() {
            @Override
            public void run() {
                AnimatedImageView view = weakReference.get();
                if (view != null && view.isAttachedToWindow()) {
                    view.reverse();
                }
            }
        };
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Drawable drawable = getDrawable();
        if (callback != null && drawable instanceof Animatable2) {
            ((Animatable2) drawable).unregisterAnimationCallback(callback);
            ((Animatable2) drawable).stop();
        }
    }

}
