package com.braindroid.conciousness.recordingList;

import android.content.Context;

import com.braindroid.conciousness.recordingTags.TagChooser;
import com.braindroid.nervecenter.kotlinModels.data.OnDiskRecording;
import com.braindroid.nervecenter.kotlinModels.data.RecordingTag;
import com.braindroid.nervecenter.kotlinModels.recordingTools.RecordingDeck;

import java.util.List;

public class RecordingListViewPresenter implements ListView.Listener<OnDiskRecording>  {

    private final Context context;
    private ListView<OnDiskRecording> persistedRecordingListView;
    private RecordingDeck recordingDeck;
    private TagChooser tagChooser;

    public RecordingListViewPresenter(Context context,
                                      ListView<OnDiskRecording> listView,
                                      RecordingDeck recordingDeck,
                                      TagChooser tagChooser) {
        this.context = context;
        this.persistedRecordingListView = listView;
        this.recordingDeck = recordingDeck;
        this.tagChooser = tagChooser;

        listView.setOnLickListener(this);
    }

    @Override
    public void onLongClick(final OnDiskRecording recording, int position) {

        tagChooser.getTags(context, new TagChooser.TagsCallback() {
            @Override
            public void onNewTags(List<RecordingTag> tags) {
                recordingDeck.getRecordingStore().setTags(
                        recording, tags
                );

                persistedRecordingListView.setNewList(
                        recordingDeck.allRecordingsAsUnmanaged()
                );
            }
        });
    }

    @Override
    public void onClick(OnDiskRecording recording, int position) {

    }
}
