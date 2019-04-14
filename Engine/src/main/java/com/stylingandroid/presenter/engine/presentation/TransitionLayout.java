package com.stylingandroid.presenter.engine.presentation;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.transition.Scene;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.stylingandroid.presenter.R;

public class TransitionLayout extends FrameLayout implements Phaseable {

    private TransitionManager transitionManager = null;

    private Scene[] mScenes;

    private int currentSceneIndex = 0;

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
                String scenesStr = ta.getString(R.styleable.TransitionLayout_scenes);
                if (scenesStr != null) {
                    String[] layouts = scenesStr.split(",");
                    mScenes = new Scene[layouts.length];
                    for (int i = 0; i < layouts.length; i++) {
                        String layout = layouts[i].trim();
                        int id = res.getIdentifier(layout, "layout", context.getPackageName());
                        mScenes[i] = Scene.getSceneForLayout(this, id, context);
                    }
                    if (transitionManager != null) {
                        TransitionManager.go(mScenes[0], TransitionInflater.from(context).inflateTransition(R.transition.none));
                    }
                }
                ta.recycle();
            }
        }
    }

    @Override
    public boolean setPhase(int phase) {
        Scene currentScene = mScenes[currentSceneIndex];
        PhasedLayout phasedLayout = getPhasedLayout(currentScene);
        boolean changed = false;
        if (phasedLayout != null) {
            if (phasedLayout.hasMorePhases()) {
                phasedLayout.nextPhase();
                changed = true;
            }
        }
        if (!changed) {
            currentSceneIndex++;
            Scene newScene = mScenes[currentSceneIndex];
            transitionManager.transitionTo(newScene);
        }
        return !hasMorePhases(phase);
    }

    private PhasedLayout getPhasedLayout(Scene scene) {
        View childView = scene.getSceneRoot().getChildAt(0);
        if (childView instanceof PhaseContainer) {
            PhaseContainer container = (PhaseContainer)childView;
            return container.getPhasedLayout();
        }
        return null;
    }

    @Override
    public boolean hasMorePhases(int phase) {
        Scene currentScene = mScenes[currentSceneIndex];
        return currentSceneIndex < mScenes.length - 1 || getPhasedLayout(currentScene).hasMorePhases();
    }
}
