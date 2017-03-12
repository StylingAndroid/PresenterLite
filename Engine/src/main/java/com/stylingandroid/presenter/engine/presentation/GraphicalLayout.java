package com.stylingandroid.presenter.engine.presentation;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.util.TimingLogger;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stylingandroid.presenter.R;

public class GraphicalLayout extends RelativeLayout implements Phaseable {
    private static final String TAG = "Presenter";
    private static final Matrix IDENTITY = new Matrix();
    public static final int BLUR_PADDING = 20;
    public static final int HALF_BLUR_PADDING = BLUR_PADDING / 2;
    private Phaser phaser = null;
    //private Bitmap mOriginal = null;
    //private ImageView mBackground = null;

    public GraphicalLayout(Context context) {
        super(context);
    }

    public GraphicalLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public GraphicalLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        phaser = new Phaser(context, attrs);
        phaser.setInitialVisibility(this);
    }

    private enum EffectType {
        FLATTEN_BACKGROUND(0x01),
        FLATTEN(0x02),
        DESATURATE(0x04),
        DESATURATE_TEXT(0x08),
        BLUR(0x10);

        private final int value;

        EffectType(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }

    private ViewTreeObserver.OnPreDrawListener mPredrawListener = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            ViewTreeObserver observer = getViewTreeObserver();
            if (observer != null) {
                observer.removeOnPreDrawListener(this);
            }
            layoutUpdated();
            return true;
        }
    };

    public static class LayoutParams extends RelativeLayout.LayoutParams {
        public static final float BLUR_RADIUS = 5.0f;
        private boolean mFlatten = false;
        private int mEffects = 0;
        private float mBlurRadius = BLUR_RADIUS;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray ta = c.obtainStyledAttributes(attrs, R.styleable.GraphicalLayout_Layout);
            mFlatten = ta.getBoolean(R.styleable.GraphicalLayout_Layout_flatten, false);
            mEffects = ta.getInt(R.styleable.GraphicalLayout_Layout_effect, 0);
            int blurRadiusId = ta.getResourceId(R.styleable.GraphicalLayout_Layout_blur_radius, 0);
            if (blurRadiusId > 0) {
                mBlurRadius = ta.getFloat(blurRadiusId, BLUR_RADIUS);
            } else {
                mBlurRadius = ta.getFloat(R.styleable.GraphicalLayout_Layout_blur_radius, BLUR_RADIUS);
            }
            ta.recycle();
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public LayoutParams(RelativeLayout.LayoutParams source) {
            super(source);
        }

        public boolean getFlatten() {
            return mFlatten;
        }

        public int getEffects() {
            return mEffects;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    private void layoutUpdated() {
        measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
        Bitmap background = null;
        Object bkg = getTag(R.id.original_background);
        if (bkg != null && bkg instanceof BitmapDrawable) {
            background = ((BitmapDrawable) bkg).getBitmap();
        }
        if (background == null) {
            Drawable b = getBackground();
            if (b != null && b instanceof BitmapDrawable) {
                background = ((BitmapDrawable) b).getBitmap();
                setTag(R.id.original_background, b);
            }
        }
        if (background == null) {
            background = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        } else {
            background = background.copy(Bitmap.Config.ARGB_8888, true);
        }

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != null && child.getVisibility() == VISIBLE) {
                ViewGroup.LayoutParams params = child.getLayoutParams();
                if (params != null && child.getLayoutParams() instanceof LayoutParams) {
                    LayoutParams layoutParams = (LayoutParams) params;
                    if (layoutParams.getFlatten()) {
                        background = flatten(background, child, layoutParams);
                    }
                }
            }
        }
        setBackgroundImage(new BitmapDrawable(getResources(), background));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        ViewTreeObserver observer = getViewTreeObserver();
        if (getVisibility() == View.VISIBLE && changed && observer != null && observer.isAlive()) {
            observer.addOnPreDrawListener(mPredrawListener);
        }
    }

    private Bitmap flatten(Bitmap bitmap, View child, LayoutParams params) {
        int effects = params.getEffects();
        Bitmap current = bitmap;
        if (effects > 0) {
            child.measure(MeasureSpec.makeMeasureSpec(child.getWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(child.getHeight(), MeasureSpec.EXACTLY));
            if ((effects & EffectType.FLATTEN_BACKGROUND.value()) == EffectType.FLATTEN_BACKGROUND.value()) {
                current = flattenBackground(current, child);
            }
            if ((effects & EffectType.FLATTEN.value()) == EffectType.FLATTEN.value()) {
                current = flatten(current, child);
            }
            if ((effects & EffectType.DESATURATE.value()) == EffectType.DESATURATE.value()) {
                current = desaturate(current, child);
            }
            if ((effects & EffectType.DESATURATE_TEXT.value()) == EffectType.DESATURATE_TEXT.value() && child instanceof TextView) {
                desaturateText(current, (TextView) child);
            }
            if ((effects & EffectType.BLUR.value()) == EffectType.BLUR.value()) {
                current = blur(current, child, params.mBlurRadius);
            }
        }
        return current;
    }

    private void setBackgroundImage(Drawable drawable) {
        setBackground(drawable);
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.TRANSPARENT);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    private Bitmap flattenBackground(Bitmap current, View view) {
        Bitmap cs = Bitmap.createBitmap(current.getWidth(), current.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);
        comboImage.drawBitmap(current, IDENTITY, null);
        Drawable drawable = view.getBackground();
        if (drawable != null) {
            drawable.setBounds(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            view.setBackground(null);
            drawable.draw(comboImage);
        }
        return cs;

    }

    private Bitmap flatten(Bitmap current, View view) {
        Canvas canvas = new Canvas(current);
        Bitmap viewBitmap = getBitmapFromView(view);
        canvas.drawBitmap(viewBitmap, view.getLeft(), view.getTop(), new Paint());
        view.setVisibility(INVISIBLE);
        return current;
    }

    private Bitmap desaturate(Bitmap current, View view) {
        Canvas canvas = new Canvas(current);
        Bitmap overlay = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas part = new Canvas(overlay);
        part.drawBitmap(current, -view.getLeft(), -view.getTop(), null);
        Paint paint = new Paint();
        ColorMatrix desatMatrix = new ColorMatrix();
        desatMatrix.setSaturation(0.0f);
        paint.setColorFilter(new ColorMatrixColorFilter(desatMatrix));
        canvas.drawBitmap(overlay, view.getLeft(), view.getTop(), paint);
        overlay.recycle();
        return current;
    }

    private void desaturateText(Bitmap current, TextView view) {
        Bitmap overlay = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas part = new Canvas(overlay);
        Paint desat = new Paint();
        ColorMatrix desatMatrix = new ColorMatrix();
        desatMatrix.setSaturation(0.0f);
        desat.setColorFilter(new ColorMatrixColorFilter(desatMatrix));
        part.drawBitmap(current, -view.getLeft(), -view.getTop(), desat);
        view.getPaint().setShader(new BitmapShader(overlay, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
    }

    private Bitmap blur(Bitmap current, View view, float radius) {
        TimingLogger tl = new TimingLogger(TAG, "blur");
        Bitmap overlay = Bitmap.createBitmap(view.getMeasuredWidth() + BLUR_PADDING,
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        tl.addSplit("Create Bitmap");

        RenderScript rs = RenderScript.create(getContext());
        tl.addSplit("Create RenderScript");
        Allocation in = Allocation.createFromBitmap(rs, current);
        tl.addSplit("Create Allocation in");
        Allocation region = Allocation.createFromBitmap(rs, overlay);
        tl.addSplit("Create Allocation region");
        region.copy2DRangeFrom(0, 0, overlay.getWidth(), overlay.getHeight(), in, view.getLeft() - HALF_BLUR_PADDING, view.getTop());
        tl.addSplit("copy2DRangeFrom");
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, region.getElement());
        tl.addSplit("Create blur");
        blur.setInput(region);
        tl.addSplit("set blur input");
        blur.setRadius(radius);
        tl.addSplit("set blur radius");
        blur.forEach(region);
        tl.addSplit("blur forEach");
        in.copy2DRangeFrom(view.getLeft(), view.getTop(), view.getMeasuredWidth(), view.getMeasuredHeight(), region, HALF_BLUR_PADDING, 0);
        tl.addSplit("copy2DRangeFrom");
        in.copyTo(current);
        tl.addSplit("copyTo");

        rs.destroy();
        tl.addSplit("Destroy RenderScript");

        overlay.recycle();
        tl.addSplit("Recycle bitmap");
        tl.dumpToLog();
        return current;
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
