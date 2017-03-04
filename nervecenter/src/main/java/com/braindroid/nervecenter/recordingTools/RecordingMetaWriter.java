package com.braindroid.nervecenter.recordingTools;

import android.content.Context;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okio.BufferedSink;
import okio.ByteString;
import okio.Okio;
import okio.Sink;
import timber.log.Timber;

public class RecordingMetaWriter {

    public static boolean hasMeta(Recording recording) {
        if(!recording.asFile().exists()) {
            return false;
        }

        if(new File(recording.metaPath()).exists()) {
            return true;
        }

        return false;
    }

    public static @Nullable RecordingUserMeta readRecording(Context context, Recording recording) {
        if(!hasMeta(recording)) {
            return null;
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(recording.metaPath());
            return LoganSquare.parse(fileInputStream, RecordingUserMeta.class);
        } catch (FileNotFoundException e) {
            Timber.e(e, "No output file found - %s", recording.metaName());
        } catch (IOException e) {
            Timber.e(e, "Unknown IOException - %s->", recording);
        }

        return null;
    }

    public static void writeMeta(Context context, Recording recording, RecordingUserMeta recordingUserMeta) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(recording.metaPath());

            Sink ioSink = Okio.sink(fileOutputStream);
            BufferedSink buffer = Okio.buffer(ioSink);
            buffer.write(ByteString.encodeUtf8(LoganSquare.serialize(recordingUserMeta)));
            buffer.flush();
            ioSink.flush();

        } catch (FileNotFoundException e) {
            Timber.e(e, "No output file found - %s", recording.metaName());
        } catch (IOException e) {
            Timber.e(e, "Unknown IOException - %s->", recording, recordingUserMeta);
        } finally {
            if(fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Timber.e(e, "Failed to close input stream");
                }
            }
        }
    }
}
