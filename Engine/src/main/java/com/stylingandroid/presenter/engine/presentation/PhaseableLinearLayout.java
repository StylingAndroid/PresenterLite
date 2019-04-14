package com.stylingandroid.presenter.engine.presentation;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class PhaseableLinearLayout extends LinearLayout implements Phaseable {
    private Phaser phaser = null;

    public PhaseableLinearLayout(Context context) {
        this(context, null, -1);
    }

    public PhaseableLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public PhaseableLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            phaser = new Phaser(context, attrs);
            phaser.setInitialVisibility(this);
        }
    }

    @Override
    public boolean setPhase(int phase) {
        return phaser.setPhase(phase, this, true);
    }

    @Override
    public boolean hasMorePhases(int phase) {
        return phaser.getLastPhase() > phase;
    }

}
