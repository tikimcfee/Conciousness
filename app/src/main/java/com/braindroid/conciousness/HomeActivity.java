package com.braindroid.conciousness;

import android.Manifest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.braindroid.nervecenter.domainRecordingTools.DeviceRecorder;
import com.braindroid.nervecenter.recordingTools.Recording;

import java.io.IOException;
import java.util.List;

import timber.log.Timber;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class HomeActivity extends AppCompatActivity {

    private DeviceRecorder deviceRecorder = null;

    private Button centerRecordButton;
    private TextView primaryStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(Timber.treeCount() == 0) {
            Timber.plant(new Timber.DebugTree());
        }

        centerRecordButton = (Button)findViewById(R.id.home_activity_central_feature_button);
        centerRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCenterRecordButtonClicked();
            }
        });
        primaryStateTextView= (TextView)findViewById(R.id.home_activity_central_feature_state_info_textView);
        primaryStateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPrimaryStateTextViewClicked();
            }
        });

        MediaRecorder mediaRecorder = new MediaRecorder();
        BasicRecordingProvider basicRecordingProvider = new BasicRecordingProvider(mediaRecorder, this);
        deviceRecorder = new DeviceRecorder(mediaRecorder, basicRecordingProvider);
        deviceRecorder.initialize();
    }

    private void onPrimaryStateTextViewClicked() {
        List<Recording> allRecordings = deviceRecorder.getAllRecordings();
        int size = allRecordings.size();
        if(size == 0) {
            Timber.e("No available file to play.");
            return;
        }

        int lastIndex = size - 1;
        Recording currentRecording = allRecordings.get(lastIndex);
        if(!currentRecording.asFile().exists()) {
            Timber.e("File doesn't exist!");
            return;
        }

        playRecording(currentRecording);
    }

    private MediaPlayer play_recording_last_media_recorder = null;
    private void playRecording(Recording currentRecording) {
        Timber.v("playRecording() called with: currentRecording = [" + currentRecording + "]");

        if(play_recording_last_media_recorder != null) {
            Timber.v("Stopping %s", play_recording_last_media_recorder);
            play_recording_last_media_recorder.stop();
            play_recording_last_media_recorder.release();
        }
        MediaPlayer mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        play_recording_last_media_recorder = mp;

        try {
//            FileInputStream fi = new FileInputStream(currentRecording.asFile());
//            mp.setDataSource(fi.getFD());
            mp.setDataSource(currentRecording.asFile().getAbsolutePath());
            Timber.v("Playing [%s]", currentRecording);

            mp.prepare();
            mp.start();
        } catch (IllegalStateException e) {
            Timber.e(e, "Illegal State in playRecording() - %s", currentRecording);
            e.printStackTrace();
            return;
        } catch (IOException e) {
            Timber.e(e, "Recording playback failed -> %s", currentRecording.toString());
        }
//
    }

    private final int on_record_request_code = 100;
    private void onCenterRecordButtonClicked() {
        if(deviceRecorder == null) {
            displayBasicMessage("Recorder unavailable.");
            return;
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PERMISSION_GRANTED) {
            toggleAudioRecordingEnabled();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, on_record_request_code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
        Recording recording;
        if(deviceRecorder.isRecording()) {
            Timber.v("Stopping recording");
            deviceRecorder.stopRecording();
            deviceRecorder.advance();
        } else {
            Timber.v("Starting recording");
            recording = deviceRecorder.startRecording();
            primaryStateTextView.setText(recording.asFile().getName());
        }
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return super.shouldShowRequestPermissionRationale(permission);
    }

    private void displayBasicMessage(CharSequence messageCharSequence) {
        Toast.makeText(this, messageCharSequence, Toast.LENGTH_LONG).show();
    }

}
