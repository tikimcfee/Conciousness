package com.braindroid.conciousness.recordingList;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.braindroid.conciousness.R;
import com.braindroid.conciousness.recordingTags.TagChooser;
import com.braindroid.nervecenter.playbackTools.ManagedMediaPlayer;
import com.braindroid.nervecenter.playbackTools.SeekingAudioController;
import com.braindroid.nervecenter.recordingTools.models.PersistedRecording;
import com.braindroid.nervecenter.utils.ViewFinder;

import timber.log.Timber;

public class RecordingListViewHolder extends RecyclerView.ViewHolder {

    public interface OnClick {
        void onRecordingClicked(RecordingListViewModel viewModel, int position, boolean longPress, SeekingAudioController seekingAudioController);
    }

    private final View rootLayout;

    private Button mainButton;
    private TextView mainTextView;
    private TextView currentTimeTextView;
    private TextView remainingTimeTextView;
    private SeekBar audioSeekBar;

    private RecordingListViewModel currentViewModel = null;
    private OnClick onClick;

    private final SeekingAudioController seekingAudioController;

    public static RecordingListViewHolder create(ViewGroup parent, OnClick listener, ManagedMediaPlayer managedMediaPlayer) {
        return new RecordingListViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.view_recording_list_item_row, parent, false
                ),
                listener,
                managedMediaPlayer
        );
    }

    private RecordingListViewHolder(View itemView, OnClick onClick, ManagedMediaPlayer mediaPlayer) {
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

        audioSeekBar = ViewFinder.in(rootLayout, R.id.view_recording_list_item_row_seekBar);
        currentTimeTextView = ViewFinder.in(rootLayout, R.id.view_recording_list_item_row_current_time_textView);
        remainingTimeTextView = ViewFinder.in(rootLayout, R.id.view_recording_list_item_row_remaining_time_textView);

        seekingAudioController = new SeekingAudioController(
                audioSeekBar, currentTimeTextView, remainingTimeTextView, mediaPlayer
        );
    }

    public void bind(RecordingListViewModel listViewModel, PersistedRecording recording) {
        currentViewModel = listViewModel;

        mainButton.setText("play");

        CharSequence toSet = listViewModel.getRecordingTitle() + "\n" + listViewModel.getTagInformation();
        mainTextView.setText(toSet);

        seekingAudioController.setRecording(recording);
    }

    private void onMainButtonClicked(boolean longPress) {
        if(onClick == null) {
            Timber.w("No onClickListener set");
            return;
        }

        if(!longPress) {
            if(seekingAudioController.isPlaying()) {
                seekingAudioController.pause();
                setPausedUi();
            } else {
                seekingAudioController.play();
                setPlayingUi();
            }
        } else {
            onClick.onRecordingClicked(currentViewModel, getAdapterPosition(), longPress, seekingAudioController);
        }
    }

    public void setPausedUi() {
        mainButton.setText("play");
    }

    public void setPlayingUi() {
        mainButton.setText("pause");
    }
}
