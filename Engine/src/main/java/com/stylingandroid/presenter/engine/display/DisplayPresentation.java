package com.stylingandroid.presenter.engine.display;

import android.app.Presentation;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;

import com.stylingandroid.presenter.R;
import com.stylingandroid.presenter.engine.presentation.DisplayLayout;

public class DisplayPresentation extends Presentation {
    public static final int DURATION = 5000;
    private DisplayLayout display;
    private StandaloneDisplayActivity activity;
    private Context outerContext;

    public DisplayPresentation(StandaloneDisplayActivity outerContext,
                               Display display) {
        super(outerContext, display);
        DisplayMetrics dm1 = outerContext.getResources().getDisplayMetrics();
        DisplayMetrics dm2 = new DisplayMetrics();
        display.getMetrics(dm2);
        activity = outerContext;
        this.outerContext = outerContext;
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

    @Override
    protected void onStart() {
        Log.v("", "Display Changed");
        DisplayMetrics dm = getResources().getDisplayMetrics();
        super.onStart();
    }

    @Override
    public void onDisplayChanged() {
        super.onDisplayChanged();

        Log.v("", "Display Changed");
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

    @Override
    public Resources getResources() {
        return outerContext.getResources();
    }
}
