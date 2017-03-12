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

    void onFinishInflate(ViewGroup layout) {
        phaseables.clear();
        getPhases(layout, phaseables);
        phase = 0;
        if (context instanceof TweetNotes) {
            TweetNotes tweetNotes = (TweetNotes) context;
            if (tweet != null) {
                tweetNotes.tweet(tweet);
            }
            if (notes != null) {
                tweetNotes.notes(notes);
            } else {
                tweetNotes.notes("");
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

    private static void getPhases(ViewGroup parent, List<Phaseable> phaseables) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            int phase = 0;
            if (child instanceof Phaseable) {
                Phaseable phaseable = (Phaseable) child;
                if (phaseable.getLastPhase() > 0) {
                    phaseables.add((Phaseable) child);
                }
            }
            Log.d(TAG, "Phase: " + phase + "; " + child.toString());
            if (child instanceof ViewGroup) {
                getPhases((ViewGroup) child, phaseables);
            }
        }
    }

    public boolean hasMorePhases() {
        Log.d(TAG, "Phase: " + phase);
        return !phaseables.isEmpty();
    }

    public void nextPhase() {
        phase++;
        List<Phaseable> complete = new ArrayList<>();
        for (Phaseable phaseable : phaseables) {
            if (phaseable.setPhase(phase)) {
                complete.add(phaseable);
            }
        }
        phaseables.removeAll(complete);
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
