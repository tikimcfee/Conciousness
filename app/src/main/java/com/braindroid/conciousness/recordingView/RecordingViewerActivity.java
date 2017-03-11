package com.braindroid.conciousness.recordingView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.braindroid.conciousness.BaseActivity;
import com.braindroid.conciousness.R;
import com.braindroid.nervecenter.recordingTools.models.PersistedRecording;
import com.braindroid.nervecenter.recordingTools.models.utils.PersistedRecordingFileHandler;
import com.semantive.waveformandroid.waveform.WaveformFragment;

import timber.log.Timber;

public class RecordingViewerActivity extends BaseActivity {

    public static final String INTENT_KEY_RECORDING_FILE_PATH = "INTENT_KEY_RECORDING_FILE_PATH";

    private static final PersistedRecordingFileHandler persistedRecordingFileHandler = new PersistedRecordingFileHandler();

    public static void showRecordingVisualizer(Context context, PersistedRecording persistedRecording) {
        Timber.v("showRecordingVisualizer() called with: context = [" + context + "], persistedRecording = [" + persistedRecording + "]");

        Intent intent = new Intent(context, RecordingViewerActivity.class);

        Bundle intentExtras = new Bundle();
        String audioFilePath = persistedRecordingFileHandler.getAudioFilePath(context, persistedRecording);
        intentExtras.putString(INTENT_KEY_RECORDING_FILE_PATH, audioFilePath);

        intent.putExtras(intentExtras);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recording_viewer_layout);

        if(savedInstanceState == null) {
            CustomAudioVisualizerFragment toAdd = CustomAudioVisualizerFragment.newInstance(getFileNameFromExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recording_viewer_root_layout, toAdd)
                    .commit();
        }
    }

    private String getFileNameFromExtras() {
        Bundle arguments = getIntent().getExtras();
        if(arguments == null) {
            Timber.e("No intent extras set on activity.");
            return null;
        }

        return arguments.getString(INTENT_KEY_RECORDING_FILE_PATH);
    }

    public static class CustomAudioVisualizerFragment extends WaveformFragment {

        public static final String FRAGMENT_ARGUMENT_KEY_AUDIO_FILE_NAME = "FRAGMENT_ARGUMENT_KEY_AUDIO_FILE_NAME";

        public static CustomAudioVisualizerFragment newInstance(String audioFileName) {
            CustomAudioVisualizerFragment fragment = new CustomAudioVisualizerFragment();

            Bundle arguments = new Bundle();
            arguments.putString(FRAGMENT_ARGUMENT_KEY_AUDIO_FILE_NAME, audioFileName);

            fragment.setArguments(arguments);

            return fragment;
        }

        @Override
        protected String getFileName() {
            return getFileNameFromExtras();
        }


        private String getFileNameFromExtras() {
            Bundle arguments = getArguments();
            if(arguments == null) {
                Timber.e("No arguments set on fragment.");
                return null;
            }

            return arguments.getString(FRAGMENT_ARGUMENT_KEY_AUDIO_FILE_NAME);
        }
    }

}
