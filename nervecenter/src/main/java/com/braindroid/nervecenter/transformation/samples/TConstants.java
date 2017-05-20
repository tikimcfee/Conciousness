package com.braindroid.nervecenter.transformation.samples;

import com.braindroid.nervecenter.transformation.DataUtils;
import com.braindroid.nervecenter.transformation.samples.modeling.TaskDataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class TConstants {
    private TConstants(){}

    public static final int SampleCount = 5;

    public static final String[] sampleNames = new String[] {
            "ivan", "brooke", "maria", "renee", "rizal", "vincent", "sue"
    };

    public static final String[] wTypes = new String[] {
            "electrical", "plumbing", "appliance"
    };

    public static final String[] wTypeDetails = new String[] {
            "exposed-wiring", "zipline-fracture", "repair", "replace"
    };

    public static final List<Map<String, String>> sampleModelsBackingList = new ArrayList<>();
    public static final List<TaskDataModel> sampleModels = new ArrayList<>();
    static {
        randomize();
    }

    public static void randomize() {
        Random random = new Random();
        for(int i = 0, s = random.nextInt(SampleCount); i < s; i++) {
            final String id = UUID.randomUUID().toString();
            final String assignee = sampleNames[random.nextInt(sampleNames.length)];
            final String workType = wTypes[random.nextInt(wTypes.length)];
            final String workTypeDetail = wTypeDetails[random.nextInt(wTypeDetails.length)];

            Map<String, String> map = DataUtils.build(
                    "id", id,
                    "assignee", assignee,
                    "workType", workType,
                    "workTypeDetail", workTypeDetail
            );
            sampleModelsBackingList.add(map);

            TaskDataModel fromMap = new TaskDataModel(
                    id, workType, workTypeDetail, assignee
            );
            sampleModels.add(fromMap);
        }
    }
}
