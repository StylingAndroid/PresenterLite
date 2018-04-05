package com.stylingandroid.presenter.engine.presentation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.stylingandroid.presenter.R;

public class SlideLayout extends LinearLayout implements PhaseContainer {
    private PhasedLayout phasedLayout;

    private static class LayoutParams extends android.widget.LinearLayout.LayoutParams {
        private Animation animation = null;

        LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);

            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlideLayout_Layout);
            int anim = ta.getResourceId(R.styleable.SlideLayout_Layout_layout_animation, -1);
            if (anim >= 0) {
                animation = AnimationUtils.loadAnimation(context, anim);
            }
            ta.recycle();
        }

        public Animation getAnimation() {
            return animation;
        }
    }

    public SlideLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            setSystemUiVisibility(SYSTEM_UI_FLAG_HIDE_NAVIGATION | SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlideLayout);
            int inAnimation = ta.getResourceId(R.styleable.SlideLayout_nextInAnimation, 0);
            int outAnimation = ta.getResourceId(R.styleable.SlideLayout_nextOutAnimation, 0);
            int tweetRes = ta.getResourceId(R.styleable.SlideLayout_tweet, 0);
            String tweet;
            if (tweetRes > 0) {
                tweet = context.getResources().getString(tweetRes);
            } else {
                tweet = ta.getString(R.styleable.SlideLayout_tweet);
            }
            int notesRes = ta.getResourceId(R.styleable.SlideLayout_notes, 0);
            String notes;
            if (notesRes > 0) {
                notes = context.getResources().getString(notesRes);
            } else {
                notes = ta.getString(R.styleable.SlideLayout_notes);
            }
            boolean autoStart = ta.getBoolean(R.styleable.SlideLayout_autoStart, false);
            ta.recycle();
            phasedLayout = new PhasedLayout(context, inAnimation, outAnimation, tweet, notes, autoStart);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (phasedLayout != null) {
            phasedLayout.onFinishInflate(this, false);
        }
    }

    @Override
    public android.widget.LinearLayout.LayoutParams generateLayoutParams(
            AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    public PhasedLayout getPhasedLayout() {
        return phasedLayout;
    }
}
