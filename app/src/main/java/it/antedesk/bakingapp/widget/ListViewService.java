package it.antedesk.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

import it.antedesk.bakingapp.R;
import it.antedesk.bakingapp.RecipeDetailsActivity;
import it.antedesk.bakingapp.model.Ingredient;

/**
 * Created by Antedesk on 24/06/2018.
 */

public class ListViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{

    private Context mContext;
    private List<Ingredient> ingredients;

    public ListRemoteViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        ingredients = RecipeDetailsActivity.ingredients;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return ingredients == null ? 0 : ingredients.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if(ingredients == null) return null;
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.recipe_ingredient_item_widget);
        remoteViews.setTextViewText(R.id.widget_list_view_text_ingredient, ingredients.get(position).getIngredient());
        remoteViews.setTextViewText(R.id.widget_list_view_text_measure, ingredients.get(position).getMeasure());
        remoteViews.setTextViewText(R.id.widget_list_view_text_quantity, String.valueOf(ingredients.get(position).getQuantity()));
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
