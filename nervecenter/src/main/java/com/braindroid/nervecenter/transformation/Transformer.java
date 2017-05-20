package com.braindroid.nervecenter.transformation;

public interface Transformer<T, K> {

    /**
     * Take some generic object type and return some representation of it. The types are specified as
     * generics in the interface, and is used as a return type enforcer. Note, that there is *no
     * explicit guarantee* that the object will not be modified by this call.
     * @param transformationTarget      The object to 'transform' into a new representation.
     * @return                          A representation of the target transformationTarget object.
     */
    K transform(T transformationTarget);

}
