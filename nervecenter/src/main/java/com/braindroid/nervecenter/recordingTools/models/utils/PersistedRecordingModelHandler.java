package com.braindroid.nervecenter.recordingTools.models.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.LoganSquare;
import com.braindroid.nervecenter.recordingTools.models.PersistedRecording;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okio.BufferedSink;
import okio.ByteString;
import okio.Okio;
import okio.Sink;
import timber.log.Timber;

public class PersistedRecordingModelHandler {

    private static PersistedRecordingFileHandler fileHandler = new PersistedRecordingFileHandler();

    public static boolean hasModel(Context context, PersistedRecording recording) {
        if(!fileHandler.modelFileExists(context, recording)) {
            return false;
        }

        if(!fileHandler.recordingFileExists(context, recording)) {
            return false;
        }

        return true;
    }

    public static @Nullable
    PersistedRecording readPersistedRecordingModel(Context context, PersistedRecording recording) {
        if(!hasModel(context, recording)) {
            return null;
        }

        FileInputStream inputStream = fileHandler.ensureModelFileInputStream(context, recording);
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

    public static void persistRecording(Context context, PersistedRecording recording) {
        Timber.d("persistRecording() called with: context = [" + context + "], recording = [" + recording + "]");

        FileOutputStream outputStream = fileHandler.ensureModelFileOutputStream(context, recording);
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
        } catch (IOException e) {
            Timber.e(e, "Unknown IOException - %s->", recording, recording.getSystemMeta());
        }
    }

    static {

    }
}
