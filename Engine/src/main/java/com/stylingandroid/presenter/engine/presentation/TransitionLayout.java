package com.stylingandroid.presenter.engine.presentation;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.transition.Scene;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.FrameLayout;

import com.stylingandroid.presenter.R;

public class TransitionLayout extends FrameLayout implements Phaseable {
    private TransitionManager transitionManager = null;

    private SparseArray<Scene> mScenes;

    private Integer maxPhase = null;

    public TransitionLayout(Context context) {
        this(context, null, -1);
    }

    public TransitionLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TransitionLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            Resources res = getResources();
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TransitionLayout);
            if (res != null && ta != null) {
                int transitionManagerId = ta.getResourceId(R.styleable.TransitionLayout_transitionManager, 0);
                if (transitionManagerId > 0) {
                    transitionManager = TransitionInflater.from(context).inflateTransitionManager(transitionManagerId, this);
                }
                int[] phases = null;
                Scene[] scenes = null;
                String scenesStr = ta.getString(R.styleable.TransitionLayout_scenes);
                if (scenesStr != null) {
                    String[] layouts = scenesStr.split(",");
                    scenes = new Scene[layouts.length];
                    for (int i = 0; i < layouts.length; i++) {
                        String layout = layouts[i].trim();
                        int id = res.getIdentifier(layout, "layout", context.getPackageName());
                        scenes[i] = Scene.getSceneForLayout(this, id, context);
                    }
                }
                String phaseStr = ta.getString(R.styleable.TransitionLayout_transitionPhases);
                if (phaseStr != null) {
                    String[] phasesStr = phaseStr.split(",");
                    phases = new int[phasesStr.length];
                    for (int i = 0; i < phasesStr.length; i++) {
                        String phase = phasesStr[i].trim();
                        phases[i] = Integer.parseInt(phase);
                    }
                }
                if (scenes != null && phases != null && scenes.length == phases.length + 1) {
                    mScenes = new SparseArray<>(scenes.length);
                    for (int i = 0; i < phases.length; i++) {
                        mScenes.put(phases[i], scenes[i + 1]);
                    }

                } else {
                    throw new RuntimeException("Either scenes or phases aren't defined, or the counts do not match");
                }
                ta.recycle();
                if (transitionManager != null) {
                    TransitionManager.go(scenes[0], TransitionInflater.from(context).inflateTransition(R.transition.none));
                }
            }
        }
    }

    @Override
    public boolean setPhase(int phase) {
        boolean changed = false;
        for (int i = 0; i < mScenes.size(); i++) {
            int p = mScenes.keyAt(i);
            if (phase == p) {
                transitionManager.transitionTo(mScenes.get(p));
                //TransitionManager.go(mScenes.get(p));
                changed = true;
            }
        }
        return changed && phase >= getLastPhase();
    }

    @Override
    public int getLastPhase() {
        if (maxPhase == null) {
            for (int i = 0; i < mScenes.size(); i++) {
                int phase = mScenes.keyAt(i);
                maxPhase = maxPhase == null ? phase : Math.max(maxPhase, phase);
            }
        }
        return maxPhase;
    }
}
