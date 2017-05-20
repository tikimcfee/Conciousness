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
import com.braindroid.conciousness.ViewFinder;
import com.braindroid.nervecenter.kotlinModels.android.AndroidDiskFileProvider;
import com.braindroid.nervecenter.kotlinModels.data.OnDiskRecording;
import com.braindroid.nervecenter.kotlinModels.playbackTools.ManagedMediaPlayerPool;
import com.braindroid.nervecenter.kotlinModels.utils.OnDiskRecordingFileHandler;

import java.util.List;


public class RecordingListView extends FrameLayout
        implements ListView<OnDiskRecording>, RecordingAdapter.OnRecordingItemClicked {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecordingAdapter recordingAdapter;

    private ListView.Listener<OnDiskRecording> clickListener;

    public RecordingListView(@NonNull Context context) {
        super(context);
    }

    public RecordingListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecordingListView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        recyclerView = ViewFinder.in(this, R.id.recording_list_view_mainRecyclerView);

        initializeAfterViewCapture();
    }

    private void initializeAfterViewCapture() {
        AndroidDiskFileProvider fileProvider = new AndroidDiskFileProvider(getContext().getApplicationContext());
        OnDiskRecordingFileHandler recordingFileHandler = new OnDiskRecordingFileHandler(fileProvider);
        ManagedMediaPlayerPool mediaPlayerPool = new ManagedMediaPlayerPool(recordingFileHandler);

        recordingAdapter = new RecordingAdapter(this, mediaPlayerPool);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recordingAdapter);
    }

    //region ListView interface implementation
    @Override
    public void setNewList(List<OnDiskRecording> newList) {
        recordingAdapter.setNewList(newList);
    }

    @Override
    public List<OnDiskRecording> getCurrentList() {
        return recordingAdapter.getRecordings();
    }

    @Override
    public void setOnLickListener(Listener<OnDiskRecording> listener) {
        this.clickListener = listener;
    }

    @Override
    public void onLongClick(final OnDiskRecording recording, int position) {
        if(clickListener != null) {
            clickListener.onLongClick(recording, position);
        }
    }

    @Override
    public void onClick(OnDiskRecording recording, int position) {
        if(clickListener != null) {
            clickListener.onClick(recording, position);
        }
    }
    //endregion
}
