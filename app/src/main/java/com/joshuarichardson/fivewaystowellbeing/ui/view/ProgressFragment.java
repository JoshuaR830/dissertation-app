package com.joshuarichardson.fivewaystowellbeing.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.TimeHelper;
import com.joshuarichardson.fivewaystowellbeing.hilt.modules.WellbeingDatabaseModule;
import com.joshuarichardson.fivewaystowellbeing.storage.LimitedRawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.RawSurveyData;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingDatabase;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphItem;
import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphValueHelper;
import com.joshuarichardson.fivewaystowellbeing.storage.dao.SurveyResponseDao;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseActivityRecord;
import com.joshuarichardson.fivewaystowellbeing.surveys.Passtime;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyDataHelper;
import com.joshuarichardson.fivewaystowellbeing.surveys.SurveyDay;
import com.joshuarichardson.fivewaystowellbeing.ui.graphs.WellbeingGraphView;
import com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys.ActivityViewHelper;
import com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys.WellbeingRecordInsertionHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import dagger.hilt.android.AndroidEntryPoint;

import static com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing.UNASSIGNED;

@AndroidEntryPoint
public class ProgressFragment extends Fragment {

    private static final int ACTIVITY_REQUEST_CODE = 1;
    @Inject
    WellbeingDatabase db;

    private Observer<List<SurveyResponse>> surveyResponsesObserver;
    private LiveData<List<SurveyResponse>> surveyResponseItems;
    private Observer<List<WellbeingGraphItem>> wholeGraphUpdate;
    private LiveData<List<WellbeingGraphItem>> graphUpdateValues;
    private long surveyId;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parentView, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progress, parentView, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SurveyResponseDao surveyDao = db.surveyResponseDao();

        long time = new Date().getTime();
        Log.d("Time", String.valueOf(time));
        long thisMorning = TimeHelper.getStartOfDay(time);
        long tonight = TimeHelper.getEndOfDay(time);
        Log.d("Time morning", String.valueOf(thisMorning));

        CardView container = view.findViewById(R.id.graph_card);
        FrameLayout canvasContainer = container.findViewById(R.id.graph_card_container);

        WellbeingGraphView graphView = new WellbeingGraphView(getActivity(), 600, new WellbeingGraphValueHelper(0, 0,0 ,0, 0));

        this.wholeGraphUpdate = graphValues -> {
            WellbeingGraphValueHelper values = WellbeingGraphValueHelper.getWellbeingGraphValues(graphValues);
            graphView.updateValues(values);
        };

        this.graphUpdateValues = this.db.wellbeingQuestionDao().getWaysToWellbeingBetweenTimes(thisMorning, tonight);
        this.graphUpdateValues.observe(requireActivity(), this.wholeGraphUpdate);
        canvasContainer.addView(graphView);

        // Calculate time for today midnight
        Calendar calendar = GregorianCalendar.getInstance();
        long epochSecondsToday = TimeHelper.getStartOfDay(calendar.getTimeInMillis());
        long epochSecondsTonight = TimeHelper.getEndOfDay(calendar.getTimeInMillis());

        Button addActivityButton = requireActivity().findViewById(R.id.add_activity_button);
        addActivityButton.setOnClickListener(v -> {
            Intent activityIntent = new Intent(requireActivity(), ViewPassTimesActivity.class);
            startActivityForResult(activityIntent, ACTIVITY_REQUEST_CODE);
        });

        this.surveyResponsesObserver = surveys -> {
            if (surveys.size() == 0) {
                long startTime = TimeHelper.getStartOfDay(new Date().getTime());
                // Adding the new survey should trigger the live data to update
                WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                    this.db.surveyResponseDao().insert(new SurveyResponse(startTime, UNASSIGNED, "", ""));
                });

                return;
            }

            this.surveyId = surveys.get(0).getSurveyResponseId();
            WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                List<RawSurveyData> rawSurveyDataList = this.db.wellbeingRecordDao().getDataBySurvey(this.surveyId);
                if (rawSurveyDataList == null || rawSurveyDataList.size() == 0) {
                    // This converts a limited response to an entire RawSurveyData response
                    List<LimitedRawSurveyData> limitedData = this.db.wellbeingRecordDao().getLimitedDataBySurvey(this.surveyId);
                    rawSurveyDataList = LimitedRawSurveyData.convertToRawSurveyDataList(limitedData);
                }

                SurveyDay surveyData = SurveyDataHelper.transform(rawSurveyDataList);
                ActivityViewHelper.displaySurveyItems(requireActivity(), surveyData, this.db);
            });
        };

        // Epoch seconds give a 24 hour time frame - any new surveys added will get updated live (using now meant that future surveys today didn't get shown)
        this.surveyResponseItems = surveyDao.getSurveyResponsesByTimestampRange(epochSecondsToday, epochSecondsTonight);
        this.surveyResponseItems.observe(requireActivity(), this.surveyResponsesObserver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (data == null) {
            return;
        }

        if (requestCode == ACTIVITY_REQUEST_CODE) {
            if (data.getExtras() == null || !data.hasExtra("activity_id")) {
                return;
            }

            long activityId = data.getExtras().getLong("activity_id", -1);
            String activityType = data.getExtras().getString("activity_type", "");
            String activityName = data.getExtras().getString("activity_name", "");

            if (activityId == -1) {
                return;
            }

            // Sequence number based on number of children in the linear layout
            LinearLayout passtimeContainer = requireActivity().findViewById(R.id.survey_item_container);

            int sequenceNumber = passtimeContainer.getChildCount() + 1;

            WellbeingDatabaseModule.databaseWriteExecutor.execute(() -> {
                long activitySurveyId = this.db.surveyResponseActivityRecordDao().insert(new SurveyResponseActivityRecord(surveyId, activityId, sequenceNumber, "", -1, -1));
                Passtime passtime = new Passtime(activityName, "", activityType);
                Passtime updatedPasstime = WellbeingRecordInsertionHelper.addPasstimeQuestions(this.db, activitySurveyId, activityType, passtime);

                ActivityViewHelper.createPasstimeItem(requireActivity(), passtimeContainer, updatedPasstime, this.db);
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.surveyResponseItems.removeObserver(this.surveyResponsesObserver);
        this.graphUpdateValues.removeObserver(this.wholeGraphUpdate);
    }
}
