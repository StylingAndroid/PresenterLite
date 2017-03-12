package com.stylingandroid.presenter.engine.display;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.stylingandroid.presenter.R;

public final class DisplayInfoHelper {
    public static final int DURATION = 500;

    private DisplayInfoHelper() {
        //NO-OP
    }

    public static void populate(Context context, View view, Display display) {
        View da = view.findViewById(R.id.display_attributes);
        Resources res = context.getResources();
        if (da != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            ((TextView) da.findViewById(R.id.density))
                    .setText(String.format("%s (%f)",
                            res.getString(R.string.density), metrics.density));
            ((TextView) da.findViewById(R.id.size)).setText(res
                    .getString(R.string.size));
            ((TextView) da.findViewById(R.id.dimensions))
                    .setText(String
                            .format("%dx%d (%dx%d)",
                                    metrics.widthPixels,
                                    metrics.heightPixels,
                                    (int) ((float) metrics.widthPixels / metrics.density),
                                    (int) ((float) metrics.heightPixels / metrics.density)));
        }
    }

    public static void hide(final View view, int when) {
        view.postDelayed(new Runnable() {

            @Override
            public void run() {
                view.animate().alpha(0).setDuration(DURATION);
            }
        }, when);
    }

    public static void show(final View view, int duration) {
        if (view != null) {
            view.animate().alpha(1).setDuration(DURATION);
            view.postDelayed(new Runnable() {

                @Override
                public void run() {
                    view.animate().alpha(0).setDuration(DURATION);
                }
            }, duration);
        }
    }
}
