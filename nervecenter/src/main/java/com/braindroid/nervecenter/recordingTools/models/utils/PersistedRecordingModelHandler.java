package com.braindroid.nervecenter.recordingTools.models.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.LoganSquare;
import com.braindroid.nervecenter.recordingTools.models.PersistedRecording;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okio.BufferedSink;
import okio.ByteString;
import okio.Okio;
import okio.Sink;
import timber.log.Timber;

public class PersistedRecordingModelHandler {

    public interface OnChangeListener {
        void onRecordingPersisted(PersistedRecording recording);
    }

    private final Context context;
    private final PersistedRecordingFileHandler fileHandler;

    private final List<OnChangeListener> listenerList = new ArrayList<>();

    public PersistedRecordingModelHandler(Context context,
                                          PersistedRecordingFileHandler fileHandler) {
        this.context = context;
        this.fileHandler = fileHandler;
    }

    public void addListener(OnChangeListener onChangeListener) {
        if(listenerList.contains(onChangeListener)) {
            Timber.w("Listener already registered [%s]->[%s]", onChangeListener, this);
            return;
        }

        listenerList.add(onChangeListener);
    }

    private boolean hasModel(PersistedRecording recording) {
        if(!fileHandler.modelFileExists(recording)) {
            return false;
        }

        if(!fileHandler.recordingFileExists(recording)) {
            return false;
        }

        return true;
    }

    @Nullable
    public  PersistedRecording readPersistedRecordingModel(PersistedRecording recording) {
        if(!hasModel(recording)) {
            return null;
        }

        FileInputStream inputStream = fileHandler.ensureModelFileInputStream(recording);
        if(inputStream == null) {
            Timber.w("No Model FIS stream for %s", recording);
            return null;
        }

        try {
            PersistedRecording loadedRecording = LoganSquare.parse(inputStream, PersistedRecording.class);
            Timber.v("Read [%s]", loadedRecording);
            return loadedRecording;
        } catch (FileNotFoundException e) {
            Timber.e(e, "No output file found - %s", recording);
        } catch (IOException e) {
            Timber.e(e, "Unknown IOException - %s->", recording);
        }

        return null;
    }

    public void persistRecording(PersistedRecording recording) {
        Timber.d("persistRecording() called with: context = [" + context + "], recording = [" + recording + "]");

        FileOutputStream outputStream = fileHandler.ensureModelFileOutputStream(recording);
        if(outputStream == null) {
            Timber.w("Could not write %s - no output stream", recording);
            return;
        }

        try {
            Sink ioSink = Okio.sink(outputStream);
            BufferedSink buffer = Okio.buffer(ioSink);
            buffer.write(ByteString.encodeUtf8(LoganSquare.serialize(recording)));
            buffer.flush();
            ioSink.flush();

        } catch (FileNotFoundException e) {
            Timber.e(e, "No output file found - %s", recording.getSystemMeta().getTargetModelIdentifier());
            return;
        } catch (IOException e) {
            Timber.e(e, "Unknown IOException - %s->", recording, recording.getSystemMeta());
            return;
        }

        for(OnChangeListener onChangeListener : listenerList) {
            onChangeListener.onRecordingPersisted(recording);
        }
    }
}
