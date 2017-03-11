package com.braindroid.conciousness.recordingList;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.braindroid.conciousness.R;
import com.braindroid.conciousness.recordingTags.TagChooser;
import com.braindroid.nervecenter.playbackTools.PersistingRecordingMetaWriter;
import com.braindroid.nervecenter.playbackTools.RecordingPlayer;
import com.braindroid.nervecenter.recordingTools.models.PersistedRecording;
import com.braindroid.nervecenter.recordingTools.models.Recording;
import com.braindroid.nervecenter.utils.ViewFinder;

import java.util.List;


public class RecordingListView extends FrameLayout
        implements ListView<PersistedRecording>, RecordingAdapter.OnRecordingItemClicked {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecordingAdapter recordingAdapter;

    private RecordingListClickListener clickListener;

    public RecordingListView(@NonNull Context context) {
        super(context);
    }

    public RecordingListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecordingListView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setClickListener(RecordingListClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        recyclerView = ViewFinder.in(this, R.id.recording_list_view_mainRecyclerView);

        initializeAfterViewCapture();
    }

    private void initializeAfterViewCapture() {
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recordingAdapter = new RecordingAdapter(this);
        recyclerView.setAdapter(recordingAdapter);
    }

    //region ListView interface implementation
    @Override
    public void setNewList(List<PersistedRecording> newList) {
        recordingAdapter.setNewList(newList);
    }

    @Override
    public void onLongClick(final PersistedRecording recording, int position) {
        if(clickListener != null) {
            clickListener.onRecordingItemClicked(recording, position);
        }
    }

    @Override
    public void onClick(PersistedRecording recording, int position) {
        if(clickListener != null) {
            clickListener.onRecordingItemLongClicked(recording, position);
        }
    }

    @Override
    public List<PersistedRecording> getCurrentList() {
        return null;
    }
    //endregion
}
