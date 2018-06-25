package it.antedesk.bakingapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.antedesk.bakingapp.R;
import it.antedesk.bakingapp.model.Recipe;

/**
 * Created by Antedesk on 16/05/2018.
 */

public class RecipeViewAdapter extends RecyclerView.Adapter<RecipeViewAdapter.RecipeAdapterViewHolder> {
    private List<Recipe> recipeList;
    private Context parentContex;

    private final RecipeViewAdapterOnClickHandler mClickHandler;

    @NonNull
    @Override
    public RecipeAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        parentContex = parent.getContext();
        int layoutIdForListItem = R.layout.recipe_item;
        LayoutInflater inflater = LayoutInflater.from(parentContex);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new RecipeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeAdapterViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        Log.d("MIOTAG", recipe.toString());
        if(recipe.getImage()!=null && !recipe.getImage().isEmpty() && recipe.getImage().equals(""))
            Picasso.with(parentContex)
                .load(recipe.getImage())
                .placeholder(R.drawable.bakery_default_img)
                .error(R.drawable.bakery_default_img)
                .into(holder.mRecipeImage);
        else
            holder.mRecipeImage.setImageResource(R.drawable.bakery_default_img);
        holder.mRecipeName.setText(recipe.getName());
        holder.mServingInfo.setText(String.valueOf(recipe.getServings()));
    }

    @Override
    public int getItemCount() {
        if (recipeList == null || recipeList.isEmpty()) return 0;
        return recipeList.size();
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface RecipeViewAdapterOnClickHandler {
        void onClick(Recipe selectedRecipe);
    }


    public RecipeViewAdapter(RecipeViewAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

    public class RecipeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_image_iv)
        ImageView mRecipeImage;
        @BindView(R.id.recipe_name_tv)
        TextView mRecipeName;
        @BindView(R.id.recipe_servings_tv)
        TextView mServingInfo;


        public RecipeAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Recipe recipe = recipeList.get(adapterPosition);
            mClickHandler.onClick(recipe);
        }
    }

    public void setRecipesData(List<Recipe> data) {
        recipeList = data;
        notifyDataSetChanged();
    }
}
