package com.hassanabid.fyberapiapp;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import com.hassanabid.fyberapiapp.data.Constants;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class OfferListActivityTest extends InstrumentationTestCase {


    @Rule
    public ActivityTestRule<OfferListActivity> mActivityRule =
            new ActivityTestRule<>(OfferListActivity.class, true, false);
    private MockWebServer server;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        server = new MockWebServer();
        server.start();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        Constants.API_URL = server.url("/").toString();
    }

    @Test
    public void testOfferIsShown() throws Exception {
        String fileName = "offers_200_ok_response.json";
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);

        onView(withId(R.id.emptyList)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withText("Download and START")).check(matches(isDisplayed()));
    }


    @Test
    public void testMessageShowsWhenError() throws Exception {
        String fileName = "offers_401_invalid.json";

        server.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), fileName)));

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);

        onView(withId(R.id.emptyList)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withText("There are no offers fetched yet. Please click the + button to request!")).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }
}
