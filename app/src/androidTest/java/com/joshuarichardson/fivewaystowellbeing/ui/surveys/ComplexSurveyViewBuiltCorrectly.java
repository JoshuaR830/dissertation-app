package com.joshuarichardson.fivewaystowellbeing.ui.surveys;

//@HiltAndroidTest
//@UninstallModules(WellbeingDatabaseModule.class)
public class ComplexSurveyViewBuiltCorrectly {
    // ToDo - this should test the main question part of the surveys
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
//    public static class TestWellbeingDatabaseModule {
//        @Provides
//        public WellbeingDatabase provideDatabaseService(@ApplicationContext Context context) {
//            WellbeingDatabase mockWellbeingDatabase = mock(WellbeingDatabase.class);
//            ActivityRecordDao activityDao = mock(ActivityRecordDao.class);
//
//            MutableLiveData<List<ActivityRecord>> data = new MutableLiveData<>();
//
//            ArrayList<ActivityRecord> array = new ArrayList<>();
//            array.add(new ActivityRecord("Running", 1200, 1607960240, ActivityType.SPORT));
//            array.add(new ActivityRecord("Jumping", 1201, 1607960241, ActivityType.SPORT));
//            array.add(new ActivityRecord("Fishing", 1202, 1607960242, ActivityType.SPORT));
//
//            data.setValue(array);
//
//            when(activityDao.getAllActivities()).thenReturn(data);
//            when(activityDao.getAllActivitiesNotLive()).thenReturn(array);
//
//            when(mockWellbeingDatabase.activityRecordDao()).thenReturn(activityDao);
//
//            return mockWellbeingDatabase;
//        }
//    }
//
//    @Before
//    public void setup() {
//        hiltTest.inject();
//    }
//
//    @Test
//    public void surveyTitle_ShouldAllowTextEntry() {
//         onView(allOf(withId(R.id.question_title), isDescendantOfA(withId(0))))
//            .check(matches(withText("Add a title")));
//
//        onView(allOf(withId(R.id.text_input_container), isDescendantOfA(withId(0))))
//            .check(matches(withMaterialHint("Add a title")));
//
//        onView(allOf(withId(R.id.text_input), isDescendantOfA(withId(0))))
//            .perform(scrollTo())
//            .check(matches(isDisplayed()))
//            .check(matches(withText("")))
//            .check(matches(withInputType(TYPE_CLASS_TEXT)))
//            .perform(typeText("Title"))
//            .check(matches(withText("Title")));
//    }
//
//    @Test
//    public void surveyDescription_ShouldAllowTextEntry() {
//        onView(allOf(withId(R.id.question_title), isDescendantOfA(withId(1))))
//            .check(matches(withText("Set a description")));
//
//        onView(allOf(withId(R.id.text_input_container), isDescendantOfA(withId(1))))
//            .check(matches(withMaterialHint("Set a description")));
//
//        onView(allOf(withId(R.id.text_input), isDescendantOfA(withId(1))))
//            .perform(scrollTo())
//            .check(matches(isDisplayed()))
//            .check(matches(withText("")))
//            .check(matches(withInputType(TYPE_CLASS_TEXT)))
//            .perform(typeText("Description"))
//            .check(matches(withText("Description")));
//    }
//
//    @Test
//    public void activityDropDownList_ShouldContainAllActivities() {
//        onView(withId(2))
//            .perform(scrollTo(), click());
//
//        // Trying to get the drop down list https://stackoverflow.com/a/45368345/13496270
//        // Get the adapter of Strings
//        // Search the popup
//        DataInteraction popup = onData(instanceOf(String.class))
//                .inRoot(RootMatchers.isPlatformPopup());
//
//        // Check the text matches
//        popup.atPosition(0)
//                .check(matches(withText("Running")));
//
//        popup.atPosition(1)
//                .check(matches(withText("Jumping")));
//
//        popup.atPosition(2)
//                .check(matches(withText("Fishing")));
//    }
//
//    @Test
//    public void moodDropDownList_ShouldContainAllMoods() {
//        onView(withId(3))
//                .perform(scrollTo(), click());
//
//        // Trying to get the drop down list https://stackoverflow.com/a/45368345/13496270
//        // Get the adapter of Strings
//        // Search the popup
//        DataInteraction popup = onData(instanceOf(String.class))
//                .inRoot(RootMatchers.isPlatformPopup());
//
//        // Check the text matches
//        popup.atPosition(0)
//                .check(matches(withText("Happy")));
//
//        popup.atPosition(1)
//                .check(matches(withText("Moderate")));
//
//        popup.atPosition(2)
//                .check(matches(withText("Sad")));
//    }
}
