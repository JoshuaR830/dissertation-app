package com.joshuarichardson.fivewaystowellbeing.ui.graphs;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphValueHelper;

import java.util.Locale;

public class WellbeingGraphView extends View implements ValueAnimator.AnimatorUpdateListener {
    private Bitmap bitmap;
    private Canvas canvas;
    private float multiplier;
    private int width;
    private int height;
    private int graphSize;
    private int canvasSize;
    private boolean shouldShowNumbers;
    private final Rect textBounds = new Rect();
    Paint outline = new Paint();
    ValueAnimator animator;

    WaysToWellbeingGraphValues give;
    WaysToWellbeingGraphValues connect;
    WaysToWellbeingGraphValues beActive;
    WaysToWellbeingGraphValues keepLearning;
    WaysToWellbeingGraphValues takeNotice;

    public WellbeingGraphView(Context context, int graphSize, WellbeingGraphValueHelper wayToWellbeingValues, boolean shouldShowNumbers) {
        super(context);
        setWellbeingGraphView(context, graphSize, wayToWellbeingValues, shouldShowNumbers);
    }

    public WellbeingGraphView(Context context, int graphSize, WellbeingGraphValueHelper wayToWellbeingValues) {
        super(context);
        setWellbeingGraphView(context, graphSize, wayToWellbeingValues, false);
    }

    private void setWellbeingGraphView(Context context, int canvasSize, WellbeingGraphValueHelper wayToWellbeingValues, boolean shouldShowNumbers) {
        this.shouldShowNumbers = shouldShowNumbers;
        this.canvasSize = canvasSize;

        if(shouldShowNumbers) {
            this.graphSize = this.canvasSize - 200;
        } else {
            this.graphSize = this.canvasSize;
        }

        // Instantiate each segment for the graph
        this.give = new WaysToWellbeingGraphValues(wayToWellbeingValues.getGiveValue(), getResources().getColor(R.color.way_to_wellbeing_give, context.getTheme()), 5, 0);
        this.connect = new WaysToWellbeingGraphValues(wayToWellbeingValues.getConnectValue(), getResources().getColor(R.color.way_to_wellbeing_connect, context.getTheme()), 5, 1);
        this.beActive = new WaysToWellbeingGraphValues(wayToWellbeingValues.getBeActiveValue(), getResources().getColor(R.color.way_to_wellbeing_be_active, context.getTheme()), 5, 2);
        this.keepLearning = new WaysToWellbeingGraphValues(wayToWellbeingValues.getKeepLearningValue(), getResources().getColor(R.color.way_to_wellbeing_keep_learning, context.getTheme()), 5, 3);
        this.takeNotice = new WaysToWellbeingGraphValues(wayToWellbeingValues.getTakeNoticeValue(), getResources().getColor(R.color.way_to_wellbeing_take_notice, context.getTheme()), 5, 4);

        // Set the correct size for the layout
        this.setLayoutParams(new FrameLayout.LayoutParams(canvasSize, canvasSize, Gravity.CENTER));

        // Create the bitmap
        bitmap = Bitmap.createBitmap(canvasSize, canvasSize, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);

        // Prepare an outline for the circle
        this.outline.setStyle(Paint.Style.STROKE);
        this.outline.setStrokeWidth(2);
        TypedValue colorValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.lineColor, colorValue, true);
        this.outline.setColor(colorValue.data);

        // Reference: https://stackoverflow.com/a/54327730/13496270
        // Create an animator which will make the graph enter with a glorious fanfare
        animator = ValueAnimator.ofInt(0, graphSize/2);
        animator.setDuration(400);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(this);
        animator.start();
    }


    // Reference: https://stackoverflow.com/a/24969713/13496270
    public void drawTextCentered(Canvas canvas, WaysToWellbeingGraphValues graph, int centerX, int centerY, int radius) {
        String text = String.format(Locale.getDefault(), "%d%%", graph.getValue());

        graph.getPaint().getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, graph.getCirclePointX(centerX, radius) - textBounds.exactCenterX(), graph.getCirclePointY(centerY, radius) - textBounds.exactCenterY(), graph.getPaint());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (this.shouldShowNumbers) {
            int centerX = (int) (this.canvasSize/2f);
            int centerY = (int) (this.canvasSize/2f);
            int radius = this.graphSize/2;

            canvas.drawLine(centerX, centerY, this.give.getCirclePointX(centerX, radius), this.give.getCirclePointY(centerY, radius), this.give.getPaint());
            canvas.drawLine(centerX, centerY, this.connect.getCirclePointX(centerX, radius), this.connect.getCirclePointY(centerY, radius), this.connect.getPaint());
            canvas.drawLine(centerX, centerY, this.beActive.getCirclePointX(centerX, radius), this.beActive.getCirclePointY(centerY, radius), this.beActive.getPaint());
            canvas.drawLine(centerX, centerY, this.keepLearning.getCirclePointX(centerX, radius), this.keepLearning.getCirclePointY(centerY, radius), this.keepLearning.getPaint());
            canvas.drawLine(centerX, centerY, this.takeNotice.getCirclePointX(centerX, radius), this.takeNotice.getCirclePointY(centerY, radius), this.takeNotice.getPaint());

            radius = (this.canvasSize/2) - 40;

            drawTextCentered(canvas, this.give, centerX, centerY, radius);
            drawTextCentered(canvas, this.connect, centerX, centerY, radius);
            drawTextCentered(canvas, this.beActive, centerX, centerY, radius);
            drawTextCentered(canvas, this.keepLearning, centerX, centerY, radius);
            drawTextCentered(canvas, this.takeNotice, centerX, centerY, radius);
        }

        // Create the arcs on the canvas
        canvas.drawArc(this.give.getShape(), this.give.getStartAngle(), this.give.getArcLength(), this.give.getUseCenter(), this.give.getPaint());
        canvas.drawArc(this.connect.getShape(), this.connect.getStartAngle(), this.connect.getArcLength(), this.connect.getUseCenter(), this.connect.getPaint());
        canvas.drawArc(this.beActive.getShape(), this.beActive.getStartAngle(), this.beActive.getArcLength(), this.beActive.getUseCenter(), this.beActive.getPaint());
        canvas.drawArc(this.keepLearning.getShape(), this.keepLearning.getStartAngle(), this.keepLearning.getArcLength(), this.keepLearning.getUseCenter(), this.keepLearning.getPaint());
        canvas.drawArc(this.takeNotice.getShape(), this.takeNotice.getStartAngle(), this.takeNotice.getArcLength(), this.takeNotice.getUseCenter(), this.takeNotice.getPaint());
        canvas.drawCircle(this.width, this.height, this.graphSize/2f, this.outline);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        int value = (int) animation.getAnimatedValue();
        this.multiplier = (value/100f);

        // Reference for centering - https://gamedev.stackexchange.com/questions/95433/how-can-i-draw-arc-in-center-of-the-screen-on-canvas-in-android/103662
        this.height = this.canvas.getHeight() / 2;
        this.width = this.canvas.getWidth() / 2;

        // Update the size
        this.give.updateSize(this.multiplier, this.height, this.width);
        this.connect.updateSize(this.multiplier, this.height, this.width);
        this.beActive.updateSize(this.multiplier, this.height, this.width);
        this.keepLearning.updateSize(this.multiplier, this.height, this.width);
        this.takeNotice.updateSize(this.multiplier, this.height, this.width);

        // Remove the existing drawing and redraw it
        this.invalidate();
    }

    public void updateValues(WellbeingGraphValueHelper updatedGraphValues) {
        this.give.updateValue(updatedGraphValues.getGiveValue(), this.multiplier, this.height, this.width);
        this.connect.updateValue(updatedGraphValues.getConnectValue(), this.multiplier, this.height, this.width);
        this.beActive.updateValue(updatedGraphValues.getBeActiveValue(), this.multiplier, this.height, this.width);
        this.keepLearning.updateValue(updatedGraphValues.getKeepLearningValue(), this.multiplier, this.height, this.width);
        this.takeNotice.updateValue(updatedGraphValues.getTakeNoticeValue(), this.multiplier, this.height, this.width);
        animator.start();

        this.invalidate();
    }

    public void highlightBar(WaysToWellbeing itemToHighlight) {

        this.connect.updateColor(getResources().getColor(R.color.translucent_way_to_wellbeing_connect, getContext().getTheme()));
        this.beActive.updateColor(getResources().getColor(R.color.translucent_way_to_wellbeing_be_active, getContext().getTheme()));
        this.keepLearning.updateColor(getResources().getColor(R.color.translucent_way_to_wellbeing_keep_learning, getContext().getTheme()));
        this.takeNotice.updateColor(getResources().getColor(R.color.translucent_way_to_wellbeing_take_notice, getContext().getTheme()));
        this.give.updateColor(getResources().getColor(R.color.translucent_way_to_wellbeing_give, getContext().getTheme()));

        switch(itemToHighlight) {
            case CONNECT:
                this.connect.updateColor(getResources().getColor(R.color.way_to_wellbeing_connect, getContext().getTheme()));
                break;
            case BE_ACTIVE:
                this.beActive.updateColor(getResources().getColor(R.color.way_to_wellbeing_be_active, getContext().getTheme()));
                break;
            case KEEP_LEARNING:
                this.keepLearning.updateColor(getResources().getColor(R.color.way_to_wellbeing_keep_learning, getContext().getTheme()));
                break;
            case TAKE_NOTICE:
                this.takeNotice.updateColor(getResources().getColor(R.color.way_to_wellbeing_take_notice, getContext().getTheme()));
                break;
            case GIVE:
                this.give.updateColor(getResources().getColor(R.color.way_to_wellbeing_give, getContext().getTheme()));
                break;
            default:
                return;
        }

        this.invalidate();
    }

    public void resetColors() {
        this.connect.updateColor(getResources().getColor(R.color.way_to_wellbeing_connect, getContext().getTheme()));
        this.beActive.updateColor(getResources().getColor(R.color.way_to_wellbeing_be_active, getContext().getTheme()));
        this.keepLearning.updateColor(getResources().getColor(R.color.way_to_wellbeing_keep_learning, getContext().getTheme()));
        this.takeNotice.updateColor(getResources().getColor(R.color.way_to_wellbeing_take_notice, getContext().getTheme()));
        this.give.updateColor(getResources().getColor(R.color.way_to_wellbeing_give, getContext().getTheme()));

        this.invalidate();
    }
}
