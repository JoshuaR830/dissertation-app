package com.joshuarichardson.fivewaystowellbeing.ui.surveys;

//@HiltAndroidTest
//@UninstallModules(WellbeingDatabaseModule.class)
public class ComplexSurveysShouldBeSavedToTheDatabase {
    // ToDo - implement the tests for testing questions
//    SurveyResponseDao surveyDao;
//
//    @Rule
//    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();
//
//    @Rule
//    public ActivityScenarioRule<AnswerSurveyActivity> answerSurveyActivity = new ActivityScenarioRule<>(AnswerSurveyActivity.class);
//
//    @Rule
//    public HiltAndroidRule hiltTest = new HiltAndroidRule(this);
//
//    @Module
//    @InstallIn(ApplicationComponent.class)
//    public class TestWellbeingDatabaseModule {
//        @Provides
//        public WellbeingDatabase provideDatabaseService(@ApplicationContext Context context) {
//            WellbeingDatabase mockWellbeingDatabase = mock(WellbeingDatabase.class);
//            ActivityRecordDao activityDao = mock(ActivityRecordDao.class);
//
//            ArrayList<ActivityRecord> activityList = new ArrayList<>();
//            activityList.add(new ActivityRecord("Activity", 2000, 736284628, ActivityType.APP));
//            when(activityDao.getAllActivitiesNotLive()).thenReturn(activityList);
//
//            ComplexSurveysShouldBeSavedToTheDatabase.this.surveyDao = mock(SurveyResponseDao.class);
//
//            when(mockWellbeingDatabase.surveyResponseDao()).thenReturn(ComplexSurveysShouldBeSavedToTheDatabase.this.surveyDao);
//            when(mockWellbeingDatabase.activityRecordDao()).thenReturn(activityDao);
//
//            return mockWellbeingDatabase;
//        }
//    }
//
//    @Test
//    public void onSubmit_SurveysShouldBeSavedToDatabase() {
//        onView(allOf(withId(R.id.text_input), isDescendantOfA(withId(0)))).perform(scrollTo(), typeText("Title"), closeSoftKeyboard());
//        onView(allOf(withId(R.id.text_input), isDescendantOfA(withId(1)))).perform(scrollTo(), typeText("Description"), closeSoftKeyboard());
//        onView(withId(2)).perform(scrollTo(), click());
//
//        onData(instanceOf(String.class))
//                .inRoot(RootMatchers.isPlatformPopup())
//                .atPosition(0)
//                .perform(scrollTo(), click());
//
//        onView(withId(3)).perform(scrollTo(), click());
//
//        onData(instanceOf(String.class))
//                .inRoot(RootMatchers.isPlatformPopup())
//                .atPosition(0)
//                .perform(scrollTo(), click());
//
//        onView(withId(R.id.submitButton))
//                .perform(click());
//
//        verify(this.surveyDao, times(1))
//                .insert(any(SurveyResponse.class));
//    }
}
