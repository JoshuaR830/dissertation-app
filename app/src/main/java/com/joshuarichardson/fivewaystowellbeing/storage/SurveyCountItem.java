package com.joshuarichardson.fivewaystowellbeing.storage;

import com.joshuarichardson.fivewaystowellbeing.R;

/**
 * For keeping track of number of emotions and the total emotion value
 */
public class SurveyCountItem {
    int emotionCount;
    int totalValue;

    public SurveyCountItem(int emotionCount, int totalValue) {
        this.emotionCount = emotionCount;
        this.totalValue = totalValue;
    }

    /**
     * Get the average emotion value
     *
     * @return The average based on number of non-zero emotions
     */
    private int calculateAverage() {
        if(totalValue == 0 || emotionCount == 0) {
            return 0;
        }

        return Math.round((float)totalValue/(float)emotionCount);
    }

    /**
     * Get the correct emotion for the average emotions
     *
     * @return The icon resource and colour to display
     */
    public SentimentItem getResourcesForAverage() {
        // Get the image and colour resource for the most appropriate emotion
        switch(calculateAverage()) {
            case 1:
                return new SentimentItem(R.drawable.sentiment_worst, R.color.sentiment_worst);
            case 2:
                return new SentimentItem(R.drawable.sentiment_bad, R.color.sentiment_bad);
            case 3:
                return new SentimentItem(R.drawable.sentiment_neutral, R.color.sentiment_neutral);
            case 4:
                return new SentimentItem(R.drawable.sentiment_good, R.color.sentiment_good);
            case 5:
                return new SentimentItem(R.drawable.sentiment_best, R.color.sentiment_best);
            default:
                return new SentimentItem(R.drawable.progress_survey_today, R.color.colorSilver);
        }
    }
}
