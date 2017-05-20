package com.braindroid.nervecenter.transformation.samples;

import com.braindroid.nervecenter.transformation.Transformer;
import com.braindroid.nervecenter.transformation.samples.modeling.TaskDataModel;
import com.braindroid.nervecenter.transformation.samples.modeling.TaskViewModel;
import com.braindroid.nervecenter.transformation.samples.modeling.TaskViewModelBuilder;

public class TaskViewModelTransformations implements Transformer<TaskDataModel, TaskViewModel> {

    public static class AssigneeTransformer implements Transformer<String, String> {
        @Override
        public String transform(String transformationTarget) {
            switch (transformationTarget) {
                case "ivan":
                    return "Ivan, Cool Guy";
                case "brooke":
                    return "Brooke, Amazing Lady";
                case "renee":
                    return "Renee, Wonderful Mother";
                case "rizal":
                    return "Rizal, Wonderful Father";
                case "maria":
                    return "Chickie, Adorable Grandmother";
            }
            return "Unknown";
        }
    }

    public static class WorkTypeTransformer implements Transformer<String, String> {
        @Override
        public String transform(String transformationTarget) {
            switch (transformationTarget) {
                case "electrical":
                    return "Misc. Electrical";
                case "plumbing":
                    return "Misc. Plumbing";
            }
            return "Unknown";
        }
    }

    public static class WorkTypeDetailTransformer implements Transformer<String, String> {
        @Override
        public String transform(String transformationTarget) {
            switch (transformationTarget) {
                case "short-circuit":
                    return "Short in Circuit";
                case "zipline-fracture":
                    return "Zipline Fracture in Pipe";
            }
            return "Unknown";
        }
    }

    private WorkTypeTransformer workTypeTransformer;
    private WorkTypeDetailTransformer workTypeDetailTransformer;
    private AssigneeTransformer assigneeTransformer;

    public static TaskViewModelTransformations defaultTransformer() {
        return new TaskViewModelTransformations(
                new WorkTypeTransformer(),
                new WorkTypeDetailTransformer(),
                new AssigneeTransformer()
        );
    }

    public TaskViewModelTransformations(WorkTypeTransformer workTypeTransformer,
                                        WorkTypeDetailTransformer workTypeDetailTransformer,
                                        AssigneeTransformer assigneeTransformer) {
        this.workTypeTransformer = workTypeTransformer;
        this.workTypeDetailTransformer = workTypeDetailTransformer;
        this.assigneeTransformer = assigneeTransformer;
    }

    @Override
    public TaskViewModel transform(TaskDataModel transformationTarget) {
        TaskViewModelBuilder builder = new TaskViewModelBuilder();
        builder.setDataModel(transformationTarget)
                .setAssigneeDisplay(assigneeTransformer.transform(
                        transformationTarget.assignee)
                )
                .setWorkTypeDetailDisplay(workTypeDetailTransformer.transform(
                        transformationTarget.workTypeDetail)
                )
                .setWorkTypeDisplay(workTypeTransformer.transform(
                        transformationTarget.workType)
                );
        return builder.createTaskViewModel();
    }
}
