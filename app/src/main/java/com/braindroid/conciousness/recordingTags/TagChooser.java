package com.braindroid.conciousness.recordingTags;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.braindroid.nervecenter.recordingTools.models.Recording;
import com.braindroid.nervecenter.recordingTools.models.PersistedRecordingTag;

import java.util.ArrayList;
import java.util.List;

public class TagChooser {

    private static final ArrayList<Recording.Tag> recordingTags;
    final static CharSequence[] displays = new String[] {
            "Happy", "Sad", "Interesting", "Funny", "Deep", "Confusing"
    };
    final static CharSequence[] vals = new String[] {
            "Happy", "Sad", "Interesting", "Funny", "Deep", "Confusing"
    };
    static {
        recordingTags = new ArrayList<>();
        for (int i = 0; i < displays.length; i++) {
            Recording.Tag recordingTag = new PersistedRecordingTag();
            recordingTag.setDisplay((String)displays[i]);
            recordingTag.setIdentifier((String)vals[i]);
            recordingTags.add(recordingTag);
        }
    }



    public interface TagsCallback {
        void onNewTags(List<Recording.Tag> tags);
    }

    public void getTags(Context context, final TagsCallback tagsCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Tags");

        final ArrayList<Recording.Tag> selectedTags = new ArrayList<>();
        builder.setMultiChoiceItems(
                displays, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked) {
                            selectedTags.add(recordingTags.get(which));
                        } else {
                            selectedTags.remove(recordingTags.get(which));
                        }
                    }
                }
        );

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tagsCallback.onNewTags(selectedTags);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }
}
