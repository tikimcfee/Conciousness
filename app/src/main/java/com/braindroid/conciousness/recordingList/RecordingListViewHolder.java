package com.braindroid.conciousness.recordingList;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.braindroid.conciousness.R;
import com.braindroid.nervecenter.utils.ViewFinder;

import timber.log.Timber;

public class RecordingListViewHolder extends RecyclerView.ViewHolder {

    public interface OnClick {
        void onRecordingClicked(RecordingListViewModel viewModel, int position, boolean longPress);
    }

    private final View rootLayout;

    private Button mainButton;
    private TextView mainTextView;

    private RecordingListViewModel currentViewModel = null;
    private OnClick onClick;


    public static RecordingListViewHolder create(ViewGroup parent, OnClick listener) {
        return new RecordingListViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.view_recording_list_item_row, parent, false
                ),
                listener
        );
    }

    private RecordingListViewHolder(View itemView, OnClick onClick) {
        super(itemView);
        this.rootLayout = itemView;
        this.onClick = onClick;

        mainButton = ViewFinder.in(rootLayout, R.id.view_recording_list_item_row_playPauseButton);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMainButtonClicked(false);
            }
        });
        mainButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onMainButtonClicked(true);
                return true;
            }
        });
        mainTextView = ViewFinder.in(rootLayout, R.id.view_recording_list_item_row_mainTextView);
    }

    public void bind(RecordingListViewModel listViewModel) {
        currentViewModel = listViewModel;

        mainButton.setText(listViewModel.getRecordingTitle());
        mainTextView.setText(listViewModel.getTopSupplementalText());
    }

    private void onMainButtonClicked(boolean longPress) {
        if(onClick == null) {
            Timber.w("No onClickListener set");
            return;
        }
        onClick.onRecordingClicked(currentViewModel, getAdapterPosition(), longPress);
    }
}
