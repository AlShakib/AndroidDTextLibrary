/*
 * Copyright (c) 2020 Al Shakib (shakib@alshakib.dev)
 *
 * This file is part of Android DText Library
 *
 * Android DText Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Android DText Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Android DText Library.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.alshakib.dtext;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DText extends ShapeDrawable {
    private final String LOG_TAG = this.getClass().getSimpleName();

    private final Builder builder;
    private final Paint textPaint;
    private final Paint borderPaint;

    private float height;
    private float width;
    private float radius;
    private float borderThickness;
    private float textSize;

    private DText(Builder builder) {
        super(builder.shape);
        this.builder = builder;

        // If context is found, do not use pixel.
        // Use sp and dp as unit of measurement.
        height = builder.context != null ? dpToPx(builder.height) : builder.height;
        width = builder.context != null ? dpToPx(builder.width) : builder.width;
        radius = builder.context != null ? dpToPx(builder.radius) : builder.radius;
        borderThickness = builder.context != null ? dpToPx(builder.borderThickness) : builder.borderThickness;
        textSize = builder.context != null ? spToPx(builder.textSize) : builder.textSize;

        // Initialize paint class for text
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(builder.textColor);
        textPaint.setTypeface(builder.typeface);
        textPaint.setStrokeWidth(this.borderThickness);

        int backgroundColor = builder.isRandomBackgroundColor ? getRandomColor() : builder.backgroundColor;

        // Initialize paint class for border
        borderPaint = new Paint();
        borderPaint.setColor(getDarkerShade(backgroundColor));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(this.borderThickness);

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

    private String getValidText(String text) {
        text = text.trim();

        // isDigitOnly and isAlphaNumOnly will work if isFirstCharOnly is set
        if (builder.isFirstCharOnly) {

            // Remove non digit characters if isDigitOnly is set.
            // If the text is "You have 4 notifications", the library will build "4" as a drawable.
            text = builder.isDigitOnly ? text.replaceAll("[^\\p{Nd}]", "") : text;

            // Remove non Alpha Numeric characters if isAlphaNumOnly is set.
            // If the text is "<Unknown>", the library will not build "<" as a drawable.
            // The library will build "U" as a drawable.
            text = builder.isAlphaNumOnly ? text.replaceAll("[^\\p{L}\\p{Nl}\\p{Nd}]", "") : text;

            if (text.isEmpty()) {
                // Build a dot as a drawable, if no valid text is found.
                return "•";
            }
            text = String.valueOf(text.charAt(0));
        }
        text = builder.toUpperCase ? text.toUpperCase() : text;
        return text;
    }

    // Generate random color if isRandomBackgroundColor is set
    private int getRandomColor() {
        Random random = new Random(System.currentTimeMillis());
        return Color.parseColor(builder.randomColorSet
                .get(random.nextInt(builder.randomColorSet.size())));
    }

    // Create a darker shade for border
    private int getDarkerShade(int color) {
        return Color.rgb((int) (builder.borderShadeFactor * Color.red(color)),
                (int) (builder.borderShadeFactor * Color.green(color)),
                (int) (builder.borderShadeFactor * Color.blue(color)));
    }

    // Draw a border around the drawable.
    private void drawBorder(Canvas canvas) {
        RectF rect = new RectF(getBounds());
        rect.inset((float) this.borderThickness / 2, (float) this.borderThickness / 2);

        if (builder.shape instanceof OvalShape) {
            canvas.drawOval(rect, borderPaint);
        } else if (builder.shape instanceof RoundRectShape) {
            canvas.drawRoundRect(rect, this.radius, this.radius, borderPaint);
        } else {
            canvas.drawRect(rect, borderPaint);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Rect bounds = getBounds();

        if (this.borderThickness > 0) {
            drawBorder(canvas);
        }

        int savedCanvasCount = canvas.save();
        canvas.translate(bounds.left, bounds.top);

        float canvasWidth = this.width < 0 ? bounds.width() : this.width;
        float canvasHeight = this.height < 0 ? bounds.height() : this.height;
        float textSize = this.textSize < 0 ? (Math.min(canvasWidth, canvasHeight) / 2) : this.textSize;

        textPaint.setTextSize(textSize);
        canvas.drawText(getValidText(builder.text), canvasWidth / 2, canvasHeight / 2 -
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

    public static class Builder {
        private Context context;
        private Shape shape;
        private String text;
        private Typeface typeface;
        private int textColor;
        private float textSize;
        private int backgroundColor;
        private float borderThickness;
        private float width;
        private float height;
        private float borderShadeFactor;
        private float radius;
        private boolean toUpperCase;
        private boolean isFirstCharOnly;
        private boolean isDigitOnly;
        private boolean isAlphaNumOnly;
        private boolean isRandomBackgroundColor;
        private List<String> randomColorSet;

        public void setDefaults() {
            text = "";
            textSize = -1;
            width = -1;
            height = -1;
            borderThickness = 0;
            borderShadeFactor = 0.9f;
            backgroundColor = Color.GRAY;
            textColor = Color.WHITE;
            shape = new RectShape();
            typeface = Typeface.DEFAULT;
            randomColorSet = new ArrayList<>(
                    Arrays.asList(
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
                    )
            );
        }

        public void doNotUsePixel(Context context) {
            this.context = context;
        }

        public void setText(String text) {
            this.text = text;
        }

        public void setHeight(float height) {
            this.height = height;
        }

        public void setWidth(float width) {
            this.width = width;
        }

        public void setTypeface(Typeface typeface) {
            this.typeface = typeface;
        }

        public void setTextSize(float textSize) {
            this.textSize = textSize;
        }

        public void setTextColor(int color) {
            this.textColor = color;
        }

        public void setBackgroundColor(int color) {
            this.backgroundColor = color;
        }

        public void setBorder(float thickness) {
            this.borderThickness = thickness;
        }

        public void setBorderShadeFactor(float borderShadeFactor) {
            this.borderShadeFactor = borderShadeFactor;
        }

        public void setUpperCase(boolean flag) {
            this.toUpperCase = flag;
        }

        public void toUpperCase() {
            setUpperCase(true);
        }

        public void setFirstCharOnly(boolean flag) {
            this.isFirstCharOnly = flag;
        }

        public void firstCharOnly() {
            setFirstCharOnly(true);
        }

        public void setDigitOnly(boolean flag) {
            this.isDigitOnly = flag;
        }

        public void digitOnly() {
            setDigitOnly(true);
        }

        public void setAlphaNumOnly(boolean flag) {
            this.isAlphaNumOnly = flag;
        }

        public void alphaNumOnly() {
            setAlphaNumOnly(true);
        }

        public void setRandomBackgroundColor(boolean flag) {
            this.isRandomBackgroundColor = flag;
        }

        public void randomBackgroundColor() {
            setRandomBackgroundColor(true);
        }

        public void bold() {
            typeface = Typeface.create(typeface, Typeface.BOLD);
        }

        public void italic() {
            typeface = Typeface.create(typeface, Typeface.ITALIC);
        }

        public void boldItalic() {
            typeface = Typeface.create(typeface, Typeface.BOLD_ITALIC);
        }

        public void drawAsRect() {
            this.shape = new RectShape();
        }

        public void drawAsRect(float radius) {
            this.radius = radius;
            float[] radii = {radius, radius, radius, radius, radius, radius, radius, radius};
            this.shape = new RoundRectShape(radii, null, null);
        }

        public void drawAsCircle() {
            shape = new OvalShape();
        }

        public DText build() {
            return new DText(this);
        }
    }
}
