package com.braindroid.conciousness.recordingList;

import java.util.List;

public interface ListView<T> {

    void setNewList(List<T> newList);

    List<T> getCurrentList();

}
