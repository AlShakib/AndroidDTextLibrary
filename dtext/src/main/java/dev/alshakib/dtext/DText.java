/*
 * MIT License
 *
 * Copyright (c) 2021 Al Shakib (shakib@alshakib.dev)
 *
 * This file is part of Android DText Library
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package dev.alshakib.dtext;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.util.TypedValue;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DText extends ShapeDrawable {
    private final Builder builder;
    private final Paint textPaint;

    private final float height;
    private final float width;
    private final float textSize;

    private List<String> randomColorList;

    private DText(Builder builder) {
        super(builder.shape);
        this.builder = builder;

        // If context is found, do not use pixel.
        // Use sp and dp as unit of measurement.
        height = builder.context != null ? dpToPx(builder.height) : builder.height;
        width = builder.context != null ? dpToPx(builder.width) : builder.width;
        textSize = builder.context != null ? spToPx(builder.textSize) : builder.textSize;

        if (builder.isRandomBackgroundColor) {
            String[] defaultColors = {
                    "#DB4437",
                    "#E91E63",
                    "#9C27B0",
                    "#673AB7",
                    "#3F51B5",
                    "#4285F4",
                    "#039BE5",
                    "#0097A7",
                    "#009688",
                    "#0F9D58",
                    "#689F38",
                    "#EF6C00",
                    "#FF5722",
                    "#757575"
            };
            randomColorList = builder.randomColorList == null ?
                    new ArrayList<>(Arrays.asList(defaultColors)) :
                    builder.randomColorList;
        }

        // Initialize paint class for text
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(builder.textColor);
        textPaint.setTypeface(builder.typeface);

        int backgroundColor = builder.isRandomBackgroundColor ?
                getRandomColor() : builder.backgroundColor;

        // Initialize paint class for background
        Paint paint = getPaint();
        paint.setColor(backgroundColor);
    }

    // Use to convert Density-independent Pixels to Pixels
    private float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                builder.context.getResources().getDisplayMetrics());
    }

    // Use to convert Scale-independent Pixels to Pixels
    private float spToPx(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                builder.context.getResources().getDisplayMetrics());
    }

    private String getValidFirstChar(String text) {
        // Remove non digit characters if isDigitOnly is set.
        // If the text is "You have 4 notifications", the library will build "4" as a drawable.
        text = builder.isDigitOnly ? text.replaceAll("[^\\p{Nd}]", "") : text;

        // Remove non Alpha Numeric characters if isAlphaNumOnly is set.
        // If the text is "<Unknown>", the library will not build "<" as a drawable.
        // The library will build "U" as a drawable.
        text = builder.isAlphaNumOnly ? text.replaceAll("[^\\p{L}\\p{Nl}\\p{Nd}]", "") : text;

        return String.valueOf(text.charAt(0));
    }

    private String getValidText() {
        String text;
        if (builder.firstText != null && builder.lastText != null) {
            if (builder.isFirstCharOnly) {
                String first = getValidFirstChar(builder.firstText.trim());
                String last = getValidFirstChar(builder.lastText.trim());
                if (first.isEmpty() && last.isEmpty()) {
                    // Build a dot as a drawable, if no valid text is found.
                    return "•";
                }
                text = first + last;
            } else {
                text = builder.text.trim();
            }
        } else {
            if (builder.isFirstCharOnly) {
                text = getValidFirstChar(builder.text.trim());
                if (text.isEmpty()) {
                    // Build a dot as a drawable, if no valid text is found.
                    return "•";
                }
            } else {
                text = builder.text.trim();
            }
        }
        text = builder.toUpperCase ? text.toUpperCase() : text;
        return text;
    }

    // Generate random color if isRandomBackgroundColor is set
    private int getRandomColor() {
        Random random = new Random(System.currentTimeMillis());
        return Color.parseColor(randomColorList
                .get(random.nextInt(randomColorList.size())));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Rect bounds = getBounds();

        int savedCanvasCount = canvas.save();
        canvas.translate(bounds.left, bounds.top);

        float canvasWidth = this.width < 0 ? bounds.width() : this.width;
        float canvasHeight = this.height < 0 ? bounds.height() : this.height;
        float textSize = this.textSize < 0 ?
                (Math.min(canvasWidth, canvasHeight) / 2) : this.textSize;

        textPaint.setTextSize(textSize);
        canvas.drawText(getValidText(), canvasWidth / 2, canvasHeight / 2 -
                ((textPaint.descent() + textPaint.ascent()) / 2), textPaint);

        canvas.restoreToCount(savedCanvasCount);
    }

    @Override
    public void setAlpha(int alpha) {
        textPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        textPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return (int) this.width;
    }

    @Override
    public int getIntrinsicHeight() {
        return (int) this.height;
    }

    public static final class Builder {
        private Context context;
        private Shape shape;
        private String text;
        private String firstText;
        private String lastText;
        private Typeface typeface;
        private int textColor;
        private float textSize;
        private int backgroundColor;
        private float width;
        private float height;
        private float radius;
        private boolean toUpperCase;
        private boolean isFirstCharOnly;
        private boolean isDigitOnly;
        private boolean isAlphaNumOnly;
        private boolean isRandomBackgroundColor;
        private List<String> randomColorList;

        public Builder() {
            text = "";
            textSize = -1;
            width = -1;
            height = -1;
            backgroundColor = Color.GRAY;
            textColor = Color.WHITE;
            shape = new RectShape();
            typeface = Typeface.DEFAULT;
        }

        public Builder useSpAndDp(@NonNull Context context) {
            this.context = context.getApplicationContext();
            return this;
        }

        public boolean isSpAndDp() {
            return this.context != null;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public Builder setText(String first, String last) {
            this.firstText = first;
            this.lastText = last;
            return setText(first + last);
        }

        public Builder setText(String first, String last, String separator) {
            this.firstText = first;
            this.lastText = last;
            return setText(first + separator + last);
        }

        public String getText() {
            return this.text;
        }

        public Builder setHeight(float height) {
            this.height = height;
            return this;
        }

        public float getHeight() {
            return this.height;
        }

        public Builder setWidth(float width) {
            this.width = width;
            return this;
        }

        public float getWidth() {
            return this.width;
        }

        public Builder setTypeface(Typeface typeface) {
            this.typeface = typeface;
            return this;
        }

        public Typeface getTypeface() {
            return this.typeface;
        }

        public Builder setTextSize(float textSize) {
            this.textSize = textSize;
            return this;
        }

        public float getTextSize() {
            return this.textSize;
        }

        public Builder setTextColor(int color) {
            this.textColor = color;
            return this;
        }

        public int getTextColor() {
            return this.textColor;
        }

        public Builder setTextColor(String color) {
            this.textColor = Color.parseColor(color);
            return this;
        }

        public Builder setBackgroundColor(int color) {
            this.backgroundColor = color;
            return this;
        }

        public int getBackgroundColor() {
            return this.backgroundColor;
        }

        public Builder setBackgroundColor(String color) {
            this.backgroundColor = Color.parseColor(color);
            return this;
        }

        public Builder enableUpperCase(boolean flag) {
            this.toUpperCase = flag;
            return this;
        }

        public Builder toUpperCase() {
            enableUpperCase(true);
            return this;
        }

        public boolean isToUpperCase() {
            return this.toUpperCase;
        }

        public Builder enableFirstCharOnly(boolean flag) {
            this.isFirstCharOnly = flag;
            return this;
        }

        public Builder firstCharOnly() {
            enableFirstCharOnly(true);
            return this;
        }

        public boolean isFirstCharOnly() {
            return this.isFirstCharOnly;
        }

        public Builder enableDigitOnly(boolean flag) {
            this.isDigitOnly = flag;
            return this;
        }

        public Builder digitOnly() {
            enableDigitOnly(true);
            return this;
        }

        public boolean isDigitOnly() {
            return this.isDigitOnly;
        }

        public Builder enableAlphaNumOnly(boolean flag) {
            this.isAlphaNumOnly = flag;
            return this;
        }

        public Builder alphaNumOnly() {
            enableAlphaNumOnly(true);
            return this;
        }

        public boolean isAlphaNumOnly() {
            return this.isAlphaNumOnly;
        }

        public Builder enableRandomBackgroundColor(boolean flag) {
            this.isRandomBackgroundColor = flag;
            return this;
        }

        public Builder randomBackgroundColor() {
            enableRandomBackgroundColor(true);
            return this;
        }

        public boolean isRandomBackgroundColor() {
            return this.isRandomBackgroundColor;
        }

        public Builder setRandomColorList(List<String> backgroundColorList) {
            this.randomColorList = backgroundColorList;
            return this;
        }

        public List<String> getRandomColorList() {
            return this.randomColorList;
        }

        public Builder boldText() {
            typeface = Typeface.create(typeface, Typeface.BOLD);
            return this;
        }

        public boolean isBoldText() {
            return typeface.isBold();
        }

        public Builder italicText() {
            typeface = Typeface.create(typeface, Typeface.ITALIC);
            return this;
        }

        public boolean isItalicText() {
            return typeface.isItalic();
        }

        public Builder boldItalicText() {
            typeface = Typeface.create(typeface, Typeface.BOLD_ITALIC);
            return this;
        }

        public Builder drawAsRectangle() {
            this.shape = new RectShape();
            return this;
        }

        public boolean isRectangle() {
            return this.shape instanceof RectShape;
        }

        public Builder drawAsRectangle(float radius) {
            this.radius = radius;
            float[] radii = {radius, radius, radius, radius, radius, radius, radius, radius};
            this.shape = new RoundRectShape(radii, null, null);
            return this;
        }

        public float getRadius() {
            return this.radius;
        }

        public Builder drawAsRound() {
            shape = new OvalShape();
            return this;
        }

        public boolean isRound() {
            return this.shape instanceof OvalShape;
        }

        public Shape getShape() {
            return this.shape;
        }

        public DText build() {
            return new DText(this);
        }
    }
}
