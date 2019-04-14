package com.stylingandroid.presenter.engine.presentation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.text.Annotation;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;

import com.stylingandroid.presenter.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HighlightedTextView extends AppCompatTextView implements Phaseable, Animatable {
    private Pattern pattern = null;
    private int highlightStyle = 0;

    private Phaser phaser = null;

    private Drawable bullet = null;

    public HighlightedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HighlightedTextView(Context context, AttributeSet attrs,
                               int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            TypedArray ta = context.obtainStyledAttributes(attrs,
                    R.styleable.HighlightedTextView);
            int bulletId = ta.getResourceId(R.styleable.HighlightedTextView_bulletDrawable, 0);
            if (bulletId > 0) {
                bullet = getResources().getDrawable(bulletId, null);
                bullet.setBounds(0, 0, bullet.getIntrinsicWidth(), bullet.getIntrinsicHeight());
            }
            CharSequence text = ta
                    .getString(R.styleable.HighlightedTextView_android_text);
            if (text != null) {
                updateText(text);
            }
            int patternRes = ta.getResourceId(R.styleable.HighlightedTextView_pattern, 0);
            String patternString;
            if (patternRes > 0) {
                patternString = context.getResources().getString(patternRes);
            } else {
                patternString = ta.getString(R.styleable.HighlightedTextView_pattern);
            }
            if (patternString != null) {
                setPattern(patternString);
            }
            int style = ta.getResourceId(
                    R.styleable.HighlightedTextView_highlightTextAppearance, 0);
            if (style >= 0) {
                setHighlightStyle(style);
            }
            ta.recycle();
            phaser = new Phaser(context, attrs);
            phaser.setInitialVisibility(this);
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        updateText(text);
    }

    public void setPattern(String pattern) {
        this.pattern = pattern == null ? null : Pattern.compile(pattern,
                Pattern.MULTILINE | Pattern.DOTALL);
        updateText(getText());
    }

    public void setHighlightStyle(int highlightStyle) {
        this.highlightStyle = highlightStyle;
        updateText(getText());
    }

    public void updateText(CharSequence text) {
        Spannable spannable;
        if (bullet != null) {
            spannable = new SpannableString(" " + text);
            ImageSpan is = new ImageSpan(bullet, DynamicDrawableSpan.ALIGN_BASELINE);
            spannable.setSpan(is, 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            spannable = new SpannableString(text);
        }
        if (text instanceof SpannedString) {
            SpannedString spanned = (SpannedString)text;
            Annotation[] annotations = spanned.getSpans(0, spanned.length(), Annotation.class);
            for (Annotation annotation : annotations) {
                if (annotation.getKey().equals("format") && annotation.getValue().equals("superscript")) {
                    spannable.setSpan(
                            new SuperscriptSpan(),
                            spanned.getSpanStart(annotation),
                            spanned.getSpanEnd(annotation),
                            Spanned.SPAN_INCLUSIVE_INCLUSIVE
                    );
                    spannable.setSpan(
                            new RelativeSizeSpan(0.5f),
                            spanned.getSpanStart(annotation),
                            spanned.getSpanEnd(annotation),
                            Spanned.SPAN_INCLUSIVE_INCLUSIVE
                    );
                }
            }
        }
        if (pattern != null) {
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                TextAppearanceSpan span = new TextAppearanceSpan(getContext(),
                        highlightStyle);
                spannable.setSpan(span, start, end,
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        super.setText(spannable, BufferType.SPANNABLE);
    }

    @Override
    public boolean setPhase(int phase) {
        return phaser.setPhase(phase, this, true);
    }

    @Override
    public boolean hasMorePhases(int phase) {
        if (!isInEditMode()) {
            return phaser.getLastPhase() > phase;
        }
        return false;
    }

    @Override
    public void start() {
        if (bullet instanceof Animatable) {
            ((Animatable) bullet).start();
        }
    }

    @Override
    public void stop() {
        if (bullet instanceof Animatable) {
            ((Animatable) bullet).stop();
        }
    }

    @Override
    public boolean isRunning() {
        if (bullet instanceof Animatable) {
            return ((Animatable) bullet).isRunning();
        }
        return false;
    }
}
