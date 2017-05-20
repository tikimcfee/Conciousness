package com.braindroid.nervecenter.transformation.samples.modeling;

import com.braindroid.nervecenter.transformation.DataModel;
import com.braindroid.nervecenter.transformation.DataUtils;

import java.util.Map;

public class TaskDataModel implements DataModel<String, Map<String, String>>{

    public final String identifier;
    public final String workType;
    public final String workTypeDetail;
    public final String assignee;

    public TaskDataModel(String identifier, String workType, String workTypeDetail, String assignee) {
        this.identifier = identifier;
        this.workType = workType;
        this.workTypeDetail = workTypeDetail;
        this.assignee = assignee;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Map<String, String> getProperties() {
        return DataUtils.build(
                "workType", workType,
                "workTypeDetail", workTypeDetail,
                "assignee", assignee
        );
    }
}
