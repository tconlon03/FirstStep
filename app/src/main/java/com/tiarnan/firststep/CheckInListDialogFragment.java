package com.tiarnan.firststep;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     CheckInListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 * <p>You activity (or fragment) needs to implement {@link CheckInListDialogFragment.Listener}.</p>
 */
public class CheckInListDialogFragment extends BottomSheetDialogFragment {

    // TODO: Customize parameter argument names
    public static final String TAG = "CheckInDialog";
    private Listener mListener;

    // TODO: Customize parameters
    public static CheckInListDialogFragment newInstance(int itemCount) {
        final CheckInListDialogFragment fragment = new CheckInListDialogFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkin_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CheckInAdapter());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (Listener) parent;
        } else {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public interface Listener {
        void onCheckInClicked(int position);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView item_text;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            // TODO: Customize the item layout
            super(inflater.inflate(R.layout.fragment_checkin_list_dialog_item, parent, false));
            item_text = (TextView) itemView.findViewById(R.id.recycler_item_text);
            item_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onCheckInClicked(getAdapterPosition());
                        dismiss();
                    }
                }
            });
        }

    }

    private class CheckInAdapter extends RecyclerView.Adapter<ViewHolder> {

        Resources res = getResources();
        private final String[] checkin_values = res.getStringArray(R.array.checkin_values);
        private final int[] icons = {
                R.drawable.ic_very_happy,
                R.drawable.ic_happy,
                R.drawable.ic_sentiment_neutral_black_24dp,
                R.drawable.ic_sentiment_dissatisfied_black_24dp,
                R.drawable.ic_sentiment_very_dissatisfied_black_24dp
        };

        CheckInAdapter() { }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.item_text.setText(checkin_values[position]);
            holder.item_text.setCompoundDrawablesWithIntrinsicBounds(icons[position], 0, 0, 0);
        }

        @Override
        public int getItemCount() {
            return checkin_values.length;
        }

    }

}
