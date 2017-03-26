package com.braindroid.conciousness;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.braindroid.conciousness.recordingList.RecordingListView;
import com.braindroid.conciousness.recordingList.RecordingListViewPresenter;
import com.braindroid.conciousness.recordingTags.TagChooser;
import com.braindroid.nervecenter.domainRecordingTools.BasicRecordingProvider;
import com.braindroid.nervecenter.domainRecordingTools.DeviceRecorder;
import com.braindroid.nervecenter.domainRecordingTools.PersistedRecordingMetaFileWriter;
import com.braindroid.nervecenter.domainRecordingTools.recordingSource.AudioRecordingHandler;
import com.braindroid.nervecenter.recordingTools.models.PersistedRecording;
import com.braindroid.nervecenter.recordingTools.models.utils.PersistedRecordingFileHandler;
import com.braindroid.nervecenter.recordingTools.models.utils.PersistedRecordingModelHandler;
import com.braindroid.nervecenter.utils.LibConstants;
import com.braindroid.nervecenter.utils.SampleIOHandler;
import com.braindroid.nervecenter.visualization.WaveformSurfaceView;
import com.braindroid.nervecenter.visualization.WaveformView;

import java.io.IOException;
import java.util.List;

import timber.log.Timber;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class HomeActivity extends BaseActivity {

    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private DeviceRecorder deviceRecorder;
    private PersistedRecordingFileHandler fileHandler;

    private RecordingListViewPresenter listViewPresenter;

    private Button centerRecordButton;
    private TextView primaryStateTextView;
    private RecordingListView recordingListView;

    private int originalScrollWidth = 0;
    private WaveformSurfaceView waveformSurfaceView;
    private ScaleGestureDetector detector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        if(Timber.treeCount() == 0) {
            Timber.plant(new Timber.DebugTree());
        }

        bindViewsAndListeners();

        buildDependencies();
    }

    @Override
    protected void onDestroy() {
//        Sensey.getInstance().stop();
        super.onDestroy();
    }

    //region Dependency building / field settings

    private void buildDependencies() {
        Context applicationContext = getApplicationContext();
        fileHandler = new PersistedRecordingFileHandler(applicationContext);

        PersistedRecordingModelHandler modelHandler = new PersistedRecordingModelHandler(applicationContext, fileHandler);
        modelHandler.addListener(new PersistedRecordingModelHandler.OnChangeListener() {
            @Override
            public void onRecordingPersisted(PersistedRecording recording) {
                updateList();
            }
        });

        PersistedRecordingMetaFileWriter metaFileWriter = new PersistedRecordingMetaFileWriter(modelHandler);
        BasicRecordingProvider basicRecordingProvider = new BasicRecordingProvider(applicationContext, fileHandler, modelHandler, metaFileWriter);

        AudioRecordingHandler handler = new AudioRecordingHandler();
        deviceRecorder = new DeviceRecorder(handler, basicRecordingProvider, fileHandler, modelHandler);

        boolean didRestore = deviceRecorder.restore();
        Timber.v("DeviceRecorder restored files - %s", didRestore);
        if(didRestore) {
            recordingListView.setNewList(deviceRecorder.getAllRecordings());
        }

        listViewPresenter = new RecordingListViewPresenter(this, recordingListView, deviceRecorder, new TagChooser());
    }

    //endregion

    //region View Binding / Interactions
    private void bindViewsAndListeners() {
        waveformSurfaceView = ViewFinder.in(this, R.id.home_activity_audio_waveform_view);

        centerRecordButton = ViewFinder.in(this, R.id.home_activity_central_feature_button);
        centerRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCenterRecordButtonClicked();
            }
        });
        primaryStateTextView = ViewFinder.in(this, R.id.home_activity_central_feature_state_info_textView);
        primaryStateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPrimaryStateTextViewClicked();
            }
        });

        recordingListView = ViewFinder.in(this, R.id.home_activity_recording_list_view);

        detector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
//                scaleWaveForm(detector.getScaleFactor());
                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {

            }
        });
    }

    private void scaleWaveForm(double scaleFactor) {
        ViewGroup.LayoutParams layoutParams = waveformSurfaceView.getLayoutParams();
        int proposedWidth = (int)Math.round(layoutParams.width * scaleFactor);
        if(proposedWidth <= originalScrollWidth) {
            proposedWidth = originalScrollWidth;
        }

        Timber.v("Scale=%f Proposed=%d", scaleFactor, proposedWidth);

        layoutParams.width = proposedWidth;
        waveformSurfaceView.setLayoutParams(layoutParams);
    }

    int counter = 0;
    private void onPrimaryStateTextViewClicked() {
        List<PersistedRecording> recordingList = deviceRecorder.getAllRecordings();
        PersistedRecording persistedRecording = recordingList.get(counter++);
        if(counter == recordingList.size()) counter = 0;
//        PersistedRecording persistedRecording = deviceRecorder.getLastRecording();
        final short[] audioData;
        try {
            audioData = SampleIOHandler.getAudioFromPath(fileHandler.getAudioFilePath(persistedRecording));
        } catch (IOException e) {
            Timber.e(e, "Failed to get audio data");
            return;
        }

//        PlaybackThread playbackThread = new PlaybackThread(audioData, new PlaybackListener() {
//            @Override
//            public void onProgress(int progress) {
//                Timber.v("PlaybackThread progress : %d", progress);
//            }
//
//            @Override
//            public void onCompletion() {
//                Timber.v("PlaybackThread COMPLETE");
//            }
//        });
//        playbackThread.startPlayback();

        waveformSurfaceView.setAudioDetails(audioData, LibConstants.SAMPLE_RATE, 1);
        waveformSurfaceView.refresh();
    }

    private final int on_record_request_code = 100;
    private void onCenterRecordButtonClicked() {
        waveformSurfaceView.RUN_TEST();
//        if(deviceRecorder == null) {
//            displayBasicMessage("Recorder unavailable.");
//            return;
//        }
//
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PERMISSION_GRANTED) {
//            toggleAudioRecordingEnabled();
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, on_record_request_code);
//        }
    }
    //endregion

    private void updateList() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                recordingListView.setNewList(deviceRecorder.getAllRecordings());
            }
        });
    }

    private void startVisualizerUpdates() {
        uiHandler.post(updateVisualizer);
    }

    // updates the visualizer every 50 milliseconds
    final Runnable updateVisualizer = new Runnable() {
        @Override
        public void run() {
            // if we are already recording
            if (deviceRecorder.isRecording()) {
                // get the current amplitude
//                int amplitude = deviceRecorder.getAmplitude();

                // update in 40 milliseconds
                uiHandler.postDelayed(this, 30);
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        deviceRecorder.initialize();

        if(on_record_request_code == requestCode) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                toggleAudioRecordingEnabled();
            } else {
                displayBasicMessage("Access to microphone has been denied.");
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void toggleAudioRecordingEnabled() {
        PersistedRecording recording;
        if(deviceRecorder.isRecording()) {
            Timber.v("Stopping recording");
            centerRecordButton.setText(R.string.home_activity_record_button_press_to_start);
            deviceRecorder.stopRecording();
            deviceRecorder.advance();
            updateList();
        } else {
            Timber.v("Starting recording");
            centerRecordButton.setText(R.string.home_activity_record_button_press_to_stop);
            recording = deviceRecorder.startRecording();
            primaryStateTextView.setText(recording.getName());

            startVisualizerUpdates();
        }
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return super.shouldShowRequestPermissionRationale(permission);
    }

    private void displayBasicMessage(CharSequence messageCharSequence) {
        Toast.makeText(this, messageCharSequence, Toast.LENGTH_LONG).show();
    }

    //region Touch handling

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        detector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }


    //endregion

}
