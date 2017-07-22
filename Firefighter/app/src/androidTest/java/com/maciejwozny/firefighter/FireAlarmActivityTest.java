package com.maciejwozny.firefighter;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by maciek on 21.07.17.
 */
@RunWith(AndroidJUnit4.class)
public class FireAlarmActivityTest {
    @Rule
    public final ActivityTestRule<FireAlarmActivity> fireAlarmActivity =
            new ActivityTestRule<>(FireAlarmActivity.class);


    @Test
    public void shouldShowAlertWithAskingAboutJoiningToAction() throws Throwable {
        onView(withText(R.string.doYouJoinToAction)).check(matches(isDisplayed()));
        onView(withText(R.string.yes)).check(matches(isDisplayed()));
        onView(withText(R.string.no)).check(matches(isDisplayed()));

        onView(withText(R.string.yes)).perform(click());

        onView(withText(R.string.confirmJoining)).check(matches(isDisplayed()));
    }
}
