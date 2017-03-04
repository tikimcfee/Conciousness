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
import com.braindroid.nervecenter.playbackTools.RecordingPlayer;
import com.braindroid.nervecenter.playbackTools.RecordingWriter;
import com.braindroid.nervecenter.recordingTools.Recording;
import com.braindroid.nervecenter.recordingTools.RecordingTag;
import com.braindroid.nervecenter.utils.ViewFinder;

import java.util.List;


public class RecordingListView extends FrameLayout
        implements ListView<Recording>, RecordingAdapter.OnRecordingItemClicked {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecordingAdapter recordingAdapter;

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
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recordingAdapter = new RecordingAdapter(this);
        recyclerView.setAdapter(recordingAdapter);
    }

    //region ListView interface implementation
    @Override
    public void setNewList(List<Recording> newList) {
        recordingAdapter.setNewList(newList);
    }

    @Override
    public void onLongClick(final Recording recording, int position) {
        TagChooser.getTags(getContext(), new TagChooser.TagsCallback() {
            @Override
            public void onNewTags(List<RecordingTag> tags) {
                if(getContext() instanceof RecordingWriter) {
                    recording.getRecordingUserMeta().setTags(tags);
                    ((RecordingWriter) getContext()).writeRecordingMeta(recording, recording.getRecordingUserMeta());
                }
            }
        });

    }

    @Override
    public void onClick(Recording recording, int position) {
        if(getContext() instanceof RecordingPlayer) {
            ((RecordingPlayer) getContext()).playRecording(recording);
        }
    }

    @Override
    public List<Recording> getCurrentList() {
        return null;
    }
    //endregion
}
