package com.braindroid.conciousness.recordingList;

import java.util.List;

public interface ListView<T> {

    interface Listener<T> {
        void onLongClick(final T listItem, int position);
        void onClick(final T recording, int position);
    }

    void setNewList(List<T> newList);

    List<T> getCurrentList();

    void setOnLickListener(Listener<T> listener);

}
