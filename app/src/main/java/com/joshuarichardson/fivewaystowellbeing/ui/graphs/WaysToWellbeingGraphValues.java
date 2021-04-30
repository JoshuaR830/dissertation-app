package com.joshuarichardson.fivewaystowellbeing.ui.graphs;

import android.graphics.Paint;
import android.graphics.RectF;

/**
 * The values and functions for drawing each item on the polar pie chart
 */
public class WaysToWellbeingGraphValues {

    private final int FULL_CIRCLE = 360;
    private int totalSegments;
    private RectF shape;
    private int arcStart;
    private int arcLength;
    private Paint paint = new Paint();
    private int value;

    public WaysToWellbeingGraphValues(int value, int paintColor, int totalSegments, int segment) {
        if(segment < 0 || totalSegments <= 0 || segment >= totalSegments) {
            return;
        }
        this.totalSegments = totalSegments;
        this.value = value;
        this.arcLength = calculateArcLength(totalSegments);
        this.arcStart = calculateArcStart(segment);
        this.paint.setColor(paintColor);
        this.paint.setTextSize(45);
        this.paint.setStrokeWidth(4);
    }

    /**
     * Calculate the size of each arc
     *
     * @param totalSegments The total number of items to display on the graph
     * @return The size of the arc
     */
    private int calculateArcLength(int totalSegments) {
        return FULL_CIRCLE / totalSegments;
    }

    /**
     * Calculate the starting point for the segment
     *
     * @param segment The number of the segment starting at 0 for the top
     * @return The starting position of the arc
     */
    private int calculateArcStart(int segment) {
        // Rather than start at the top, want to offset by half an arc length to centralize
        int offset = this.arcLength / 2;

        // The starting point is not usually at the top but this corrects that
        int startingPoint = (segment * this.arcLength) - ((FULL_CIRCLE/4) + offset);
        return (startingPoint < 0) ? startingPoint + FULL_CIRCLE : startingPoint;
    }

    /**
     * The size of the coloured segment.
     * Allows for an animated graph.
     *
     * @param multiplier The size of the segment.
     * @param height The height based on size of canvas
     * @param width The width based on size of the canvas
     */
    public void updateSize(float multiplier, int height, int width) {
        float size = this.value * multiplier;
        this.shape = new RectF(width - size, height - size, width + size, height + size);
    }

    /**
     * Update the values for the graph so that the value can be changed
     *
     * @param value The new value
     * @param multiplier The size of the segment
     * @param height The height based on size of canvas
     * @param width The width based on size of canvas
     */
    public void updateValue(int value, float multiplier, int height, int width) {
        this.value = value;
        this.updateSize(multiplier, height, width);
    }

    public void updateColor(int color) {
        this.paint.setColor(color);
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

    /**
     * Calculate where the x point of the line needs drawn to
     *
     * @param centerX The x coordinate of centre
     * @param radius The radius of the circle
     * @return The x position of the centre of the arc
     */
    public int getCirclePointX(int centerX, int radius) {
        // Calculate x coord of point on circle
        int pointOfAngle = getStartAngle() + ((FULL_CIRCLE / this.totalSegments)/2);
        return centerX + (int)(radius*Math.cos(pointOfAngle*(Math.PI/180)));
    }

    /**
     * Calculate where the y point of the line needs drawn to
     *
     * @param centerY The y coordinate of centre
     * @param radius The radius of the circle
     * @return The y position of the centre of the arc
     */
    public int getCirclePointY(int centerY, int radius) {
        // Calculate y coord of point on circle
        int pointOfAngle = getStartAngle() + ((FULL_CIRCLE / this.totalSegments)/2);
        return centerY + (int)(radius*Math.sin(pointOfAngle * (Math.PI/180)));
    }

    public int getValue() {
        return this.value;
    }

    public Paint getPaint() {
        return this.paint;
    }
}
