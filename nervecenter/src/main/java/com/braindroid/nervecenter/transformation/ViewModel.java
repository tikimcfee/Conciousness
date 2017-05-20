package com.braindroid.nervecenter.transformation;

import android.support.annotation.Nullable;

public interface ViewModel<T extends DataModel> {

    /**
     * ViewModel *can* return a backing DataModel. This is a Nullable return. It shouldn't be assumed
     * that the dataModel is always available, and may have been released elsewhere. An example of when
     * this may be non-null is during a ViewModel's construction, where direct access might be useful
     * @return The backing DataModel, if available
     */
    @Nullable
    T getDataModel();


}
