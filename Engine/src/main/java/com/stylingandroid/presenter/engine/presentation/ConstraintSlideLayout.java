package com.stylingandroid.presenter.engine.presentation;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.stylingandroid.presenter.R;

public class ConstraintSlideLayout extends ConstraintLayout implements PhaseContainer {
    private PhasedLayout phasedLayout;

    public ConstraintSlideLayout(Context context) {
        super(context, null, 0);
    }

    public ConstraintSlideLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConstraintSlideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            setSystemUiVisibility(SYSTEM_UI_FLAG_HIDE_NAVIGATION | SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ConstraintSlideLayout);
            int inAnimation = ta.getResourceId(R.styleable.ConstraintSlideLayout_nextInAnimation, 0);
            int outAnimation = ta.getResourceId(R.styleable.ConstraintSlideLayout_nextOutAnimation, 0);
            int tweetRes = ta.getResourceId(R.styleable.ConstraintSlideLayout_tweet, 0);
            String tweet;
            if (tweetRes > 0) {
                tweet = context.getResources().getString(tweetRes);
            } else {
                tweet = ta.getString(R.styleable.ConstraintSlideLayout_tweet);
            }
            int notesRes = ta.getResourceId(R.styleable.ConstraintSlideLayout_notes, 0);
            String notes;
            if (notesRes > 0) {
                notes = context.getResources().getString(notesRes);
            } else {
                notes = ta.getString(R.styleable.ConstraintSlideLayout_notes);
            }
            boolean autoStart = ta.getBoolean(R.styleable.ConstraintSlideLayout_autoStart, false);
            ta.recycle();
            phasedLayout = new PhasedLayout(context, inAnimation, outAnimation, tweet, notes, autoStart);
        }
    }

    public static class LayoutParams extends ConstraintLayout.LayoutParams {
        private Animation animation = null;

        LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            TypedArray ta = context.obtainStyledAttributes(attributeSet, R.styleable.SlideLayout_Layout);
            int anim = ta.getResourceId(R.styleable.ConstraintSlideLayout_Layout_layout_animation, -1);
            if (anim >= 0) {
                animation = AnimationUtils.loadAnimation(context, anim);
            }
            ta.recycle();
        }

        public Animation getAnimation() {
            return animation;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (phasedLayout != null) {
            phasedLayout.onFinishInflate(this);
        }
    }

    @Override
    public ConstraintLayout.LayoutParams generateLayoutParams(
            AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    public PhasedLayout getPhasedLayout() {
        return phasedLayout;
    }
}
