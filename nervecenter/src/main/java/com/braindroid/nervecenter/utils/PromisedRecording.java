package com.braindroid.nervecenter.utils;

import com.braindroid.nervecenter.recordingTools.models.PersistedRecording;

public abstract class PromisedRecording implements ObjectPromise<PersistedRecording> {

    abstract public PersistedRecording getRecording();

//    private PromisedRecording currentRecording = new PromisedRecording() {
//        @Override
//        public PersistedRecording getRecording() {
//            PersistedRecording toReturn = recordingProvider.getCurrentRecording();
//            if(toReturn == null) {
//                toReturn = new PersistedRecording() {
//                    @Override
//                    public File asFile() {
//                        return new File("");
//                    }
//                };
//            }
//            return ;
//        }
//
//        @Override
//        public PersistedRecording getT() {
//            return null;
//        }
//    };
}
