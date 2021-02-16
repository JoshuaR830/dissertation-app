//ToDo - look further into this when working on notifications
//package com.joshuarichardson.fivewaystowellbeing.analytics;
//
//import android.content.Context;
//
//import com.joshuarichardson.fivewaystowellbeing.TimeHelper;
//
//import java.util.Calendar;
//import java.util.GregorianCalendar;
//
//import androidx.annotation.NonNull;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;
//
//public class DailyWaysToWellbeingWorker extends Worker {
//    public DailyWaysToWellbeingWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//    }
//
//    @NonNull
//    @Override
//    public Result doWork() {
//
//
//        // ToDo get the current time
//        // ToDo get yesterday start time
//
//        // This gets the time for yesterday
//        Calendar cal = GregorianCalendar.getInstance();
//        cal.add(Calendar.DATE, -1);
//
//        TimeHelper.getStartOfDay(cal.getTimeInMillis());
//        TimeHelper.getEndOfDay(cal.getTimeInMillis());
//        // ToDo get yesterday end time
//
//        return null;
//    }
//}
