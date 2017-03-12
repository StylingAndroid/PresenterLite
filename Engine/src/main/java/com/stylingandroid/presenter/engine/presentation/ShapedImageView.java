package com.stylingandroid.presenter.engine.presentation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.stylingandroid.presenter.R;

@SuppressWarnings("PMD.UselessParentheses")
public class ShapedImageView extends AnimatedImageView {
    public static final int ROTATION = 30;
    public static final int OVAL = 1;
    public static final int ROTATED_OVAL = 2;
    public static final int CLIPPED_ROTATED_OVAL = 3;
    public static final int HEART = 4;
    public static final int OVAL_FACTOR = 8;
    private int shape;

    @SuppressWarnings("unused")
    public ShapedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttrs(context, attrs);
    }

    @SuppressWarnings("unused")
    public ShapedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseAttrs(context, attrs);
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
        if (!isInEditMode()) {
            TypedArray ta = context.obtainStyledAttributes(attrs,
                    R.styleable.ShapedImageView);
            shape = ta.getInt(R.styleable.ShapedImageView_shape, 0);
            Drawable drawable = getDrawable();
            if (drawable != null) {
                setImageDrawable(createOutlinedDrawable(drawable));
            }
            ta.recycle();
        }
    }

    public Drawable createOutlinedDrawable(Drawable drawable) {
        Drawable newDrawable = drawable;
        if (drawable instanceof BitmapDrawable) {
            Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();
            newDrawable = new BitmapDrawable(getResources(), createOutlinedBitmap(bmp));
        }
        return newDrawable;
    }

    public Bitmap createOutlinedBitmap(Bitmap bm) {
        Bitmap outlinedBitmap;
        switch (shape) {
            case OVAL:
                outlinedBitmap = oval(bm);
                break;
            case ROTATED_OVAL:
                outlinedBitmap = rotatedOval(bm);
                break;
            case CLIPPED_ROTATED_OVAL:
                outlinedBitmap = clippedRotatedOval(bm);
                break;
            case HEART:
                outlinedBitmap = heart(bm);
                break;
            default:
                outlinedBitmap = bm;
                break;
        }
        return outlinedBitmap;
    }

    private Bitmap oval(Bitmap bitmap) {
        Bitmap bmp;
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();

        bmp = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);

        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Path oval = new Path();
        RectF ovalRect = new RectF(width / OVAL_FACTOR, 0, width - (width / OVAL_FACTOR), height);

        oval.addOval(ovalRect, Path.Direction.CW);
        canvas.drawPath(oval, paint);

        return bmp;
    }

    private Bitmap rotatedOval(Bitmap bitmap) {
        Bitmap bmp;
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();

        bmp = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);

        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Path oval = new Path();
        Matrix matrix = new Matrix();
        RectF ovalRect = new RectF(width / OVAL_FACTOR, 0, width - (width / OVAL_FACTOR), height);

        oval.addOval(ovalRect, Path.Direction.CW);
        matrix.postRotate(ROTATION, width / 2, height / 2);
        oval.transform(matrix, oval);
        canvas.drawPath(oval, paint);

        return bmp;
    }

    private Bitmap clippedRotatedOval(Bitmap bitmap) {
        Bitmap bmp;
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();

        bmp = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);

        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Path oval = new Path();
        Matrix matrix = new Matrix();
        Region region = new Region();
        RectF ovalRect = new RectF(width / OVAL_FACTOR, 0, width - (width / OVAL_FACTOR), height);

        oval.addOval(ovalRect, Path.Direction.CW);
        matrix.postRotate(ROTATION, width / 2, height / 2);
        oval.transform(matrix, oval);
        region.setPath(oval, new Region((int) width / 2, 0, (int) width, (int) height));
        canvas.drawPath(region.getBoundaryPath(), paint);

        return bmp;
    }

    private Bitmap heart(Bitmap bitmap) {
        Bitmap bmp;
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();

        bmp = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);

        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Path oval = new Path();
        Matrix matrix = new Matrix();
        Region region = new Region();
        RectF ovalRect = new RectF(width / OVAL_FACTOR, 0, width - (width / OVAL_FACTOR), height);

        oval.addOval(ovalRect, Path.Direction.CW);
        matrix.postRotate(ROTATION, width / 2, height / 2);
        oval.transform(matrix, oval);
        region.setPath(oval, new Region((int) width / 2, 0, (int) width, (int) height));
        canvas.drawPath(region.getBoundaryPath(), paint);

        matrix.reset();
        oval.reset();
        oval.addOval(ovalRect, Path.Direction.CW);
        matrix.postRotate(-ROTATION, width / 2, height / 2);
        oval.transform(matrix, oval);
        region.setPath(oval, new Region(0, 0, (int) width / 2, (int) height));
        canvas.drawPath(region.getBoundaryPath(), paint);

        return bmp;
    }

}
