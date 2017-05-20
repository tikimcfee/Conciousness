package com.braindroid.nervecenter.transformation.samples.modeling;

import android.support.annotation.Nullable;

import com.braindroid.nervecenter.transformation.ViewModel;

import java.lang.ref.WeakReference;

public class TaskViewModel implements ViewModel<TaskDataModel> {

    private final WeakReference<TaskDataModel> weakBackingTask;
    private final String workTypeDisplay;
    private final String workTypeDetailDisplay;
    private final String assigneeDisplay;

    public TaskViewModel(String workTypeDisplay,
                         String workTypeDetailDisplay,
                         String assigneeDisplay,
                         TaskDataModel dataModel) {
        this.workTypeDisplay = workTypeDisplay;
        this.workTypeDetailDisplay = workTypeDetailDisplay;
        this.assigneeDisplay = assigneeDisplay;
        this.weakBackingTask = new WeakReference<>(dataModel);
    }

    @Nullable
    @Override
    public TaskDataModel getDataModel() {
        return weakBackingTask.get();
    }
}
