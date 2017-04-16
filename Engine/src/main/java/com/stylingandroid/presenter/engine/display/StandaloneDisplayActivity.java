package com.stylingandroid.presenter.engine.display;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.media.MediaRouter;
import android.media.MediaRouter.RouteInfo;
import android.media.MediaRouter.SimpleCallback;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.stylingandroid.presenter.R;
import com.stylingandroid.presenter.engine.presentation.DisplayLayout;

public class StandaloneDisplayActivity extends FragmentActivity {
    public static final String TAG = "Presenter";

    public static final String EXTRA_MIRROR_DISPLAY = "EXTRA_MIRROR_DISPLAY";
    public static final String EXTRA_LAYOUT = "EXTRA_LAYOUT";
    public static final int DURATION = 5000;

    private DisplayLayout displayLayout = null;
    private DisplayPresentation displayPresentation = null;

    private MediaRouter mediaRouter = null;

    private SimpleCallback callback = new SimpleCallback() {
        public void onRoutePresentationDisplayChanged(MediaRouter router, RouteInfo info) {
            updatePresentation();
        }
    };

    protected int getLayout() {
        int layout = R.layout.display;

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_LAYOUT)) {
            layout = intent.getIntExtra(EXTRA_LAYOUT, R.layout.display);
        }
        return layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "StandaloneDisplayActivity.onCreate()");

        super.onCreate(savedInstanceState);

        int layout = getLayout();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(layout);
        displayLayout = (DisplayLayout) findViewById(R.id.display);
        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.hide();
        }
        if (!getIntent().getBooleanExtra(EXTRA_MIRROR_DISPLAY, false)) {
            mediaRouter = (MediaRouter) getSystemService(MEDIA_ROUTER_SERVICE);
            mediaRouter
                    .addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, callback);
            updatePresentation();
        }
        displayLayout.go(0, false);
        View view = findViewById(R.id.display_attributes);
        DisplayInfoHelper.populate(this, view, getWindowManager().getDefaultDisplay());
        DisplayInfoHelper.show(view, DURATION);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            advance();
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_PAGE_UP:
                previous();
                return true;
            case KeyEvent.KEYCODE_SPACE:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_PAGE_DOWN:
                advance();
                return true;
            case KeyEvent.KEYCODE_I:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_PERIOD:
                display();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private void updatePresentation() {
        MediaRouter.RouteInfo route = mediaRouter
                .getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay = route != null ? route
                .getPresentationDisplay() : null;
        if (displayPresentation != null
                && displayPresentation.getDisplay() != presentationDisplay) {
            displayPresentation.dismiss();
            displayPresentation = null;
        }
        if (displayPresentation == null && presentationDisplay != null) {
            displayPresentation = new DisplayPresentation(this, presentationDisplay);
            displayPresentation.setOnDismissListener(
                    new OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (dialog == displayPresentation) {
                                displayPresentation = null;
                            }
                        }
                    }
            );
            displayPresentation.show();
            displayPresentation.go(displayLayout.getCurrentSlidePos(), displayLayout.getCurrentSlidePhase());
        }
    }

    protected void next() {
        if (displayLayout != null) {
            displayLayout.next();
        }
        if (displayPresentation != null) {
            displayPresentation.next();
        }
    }

    protected void previous() {
        if (displayLayout != null) {
            displayLayout.previous();
        }
        if (displayPresentation != null) {
            displayPresentation.previous();
        }
    }

    protected void advance() {
        if (displayLayout != null) {
            displayLayout.advance();
        }
        if (displayPresentation != null) {
            displayPresentation.advance();
        }
    }

    public void display() {
        if (displayLayout != null) {
            DisplayInfoHelper.show(findViewById(R.id.display_attributes), DURATION);
        }
        if (displayPresentation != null) {
            DisplayInfoHelper.show(displayPresentation.findViewById(R.id.display_attributes), DURATION);
        }
    }

    public DisplayLayout getDisplayLayout() {
        return displayLayout;
    }
}
