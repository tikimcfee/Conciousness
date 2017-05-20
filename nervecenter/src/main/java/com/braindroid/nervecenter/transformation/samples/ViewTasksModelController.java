package com.braindroid.nervecenter.transformation.samples;

import com.braindroid.nervecenter.transformation.samples.modeling.TaskDataModel;

import java.util.List;

public class ViewTasksModelController {

    public List<TaskDataModel> getAllTasks() {
        return TConstants.sampleModels;
    }

    public void reload() {
        TConstants.randomize();
    }



}
