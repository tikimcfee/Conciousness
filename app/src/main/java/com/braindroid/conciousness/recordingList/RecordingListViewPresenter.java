package com.braindroid.conciousness.recordingList;

import android.content.Context;

import com.braindroid.conciousness.recordingTags.TagChooser;
import com.braindroid.nervecenter.domainRecordingTools.DeviceRecorder;
import com.braindroid.nervecenter.kotlinModels.data.OnDiskRecording;
import com.braindroid.nervecenter.kotlinModels.data.RecordingMeta;
import com.braindroid.nervecenter.kotlinModels.data.RecordingTag;
import com.braindroid.nervecenter.recordingTools.models.PersistedRecording;
import com.braindroid.nervecenter.recordingTools.models.Recording;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordingListViewPresenter implements ListView.Listener<OnDiskRecording>  {

    private final Context context;
    private ListView<OnDiskRecording> persistedRecordingListView;
    private DeviceRecorder deviceRecorder;
    private TagChooser tagChooser;

    public RecordingListViewPresenter(Context context,
                                      ListView<OnDiskRecording> listView,
                                      DeviceRecorder deviceRecorder,
                                      TagChooser tagChooser) {
        this.context = context;
        this.persistedRecordingListView = listView;
        this.deviceRecorder = deviceRecorder;
        this.tagChooser = tagChooser;

        listView.setOnLickListener(this);
    }

    @Override
    public void onLongClick(final OnDiskRecording recording, int position) {

        tagChooser.getTags(context, new TagChooser.TagsCallback() {
            @Override
            public void onNewTags(List<Recording.Tag> tags) {
                List<RecordingTag> copied = new ArrayList<RecordingTag>();
                for(Recording.Tag tag : tags) {
                    Map<String, String> map = tag.getTagProperties();
                    Map<String, Object> copiedTagProps = new HashMap<>();
                    for(String key : map.keySet()) {
                        copiedTagProps.put(key, map.get(key));
                    }


                    copied.add(new RecordingTag(
                            tag.getIdentifier(),
                            tag.getDisplay(),
                            new RecordingMeta(copiedTagProps)
                    ));
                }

                recording.setTags(copied);


                deviceRecorder.persistRecording(PersistedRecording.fromOnDiskRecording(recording));
            }
        });
    }

    @Override
    public void onClick(OnDiskRecording recording, int position) {

    }
}
