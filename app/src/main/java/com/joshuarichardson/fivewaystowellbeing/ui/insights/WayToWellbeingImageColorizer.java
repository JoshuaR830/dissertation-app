package com.joshuarichardson.fivewaystowellbeing.ui.insights;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.widget.ImageView;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.WellbeingHelper;

import androidx.core.content.ContextCompat;

public class WayToWellbeingImageColorizer {
    public static void colorize(Context context, ImageView typeImage, WaysToWellbeing wayToWellbeing) {
        GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.wellbeing_type_indicator);
        if (drawable != null) {
            drawable = (GradientDrawable) drawable.mutate();
            drawable.setColor(WellbeingHelper.getColor(context, wayToWellbeing.toString()));
            typeImage.setImageDrawable(drawable);
        }
    }
}