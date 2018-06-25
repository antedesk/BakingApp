package it.antedesk.bakingapp;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

/**
 * Created by Antedesk on 19/06/2018.
 */

@RunWith(AndroidJUnit4.class)
public class HomeActivityBasicTest {
    public static final String INGREDIENTS_TITLE = "Show Ingredients";
    public static final String RECIPE_NUTELLA_PIE = "Nutella Pie";
    public static final String RECIPE_BROWNIES = "Brownies";
    public static final String RECIPE_YELLOW_CAKE = "Yellow Cake";
    private IdlingResource mIdlingResource;

    @Rule public ActivityTestRule<HomeActivity> mActivityTestRule =
            new ActivityTestRule<>(HomeActivity.class);

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);

    }

    @Test
    public void clickOnRecipeCardNutellaPie_OpensRecipeDetailActivity(){
        onView(withId(R.id.recipes_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.show_ingredients)).check(matches(withText(INGREDIENTS_TITLE)));
    }

    @Test
    public void clickOnRecipeCardYellowCake_OpensRecipeDetailActivity(){
        onView(withId(R.id.recipes_rv))
                .perform(RecyclerViewActions.actionOnItemAtPosition(3, scrollTo()));
        onView(withText(RECIPE_YELLOW_CAKE)).perform(click());

        onView(withId(R.id.show_ingredients)).check(matches(withText(INGREDIENTS_TITLE)));
        onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class)))).check(matches(withText(RECIPE_YELLOW_CAKE)));
    }

    @Test
    public void recipeIngredients_OpensIngredients(){
        onView(withId(R.id.recipes_rv))
                .perform(RecyclerViewActions.actionOnItemAtPosition(3, scrollTo()));
        onView(withText(RECIPE_BROWNIES)).perform(click());

        onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class)))).check(matches(withText(RECIPE_BROWNIES)));

        onView(withId(R.id.show_ingredients)).check(matches(withText(R.string.show_ingredients)));
        onView(withId(R.id.show_ingredients)).perform(click());

        onView(withId(R.id.ingredient_list)).perform(RecyclerViewActions.scrollToPosition(8));
        onView(withText("salt")).check(matches(isDisplayed()));
    }


    @Test
    public void recipeStepsDetails_OpensDetails(){
        onView(withId(R.id.recipes_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(2, scrollTo()));
        onView(withText(RECIPE_NUTELLA_PIE)).perform(click());

        onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class)))).check(matches(withText(RECIPE_NUTELLA_PIE)));

        onView(withId(R.id.show_ingredients)).check(matches(withText(R.string.show_ingredients)));

        onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(4, click()));

        onView(withId(R.id.player_view)).check(matches(isDisplayed()));
        onView(withId(R.id.step_title)).check(matches(isDisplayed()));
        onView(withId(R.id.step_title)).check(matches(withText("Start filling prep")));

        onView(withId(R.id.step_description_tv)).check(matches(isDisplayed()));

    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }

}
