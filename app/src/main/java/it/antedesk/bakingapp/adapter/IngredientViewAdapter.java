package it.antedesk.bakingapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.antedesk.bakingapp.R;
import it.antedesk.bakingapp.fragment.IngredientFragment.OnListFragmentInteractionListener;
import it.antedesk.bakingapp.model.Ingredient;

import java.util.List;

public class IngredientViewAdapter extends RecyclerView.Adapter<IngredientViewAdapter.ViewHolder> {

    private final List<Ingredient> ingredients;
    private Context parentContex;
    private final OnListFragmentInteractionListener mListener;

    public IngredientViewAdapter(List<Ingredient> items, OnListFragmentInteractionListener listener) {
        ingredients = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        parentContex = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_ingredient_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.ingredient = ingredients.get(position);
        holder.ingredientName.setText(ingredients.get(position).getIngredient());
        holder.quantity.setText(String.valueOf(ingredients.get(position).getQuantity()));
        holder.measure.setText(ingredients.get(position).getMeasure());
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @BindView(R.id.ingredient_descp)
        public TextView ingredientName;
        @BindView(R.id.ingredient_quantity)
        public TextView quantity;
        @BindView(R.id.ingredient_measure)
        public TextView measure;
        public Ingredient ingredient;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, mView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + ingredientName.getText() + ", "
                    + quantity.getText() + ", "
                    + measure.getText() + "'";
        }
    }
}
