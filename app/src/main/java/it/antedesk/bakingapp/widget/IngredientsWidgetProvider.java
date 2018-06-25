package it.antedesk.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import it.antedesk.bakingapp.R;
import it.antedesk.bakingapp.widget.IngrendientsService;
import it.antedesk.bakingapp.widget.ListViewService;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                String recipeName,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_provider);
        views.setTextViewText(R.id.appwidget_text, recipeName);

        Intent intent = new Intent(context, ListViewService.class);
        views.setRemoteAdapter(R.id.ingredient_lv_widget, intent);

        // Instruct the it.antedesk.bakingapp.widget manager to update the it.antedesk.bakingapp.widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        IngrendientsService.startActionUpdateRecipeIngredientsList(context);
    }

    public static void updateIngredientsWidgets(Context context, AppWidgetManager appWidgetManager, String recipeName, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, recipeName, appWidgetId);
        }
    }
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first it.antedesk.bakingapp.widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last it.antedesk.bakingapp.widget is disabled
    }
}

