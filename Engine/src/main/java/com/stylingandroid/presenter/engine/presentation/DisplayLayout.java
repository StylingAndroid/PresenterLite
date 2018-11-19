package com.stylingandroid.presenter.engine.presentation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ViewSwitcher;

import com.stylingandroid.presenter.R;
import com.stylingandroid.presenter.engine.AppState;
import com.stylingandroid.presenter.engine.display.StandaloneDisplayActivity;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisplayLayout extends ViewSwitcher {
    private List<String> slides = new ArrayList<>();

    private int current = 0;

    private static final String TAG = StandaloneDisplayActivity.TAG;

    private PhasedLayout currentSlide;

    private int inAnim = -1;
    private int outAnim = -1;

    public DisplayLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setValidating(false);
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Error creating XmlPullParserFactory", e);
        }

        TypedArray ta = context.obtainStyledAttributes(
                attrs,
                R.styleable.DisplayLayout
        );

        CharSequence presentationName = AppState.getInstance().getPresentationName();
        if (presentationName != null) {
            Log.v(TAG, "Presentation: " + presentationName);
            int id = getResources().getIdentifier(presentationName.toString(), "array", context.getPackageName());
            Log.v(TAG, "id: " + id);
            if (id > 0) {
                String[] slideNames = getResources().getStringArray(id);
                Log.v(TAG, "slideNames: " + Arrays.toString(slideNames));
                slides.addAll(Arrays.asList(slideNames));
            }
            for (String slide : slides) {
                Log.v(TAG, "Slide: " + slide);
            }
        }

        inAnim = ta.getResourceId(R.styleable.DisplayLayout_inAnimation, -1);
        outAnim = ta.getResourceId(R.styleable.DisplayLayout_outAnimation, -1);

        ta.recycle();

    }

    public int getCurrentSlidePhase() {
        int phase = -1;
        if (currentSlide != null) {
            phase = currentSlide.getPhase();
        }
        return phase;
    }

    public void go(int pos, int phase) {
        go(pos, false);
        while (currentSlide.hasMorePhases() && currentSlide.getPhase() < phase) {
            currentSlide.nextPhase();
        }
    }

    public void go(int pos, boolean animate) {
        Log.d(TAG, "Loading " + slides.get(pos));
        int id = getContext().getResources().getIdentifier(slides.get(pos), "layout", getContext().getPackageName());
        View v = null;
        if (id > 0) {
            v = LayoutInflater.from(getContext()).inflate(id, this, false);
        } else {
            Log.e(TAG, "Resource not found " + slides.get(pos));
        }
        current = pos;
        if (v != null && v instanceof PhaseContainer) {
            PhasedLayout newSlide = ((PhaseContainer) v).getPhasedLayout();
            int in = inAnim;
            int out = outAnim;
            if (currentSlide != null) {
                if (currentSlide.getInAnimation() > 0) {
                    in = currentSlide.getInAnimation();
                }
                if (currentSlide.getOutAnimation() > 0) {
                    out = currentSlide.getOutAnimation();
                }
            }
            if (animate && in > 0 && out > 0) {
                setInAnimation(getContext(), in);
                setOutAnimation(getContext(), out);
            } else {
                setInAnimation(getContext(), R.anim.none);
                setOutAnimation(getContext(), R.anim.none);
            }
            while (getChildCount() > 1) {
                removeViewAt(0);
            }
            addView(v);
            setCurrentSlide(newSlide);
            showNext();
        }

    }

    public void setCurrentSlide(PhasedLayout slide) {
        this.currentSlide = slide;
    }

    public void next() {
        next(false);
    }

    public void next(boolean animate) {
        if (current < slides.size() - 1) {
            go(++current, animate);
        }
    }

    public void previous() {
        if (current > 0) {
            go(--current, false);
        }
    }

    public void advance() {
        if (currentSlide.hasMorePhases()) {
            currentSlide.nextPhase();
        } else {
            next(true);
        }
    }

    public int getCurrentSlidePos() {
        return current;
    }

    public void goLast() {
        while (currentSlide.hasMorePhases()) {
            currentSlide.nextPhase();
        }
    }

    public boolean isLastSlide() {
        return current >= slides.size() - 1;
    }

    public boolean isLastPhase() {
        return !currentSlide.hasMorePhases();
    }
}
