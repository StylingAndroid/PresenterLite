package com.stylingandroid.presenter.engine.presentation;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * Created by markallison on 09/06/2013.
 */
public class FilteredImageView extends AnimatedImageView {
    private float saturation = 1.0f;

    public FilteredImageView(Context context) {
        this(context, null, -1);
    }

    public FilteredImageView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public FilteredImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setSaturation(float sat) {
        saturation = sat;
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(sat);
        ColorFilter filter = new ColorMatrixColorFilter(cm);
        getDrawable().setColorFilter(filter);
    }

    public float getSaturation() {
        return saturation;
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable.getConstantState().newDrawable());
        setSaturation(1.0f);
    }
}
