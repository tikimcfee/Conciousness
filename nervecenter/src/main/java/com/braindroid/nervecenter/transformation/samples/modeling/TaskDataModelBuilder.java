package com.braindroid.nervecenter.transformation.samples.modeling;

public class TaskDataModelBuilder {
    private String identifier;
    private String workType;
    private String workTypeDetail;
    private String assignee;

    public TaskDataModelBuilder setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public TaskDataModelBuilder setWorkType(String workType) {
        this.workType = workType;
        return this;
    }

    public TaskDataModelBuilder setWorkTypeDetail(String workTypeDetail) {
        this.workTypeDetail = workTypeDetail;
        return this;
    }

    public TaskDataModelBuilder setAssignee(String assignee) {
        this.assignee = assignee;
        return this;
    }

    public TaskDataModel createTaskDataModel() {
        return new TaskDataModel(identifier, workType, workTypeDetail, assignee);
    }
}