package com.stylingandroid.presenter.engine.presentation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.stylingandroid.presenter.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by markallison on 01/06/2013.
 */
public class Phaser {
    private List<Integer> showPhase = new ArrayList<>();
    private List<Integer> hidePhase = new ArrayList<>();
    private List<Integer> gonePhase = new ArrayList<>();

    private Animation showAnimation = null;
    private Animation hideAnimation = null;

    public Phaser(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.Phaser);
        populate(ta.getString(R.styleable.Phaser_showPhase),
                showPhase);
        populate(ta.getString(R.styleable.Phaser_hidePhase),
                hidePhase);
        populate(ta.getString(R.styleable.Phaser_gonePhase),
                gonePhase);

        int a = ta.getResourceId(
                R.styleable.Phaser_showAnimation, 0);
        if (a > 0) {
            showAnimation = AnimationUtils.loadAnimation(context, a);
        }
        a = ta.getResourceId(R.styleable.Phaser_hideAnimation, 0);
        if (a > 0) {
            hideAnimation = AnimationUtils.loadAnimation(context, a);
        }
        ta.recycle();
    }

    private static void populate(String str, List<Integer> list) {
        list.clear();
        if (!TextUtils.isEmpty(str)) {
            String[] tokens = str.split("\\s?,\\s?");
            for (String token : tokens) {
                list.add(Integer.parseInt(token));
            }
        }
        Collections.sort(list);
    }

    public boolean willHide(int phase) {
        return hidePhase.contains(phase) || gonePhase.contains(phase);
    }

    public boolean setPhase(final int phase, final View view, boolean animate) {
        if (showPhase.contains(phase)) {
            view.setVisibility(View.VISIBLE);
            if (animate && showAnimation != null) {
                view.startAnimation(showAnimation);
            }
            if (view instanceof Animatable) {
                ((Animatable) view).start();
            }
        } else {
            if (hidePhase.contains(phase) || gonePhase.contains(phase)) {
                final int visibility = gonePhase.contains(phase) ? View.GONE
                        : View.INVISIBLE;
                if (animate && hideAnimation != null) {
                    hideAnimation.setAnimationListener(new Animation.AnimationListener() {

                        public void onAnimationStart(Animation animation) {
                        }

                        public void onAnimationRepeat(Animation animation) {
                        }

                        public void onAnimationEnd(Animation animation) {
                            view.setVisibility(visibility);
                        }
                    });
                    view.startAnimation(hideAnimation);
                } else {
                    view.setVisibility(visibility);
                }
            }
        }

        return getLast(showPhase) <= phase && getLast(hidePhase) <= phase
                && getLast(gonePhase) <= phase;
    }

    private static int getLast(List<Integer> list) {
        return list.isEmpty() ? 0 : list.get(list.size() - 1);
    }

    private static int getFirst(List<Integer> list) {
        return list.isEmpty() ? 0 : list.get(0);
    }

    public int getLastPhase() {
        return Math.max(getLast(showPhase), Math.max(getLast(hidePhase), getLast(gonePhase)));
    }

    public void setInitialVisibility(View view) {
        if (getFirst(showPhase) != 0 && view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.INVISIBLE);
        }

    }
}
