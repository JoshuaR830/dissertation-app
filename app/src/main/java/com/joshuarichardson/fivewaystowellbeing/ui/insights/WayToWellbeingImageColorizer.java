package com.joshuarichardson.fivewaystowellbeing.ui.insights;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.joshuarichardson.fivewaystowellbeing.DisplayHelper;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.WellbeingHelper;

import androidx.core.content.ContextCompat;

/**
 * A helper class to colorize drawables to match the ways to wellbeing
 */
public class WayToWellbeingImageColorizer {
    /**
     * Change the colour of the wellbeing type indicator drawable.
     * Set the image view resource to the new colorized drawable
     *
     * @param context Application context
     * @param typeImage The image view to change
     * @param wayToWellbeing The way to wellbeing to influence the color
     */
    public static void colorize(Context context, ImageView typeImage, WaysToWellbeing wayToWellbeing) {
        GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.wellbeing_type_indicator);
        if (drawable != null) {
            drawable = (GradientDrawable) drawable.mutate();
            drawable.setColor(WellbeingHelper.getColor(context, wayToWellbeing.toString()));
            typeImage.setImageDrawable(drawable);
        }
    }

    /**
     * Change the colour of the frame drawable.
     * Set the background image to be a new colorized drawable
     *
     * @param context Application context
     * @param imageFrame The frame to change
     * @param wayToWellbeing The way to wellbeing to influence the color
     */
    public static void colorizeFrame(Context context, FrameLayout imageFrame, WaysToWellbeing wayToWellbeing) {
        GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.frame_circle);
        if (drawable != null) {
            drawable = (GradientDrawable) drawable.mutate();
            drawable.setStroke(DisplayHelper.dpToPx(context, 4), WellbeingHelper.getColor(context, wayToWellbeing.toString()));
            imageFrame.setBackground(drawable);
        }
    }
}
