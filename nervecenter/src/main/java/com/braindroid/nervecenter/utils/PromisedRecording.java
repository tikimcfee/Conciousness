package com.braindroid.nervecenter.utils;

import com.braindroid.nervecenter.recordingTools.Recording;

import java.io.File;

public abstract class PromisedRecording implements ObjectPromise<Recording> {

    abstract public Recording getRecording();

//    private PromisedRecording currentRecording = new PromisedRecording() {
//        @Override
//        public Recording getRecording() {
//            Recording toReturn = recordingProvider.getCurrentRecording();
//            if(toReturn == null) {
//                toReturn = new Recording() {
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
//        public Recording getT() {
//            return null;
//        }
//    };
}
