package com.stylingandroid.presenter.engine.presentation;

public interface Phaseable {
    boolean setPhase(final int phase);

    boolean hasMorePhases(int phase);
}
