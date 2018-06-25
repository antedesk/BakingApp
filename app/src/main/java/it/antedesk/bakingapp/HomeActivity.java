package it.antedesk.bakingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import it.antedesk.bakingapp.IdlingResource.SimpleIdlingResource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.antedesk.bakingapp.adapter.RecipeViewAdapter;
import it.antedesk.bakingapp.adapter.RecipeViewAdapter.RecipeViewAdapterOnClickHandler;
import it.antedesk.bakingapp.contracts.DelayerCallback;
import it.antedesk.bakingapp.model.Recipe;
import it.antedesk.bakingapp.utils.NetworkUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static it.antedesk.bakingapp.utils.SupportVariablesDefinition.HOME_ACTIVITY_LOADING;
import static it.antedesk.bakingapp.utils.SupportVariablesDefinition.RECIPES_DATASOURCE_URL;
import static it.antedesk.bakingapp.utils.SupportVariablesDefinition.SELECTED_RECIPE;

public class HomeActivity extends BaseActivity implements RecipeViewAdapterOnClickHandler, DelayerCallback {

    private List<Recipe> recipes =null;
    private OkHttpClient client = new OkHttpClient();
    private static final int DELAY_MILLIS = 3000;

    @BindView(R.id.recipes_rv) RecyclerView mRecipesRecyclerView;
    private RecipeViewAdapter mRecipesViewAdapter;

    private static final String LIST_STATE = "listState";
    private Parcelable mListState = null;

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @BindView(R.id.connection_error_layout)
    LinearLayout mConnectionErrorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        initFont();

        getIdlingResource();

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        int portraitColumns = tabletSize ? 2 : 1;
        int landscapeColumns = tabletSize ? 3 : calculateNoOfColumns(this);

        int numberOfColumns =
                getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT ? portraitColumns : landscapeColumns;

        // creating a GridLayoutManager
        GridLayoutManager mLayoutManager
                = new GridLayoutManager(this, numberOfColumns);

        // setting the mlayoutManager on mRecyclerView
        mRecipesRecyclerView.setLayoutManager(mLayoutManager);
        mRecipesRecyclerView.setHasFixedSize(true);
        mRecipesViewAdapter = new RecipeViewAdapter(this);
        mRecipesRecyclerView.setAdapter(mRecipesViewAdapter);
        if(savedInstanceState!=null
                && savedInstanceState.containsKey(LIST_STATE)) {
            mListState = savedInstanceState.getParcelable(LIST_STATE);
        }
        if(!NetworkUtils.isOnline(this))
            showErrorMessage();
        loadRecipesData();
    }

    /**
     * Calculates the number of columns for the gridlayout
     * @param context
     * @return
     */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if(noOfColumns < 2)
            noOfColumns = 2;
        return noOfColumns;
    }

    /**
     * Retrives the recipes from the json file by using okhttp lib with async call
     * @throws Exception
     */
    public void getRecipes(final DelayerCallback callback,
                           @Nullable final SimpleIdlingResource idlingResource) throws Exception {
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        Request request = new Request.Builder()
                .url(RECIPES_DATASOURCE_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, IOException e) {
                hideProgressDialog();
                e.printStackTrace();
            }

            @Override public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        String responseJson = responseBody.string();
                        Log.d("JSON", responseJson);
                        Type recipesType = new TypeToken<List<Recipe>>() {}.getType();
                        recipes = new Gson().fromJson(responseJson, recipesType);
                        Log.d(HOME_ACTIVITY_LOADING, recipes.get(0).toString());
                       /* HomeActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showRecipes(recipes);
                            }
                        });
                        */
                    }
                    //hideProgressDialog();
                } catch (IOException e){
                    throw new IOException("Unexpected code " + response);
                }
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (callback != null && recipes!=null) {
                    callback.onDone(recipes);
                    if (idlingResource != null) {
                        idlingResource.setIdleState(true);
                    }
                }
            }
        }, DELAY_MILLIS);
    }

    private void loadRecipesData() {
        try {
            showProgressDialog();
            getRecipes(HomeActivity.this, mIdlingResource);
        } catch (Exception e) {
            showErrorMessage();
        }
    }

    private void showRecipes(List<Recipe> data) {
        mRecipesViewAdapter.setRecipesData(data);
        if (mListState!=null)
            mRecipesRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);
    }

    public void showErrorMessage(){
        mConnectionErrorLayout.setVisibility(View.VISIBLE);
        mRecipesRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(Recipe selectedRecipe) {
        Log.d(HOME_ACTIVITY_LOADING, selectedRecipe.toString());
        Intent intent = new Intent(this, RecipeDetailsActivity.class);
        intent.putExtra(SELECTED_RECIPE, selectedRecipe);
        startActivity(intent);
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    public void onDone(List<Recipe> recipes) {
        showRecipes(recipes);
        hideProgressDialog();
    }
}
