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
import it.antedesk.bakingapp.fragment.StepFragment.OnListFragmentInteractionListener;
import it.antedesk.bakingapp.model.Step;

import java.util.List;

public class StepViewAdapter extends RecyclerView.Adapter<StepViewAdapter.ViewHolder> {

    private final List<Step> steps;
    private Context parentContex;

    private final OnListFragmentInteractionListener mListener;

    public StepViewAdapter(List<Step> items, OnListFragmentInteractionListener listener) {
        steps = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        parentContex = parent.getContext();
        View view = LayoutInflater.from(parentContex)
                .inflate(R.layout.fragment_step_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.step = steps.get(position);
        holder.mStepShortDesc.setText(steps.get(position).getShortDescription());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.step);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @BindView(R.id.step_short_desc_tv) TextView mStepShortDesc;
        public Step step;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, mView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mStepShortDesc.getText() + "'";
        }
    }
}
