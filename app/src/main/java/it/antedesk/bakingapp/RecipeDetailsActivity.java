package it.antedesk.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.antedesk.bakingapp.fragment.IngredientFragment;
import it.antedesk.bakingapp.fragment.StepDetailsFragment;
import it.antedesk.bakingapp.fragment.StepFragment;
import it.antedesk.bakingapp.model.Ingredient;
import it.antedesk.bakingapp.model.Recipe;
import it.antedesk.bakingapp.model.Step;
import it.antedesk.bakingapp.widget.IngrendientsService;

import static it.antedesk.bakingapp.utils.SupportVariablesDefinition.CURRENT_STEP;
import static it.antedesk.bakingapp.utils.SupportVariablesDefinition.RECIPES_INGREDIENT;
import static it.antedesk.bakingapp.utils.SupportVariablesDefinition.RECIPES_STEPS;
import static it.antedesk.bakingapp.utils.SupportVariablesDefinition.SELECTED_RECIPE;

public class RecipeDetailsActivity extends BaseActivity implements StepFragment.OnListFragmentInteractionListener,
        IngredientFragment.OnListFragmentInteractionListener {
    public static final String STEP_MASTER_FRAGMENT = "STEP_MASTER_FRAGMENT";
    public static final String STEP_DETAIL_FRAGMENT = "STEP_DETAIL_FRAGMENT";
    public static final String INGREDIENT_FRAGMENT = "INGREDIENT_FRAGMENT";

    Recipe mRecipe;
    Step currentStep;
    String mLastSinglePaneFragment;
    public static String recipeName = "";
    public static List<Ingredient> ingredients = new ArrayList<Ingredient>();

    boolean mDualPane = false;
    final String lastSinglePaneFragment = "lastSinglePaneFragment";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        initFont();

        Intent intent = getIntent();
        // checking if it is null, if so close the activity
        if (intent == null) {
            closeOnError();
        }
        // retriving the movie form intent
        mRecipe = intent.getParcelableExtra(SELECTED_RECIPE);
        Log.d(SELECTED_RECIPE, mRecipe.toString());
        if (mRecipe == null) {
            closeOnError();
        }

        recipeName = mRecipe.getName();
        setTitle(recipeName);

        mDualPane = findViewById(R.id.steps_details_container)!=null;

        if (savedInstanceState!=null) {
            currentStep = savedInstanceState.getParcelable(CURRENT_STEP);
        }

        FragmentManager fm = getSupportFragmentManager();

        if (!mDualPane && fm.findFragmentById(R.id.steps_container)==null) {
            StepFragment masterFragment = getDetatchedMasterFragment(false);
            fm.beginTransaction().replace(R.id.steps_container, masterFragment, STEP_MASTER_FRAGMENT).commit();
            if (mLastSinglePaneFragment==STEP_DETAIL_FRAGMENT || mLastSinglePaneFragment == INGREDIENT_FRAGMENT) {
                openSinglePaneDetailFragment();
            }
        }
        if (mDualPane && fm.findFragmentById(R.id.steps_list_container)==null) {
            StepFragment masterFragment = getDetatchedMasterFragment(true);
            fm.beginTransaction().replace(R.id.steps_list_container, masterFragment, STEP_MASTER_FRAGMENT).commit();
        }
        if (mDualPane && fm.findFragmentById(R.id.steps_details_container)==null) {
            StepDetailsFragment detailFragment = getDetatchedDetailFragment();
            fm.beginTransaction().replace(R.id.steps_details_container, detailFragment, STEP_DETAIL_FRAGMENT).commit();
        }

        Log.d("TestFrag","onCreate - #frag = "+fm.getBackStackEntryCount());
    }

    private void closeOnError() {
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.recipe_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        boolean isRecipeAdded = false;
        if (itemId == R.id.action_add) {
            ingredients = mRecipe.getIngredients();
            isRecipeAdded = IngrendientsService.startActionUpdateRecipeIngredientsList(this);
            int textId2Display = isRecipeAdded ? R.string.text_recipe_ingredients_added : R.string.text_recipe_ingredients_not_added;
            Toast.makeText(this,getString(textId2Display),Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        Log.d("TestFrag","onBackPressed");
        FragmentManager fragmentManager = getSupportFragmentManager();
        Log.d("TestFrag","onBackPressed - #frag = "+fragmentManager.getBackStackEntryCount());

        Log.d("TestFrag","onBackPressed - mDualPane is " + mDualPane);
        if (!mDualPane && fragmentManager.getBackStackEntryCount() > 0) {
            Log.d("TestFrag","onBackPressed - entry name at pos 0 " + fragmentManager.getBackStackEntryAt(0).getName());
            fragmentManager.popBackStack();
            mLastSinglePaneFragment = STEP_MASTER_FRAGMENT;
        } else {
            Log.d("TestFrag","onBackPressed - else");
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(!mDualPane && currentStep!=null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Log.d("TestFrag","onSaveInstanceState - #frag = "+fragmentManager.getBackStackEntryCount());
            outState.putParcelable(CURRENT_STEP, currentStep);
        }
    }

    @Override
    public void onListFragmentInteraction(Step item) {
        currentStep = item;
        FragmentManager fragmentManager = getSupportFragmentManager();
        StepDetailsFragment stepFragment = StepDetailsFragment.newInstance(item);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().replace(
                mDualPane ? R.id.steps_details_container : R.id.steps_container, stepFragment);
        if(!mDualPane)
            fragmentTransaction.addToBackStack("detail");
        fragmentTransaction.commit();
        Log.d("TestFrag","onListFragmentInteraction - #frag = "+fragmentManager.getBackStackEntryCount());
    }

    private StepFragment getDetatchedMasterFragment(boolean popBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        StepFragment masterFragment = (StepFragment) getSupportFragmentManager().findFragmentByTag(STEP_MASTER_FRAGMENT);
        if (masterFragment == null) {
            masterFragment = new StepFragment();
            Bundle stepsFragBundle = new Bundle();
            stepsFragBundle.putParcelableArrayList(RECIPES_STEPS, (ArrayList<Step>) mRecipe.getSteps());
            masterFragment.setArguments(stepsFragBundle);
        } else {
            if (popBackStack) {
                fm.popBackStack("master", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            fm.beginTransaction().remove(masterFragment).commit();
            fm.executePendingTransactions();
        }
        return masterFragment;
    }

    private StepDetailsFragment getDetatchedDetailFragment() {
        FragmentManager fm = getSupportFragmentManager();
        StepDetailsFragment detailFragment = (StepDetailsFragment) getSupportFragmentManager().findFragmentByTag(STEP_DETAIL_FRAGMENT);
        if (detailFragment == null) {
            currentStep = currentStep==null? mRecipe.getSteps().get(0): currentStep;
            detailFragment = StepDetailsFragment.newInstance(currentStep);
        }
        else {
            fm.beginTransaction().remove(detailFragment).commit();
            fm.executePendingTransactions();
        }
        return detailFragment;
    }

    private IngredientFragment getDetatchedIngredientFragment() {
        FragmentManager fm = getSupportFragmentManager();
        IngredientFragment ingredientFragment = (IngredientFragment) getSupportFragmentManager().findFragmentByTag(INGREDIENT_FRAGMENT);
        if (ingredientFragment == null) {
            ingredientFragment = IngredientFragment.newInstance(1);
            Bundle ingredientsFragBundle = new Bundle();
            ingredientsFragBundle.putParcelableArrayList(RECIPES_INGREDIENT, (ArrayList<Ingredient>) mRecipe.getIngredients());
            ingredientFragment.setArguments(ingredientsFragBundle);
        }
        else {
            fm.beginTransaction().remove(ingredientFragment).commit();
            fm.executePendingTransactions();
        }
        return ingredientFragment;
    }

    private void openSinglePaneDetailFragment() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack("detail", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        StepDetailsFragment detailFragment = getDetatchedDetailFragment();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.steps_container, detailFragment, STEP_DETAIL_FRAGMENT);
        fragmentTransaction.commit();
        mLastSinglePaneFragment = STEP_DETAIL_FRAGMENT;
    }

    public void showIngredients(View view) {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack("ingredient", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        IngredientFragment ingredientFragment = getDetatchedIngredientFragment();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        int container = mDualPane ? R.id.steps_details_container : R.id.steps_container;
        fragmentTransaction.replace(container, ingredientFragment, INGREDIENT_FRAGMENT);
        if(!mDualPane) fragmentTransaction.addToBackStack("ingredient");
        fragmentTransaction.commit();
        mLastSinglePaneFragment = INGREDIENT_FRAGMENT;
    }

    @Override
    public void onListFragmentInteraction(Ingredient ingredient) { /*DO NOTHING*/}
}
