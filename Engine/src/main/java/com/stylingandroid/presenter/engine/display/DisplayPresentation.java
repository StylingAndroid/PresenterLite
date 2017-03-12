package com.stylingandroid.presenter.engine.display;

import android.app.Presentation;
import android.os.Bundle;
import android.view.Display;
import android.view.View;

import com.stylingandroid.presenter.R;
import com.stylingandroid.presenter.engine.presentation.DisplayLayout;

public class DisplayPresentation extends Presentation {
    public static final int DURATION = 5000;
    private DisplayLayout display;
    private StandaloneDisplayActivity activity;

    public DisplayPresentation(StandaloneDisplayActivity outerContext,
                               Display display) {
        super(outerContext, display);
        activity = outerContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity.getLayout());
        display = (DisplayLayout) findViewById(R.id.display);
        display.go(0, false);
        View view = findViewById(R.id.display_attributes);
        DisplayInfoHelper.populate(getContext(), view,
                getDisplay());
        DisplayInfoHelper.show(view, DURATION);
    }

    public void advance() {
        display.advance();
    }

    public void next() {
        display.next();
    }

    public void previous() {
        display.previous();
    }

    public void setVisibility(int visibility) {
        display.setVisibility(visibility);
    }

    public void go(int currentSlidePos, int currentSlidePhase) {
        display.go(currentSlidePos, currentSlidePhase);
    }

}
