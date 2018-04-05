package com.stylingandroid.presenter.engine.presentation;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stylingandroid.presenter.R;
import com.stylingandroid.presenter.engine.AppState;
import com.stylingandroid.presenter.engine.display.StandaloneDisplayActivity;

import java.util.ArrayList;
import java.util.List;

final class PhasedLayout {
    private static final String TAG = StandaloneDisplayActivity.TAG;
    public static final int AUTOSTART_DELAY = 500;

    private Context context;

    private List<Phaseable> phaseables = new ArrayList<>();
    private int phase = 0;

    private int inAnimation = 0;
    private int outAnimation = 0;

    private boolean autoStart = false;

    private String tweet = null;
    private String notes = null;

    private Handler handler;

    PhasedLayout(Context context, int inAnimation, int outAnimation, String tweet, String notes, boolean autoStart) {
        this.context = context;
        this.inAnimation = inAnimation;
        this.outAnimation = outAnimation;
        this.tweet = tweet;
        this.notes = notes;
        this.autoStart = autoStart;

        handler = new Handler(context.getMainLooper());
        phase = 0;
    }

    void onFinishInflate(ViewGroup layout, boolean recursive) {
        phaseables.clear();
        getPhases(layout, phaseables, recursive);
        phase = 0;
        if (context instanceof Tweet && tweet != null) {
            Tweet tweeter = (Tweet) context;
            tweeter.tweet(tweet);
        }
        if (context instanceof Notes) {
            Notes noter = (Notes) context;
            if (notes != null) {
                noter.notes(notes);
            } else {
                noter.notes("");
            }
        }
        View eventLongName = layout.findViewById(R.id.event_long_name);
        if (eventLongName != null && eventLongName instanceof TextView) {
            ((TextView) eventLongName).setText(AppState.getInstance().getEventLongName());
        }
        View eventShortName = layout.findViewById(R.id.event_short_name);
        if (eventShortName != null && eventShortName instanceof TextView) {
            ((TextView) eventShortName).setText(AppState.getInstance().getEventShortName());
        }
        View eventHashtag = layout.findViewById(R.id.event_hashtag);
        if (eventHashtag != null && eventHashtag instanceof TextView) {
            ((TextView) eventHashtag).setText(AppState.getInstance().getEventHashtag());
        }

        if (autoStart) {
            handler.postDelayed(runnable, AUTOSTART_DELAY);
        }
    }

    private static void getPhases(ViewGroup parent, List<Phaseable> phaseables, boolean recursive) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof Phaseable) {
                Phaseable phaseable = (Phaseable) child;
                if (phaseable.getLastPhase() > 0) {
                    phaseables.add((Phaseable) child);
                }
            }
            if (recursive && child instanceof ViewGroup) {
                getPhases((ViewGroup) child, phaseables, recursive);
            }
        }
    }

    public boolean hasMorePhases() {
        Log.d(TAG, "Phase: " + phase);
        return !phaseables.isEmpty();
    }

    public void nextPhase() {
        phase++;
        Log.d(TAG, "nextPhase: " + phase);
        List<Phaseable> complete = new ArrayList<>();
        for (Phaseable phaseable : phaseables) {
            if (phaseable.setPhase(phase)) {
                complete.add(phaseable);
            }
        }
        phaseables.removeAll(complete);
        Log.d(TAG, "nextPhase remaining: " + phaseables.size());
    }

    public int getPhase() {
        return phase;
    }

    public int getInAnimation() {
        return inAnimation;
    }

    public int getOutAnimation() {
        return outAnimation;
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            nextPhase();
        }
    };
}
