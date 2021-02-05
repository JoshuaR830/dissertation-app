package com.joshuarichardson.fivewaystowellbeing.ui.graphs;

import android.graphics.Paint;
import android.graphics.RectF;

public class WaysToWellbeingGraphValues {

    private final int FULL_CIRCLE = 360;

    private RectF shape;
    private int arcStart;
    private int arcLength;
    private Paint paint = new Paint();
    private int value;

    public WaysToWellbeingGraphValues(int value, int paintColor, int totalSegments, int segment) {
        if(segment < 0 || totalSegments <= 0 || segment >= totalSegments) {
            return;
        }

        this.value = value;
        this.arcLength = calculateArcLength(totalSegments);
        this.arcStart = calculateArcStart(segment);
        this.paint.setColor(paintColor);
    }

    private int calculateArcLength(int totalSegments) {
        return FULL_CIRCLE / totalSegments;
    }

    private int calculateArcStart(int segment) {
        int offset = this.arcLength / 2;
        int startingPoint = (segment * this.arcLength) - ((FULL_CIRCLE/4) + offset);
        return (startingPoint < 0) ? startingPoint + FULL_CIRCLE : startingPoint;
    }

    public void updateSize(float multiplier, int height, int width) {
        float size = this.value * multiplier;
        this.shape = new RectF(width - size, height - size, width + size, height + size);
    }

    public void updateValue(int value, float multiplier, int height, int width) {
        this.value = value;
        this.updateSize(multiplier, height, width);
    }

    public RectF getShape() {
        return this.shape;
    }

    public int getStartAngle() {
        return this.arcStart;
    }

    public int getArcLength() {
        return this.arcLength;
    }

    public boolean getUseCenter() {
        return true;
    }

    public Paint getPaint() {
        return this.paint;
    }
}
