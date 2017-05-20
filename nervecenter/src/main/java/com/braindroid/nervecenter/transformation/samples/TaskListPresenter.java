package com.braindroid.nervecenter.transformation.samples;

import com.braindroid.nervecenter.transformation.samples.modeling.TaskDataModel;
import com.braindroid.nervecenter.transformation.samples.modeling.TaskViewModel;

import java.util.ArrayList;
import java.util.List;

public class TaskListPresenter {

    private final List<TaskViewModel> currentViewModelList = new ArrayList<>();

    private final ViewTasksModelController modelController;
    private final TaskViewModelTransformations taskViewModelTransformations;

    public TaskListPresenter(ViewTasksModelController modelController, TaskViewModelTransformations taskViewModelTransformations) {
        this.modelController = modelController;
        this.taskViewModelTransformations = taskViewModelTransformations;
    }

    public void buildTaskList() {

        // todo : acquire from UseCase / 'DataLoader'. alternatively, accept streams of lists data to display
        // model update listener; db load listener


        List<TaskDataModel> dataModelList = modelController.getAllTasks();
        for(TaskDataModel dataModel : dataModelList) {
            TaskViewModel viewModel = taskViewModelTransformations.transform(dataModel);
            currentViewModelList.add(viewModel);
        }


    }

}
