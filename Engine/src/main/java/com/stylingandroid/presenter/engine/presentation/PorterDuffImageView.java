package com.stylingandroid.presenter.engine.presentation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.stylingandroid.presenter.R;

public class PorterDuffImageView extends AppCompatImageView implements Phaseable {
    private PorterDuff.Mode mMode = null;
    private Bitmap mSrc = null;
    private Bitmap mDst = null;

    private Phaser phaser = null;

    public PorterDuffImageView(Context context) {
        this(context, null, -1);
    }

    public PorterDuffImageView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public PorterDuffImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PorterDuffImageView);
        int srcId = ta.getResourceId(R.styleable.PorterDuffImageView_src, 0);
        if (srcId > 0) {
            mSrc = BitmapFactory.decodeResource(getResources(), srcId);
        }
        int dstId = ta.getResourceId(R.styleable.PorterDuffImageView_dst, 0);
        if (dstId > 0) {
            mDst = BitmapFactory.decodeResource(getResources(), dstId);
        }
        int mode = ta.getInt(R.styleable.PorterDuffImageView_mode, -1);
        if (mode >= 0) {
            mMode = PorterDuff.Mode.values()[mode];
        }
        phaser = new Phaser(context, attrs);
        phaser.setInitialVisibility(this);
        ta.recycle();
        combine();
    }

    private void combine() {
        Bitmap out = null;
        if (mSrc != null && mDst != null) {
            int width = Math.max(mSrc.getWidth(), mDst.getWidth());
            int height = Math.max(mSrc.getHeight(), mDst.getHeight());
            out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            int sOffsetX = (out.getWidth() - mSrc.getWidth()) / 2;
            int sOffsetY = (out.getHeight() - mSrc.getHeight()) / 2;
            int dOffsetX = (out.getWidth() - mDst.getWidth()) / 2;
            int dOffsetY = (out.getHeight() - mDst.getHeight()) / 2;

            Canvas canvas = new Canvas(out);
            canvas.setMatrix(getMatrix());
            canvas.drawBitmap(mDst, sOffsetX, sOffsetY, null);
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(mMode));
            canvas.drawBitmap(mSrc, dOffsetX, dOffsetY, paint);
        }
        setImageBitmap(out);
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
