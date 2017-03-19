package com.braindroid.conciousness.recordingList;

import android.content.Context;

import com.braindroid.conciousness.recordingTags.TagChooser;
import com.braindroid.nervecenter.domainRecordingTools.DeviceRecorder;
import com.braindroid.nervecenter.recordingTools.models.PersistedRecording;
import com.braindroid.nervecenter.recordingTools.models.Recording;

import java.util.List;

public class RecordingListViewPresenter implements ListView.Listener<PersistedRecording>  {

    private final Context context;
    private ListView<PersistedRecording> persistedRecordingListView;
    private DeviceRecorder deviceRecorder;
    private TagChooser tagChooser;

    public RecordingListViewPresenter(Context context,
                                      ListView<PersistedRecording> listView,
                                      DeviceRecorder deviceRecorder,
                                      TagChooser tagChooser) {
        this.context = context;
        this.persistedRecordingListView = listView;
        this.deviceRecorder = deviceRecorder;
        this.tagChooser = tagChooser;

        listView.setOnLickListener(this);
    }

    @Override
    public void onLongClick(final PersistedRecording recording, int position) {
        tagChooser.getTags(context, new TagChooser.TagsCallback() {
            @Override
            public void onNewTags(List<Recording.Tag> tags) {
                recording.setTags(tags);
                deviceRecorder.persistRecording(recording);
            }
        });
    }

    @Override
    public void onClick(PersistedRecording recording, int position) {

    }
}
