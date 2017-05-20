package com.braindroid.nervecenter.transformation.samples.modeling;

public class TaskViewModelBuilder {
    private String workTypeDisplay;
    private String workTypeDetailDisplay;
    private String assigneeDisplay;
    private TaskDataModel dataModel;

    public TaskViewModelBuilder setWorkTypeDisplay(String workTypeDisplay) {
        this.workTypeDisplay = workTypeDisplay;
        return this;
    }

    public TaskViewModelBuilder setWorkTypeDetailDisplay(String workTypeDetailDisplay) {
        this.workTypeDetailDisplay = workTypeDetailDisplay;
        return this;
    }

    public TaskViewModelBuilder setAssigneeDisplay(String assigneeDisplay) {
        this.assigneeDisplay = assigneeDisplay;
        return this;
    }

    public TaskViewModelBuilder setDataModel(TaskDataModel dataModel) {
        this.dataModel = dataModel;
        return this;
    }

    public TaskViewModel createTaskViewModel() {
        return new TaskViewModel(workTypeDisplay, workTypeDetailDisplay, assigneeDisplay, dataModel);
    }
}