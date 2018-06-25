package it.antedesk.bakingapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import it.antedesk.bakingapp.R;
import it.antedesk.bakingapp.RecipeDetailsActivity;

/**
 * Created by Antedesk on 24/06/2018.
 */

public class IngrendientsService extends IntentService {

    public static final String ACTION_UPDATE_RECIPE_INGREDIENTS_LIST = "it.antedesk.bakingapp.widget.action.update_recipe_ingredients";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public IngrendientsService() {
        super("IngrendientsService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_RECIPE_INGREDIENTS_LIST.equals(action)) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, IngredientsWidgetProvider.class));

                //Trigger data update to handle the ListView widgets and force a data refresh
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.ingredient_lv_widget);

                IngredientsWidgetProvider.updateIngredientsWidgets(this, appWidgetManager, RecipeDetailsActivity.recipeName, appWidgetIds);
            }
        }
    }

    public static boolean startActionUpdateRecipeIngredientsList(Context context) {
        Intent intent = new Intent(context, IngrendientsService.class);
        intent.setAction(ACTION_UPDATE_RECIPE_INGREDIENTS_LIST);
        try {
            context.startService(intent);
            return true;
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
