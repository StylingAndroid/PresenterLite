package com.stylingandroid.presenter.engine.presentation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class PhaseableView extends View implements Phaseable {
    private Phaser phaser = null;

    public PhaseableView(Context context) {
        this(context, null, -1);
    }

    public PhaseableView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public PhaseableView(Context context, AttributeSet attrs, int defStyle) {
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
    public int getLastPhase() {
        return phaser.getLastPhase();
    }
}
