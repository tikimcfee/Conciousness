package com.braindroid.nervecenter.transformation;

public interface DataModel<Q, Z> {

    /*
    The interfaces below were a bit much.. no reason to wrap them up, really. They can be reused
    if a need for them is found, but again, lots of unnecessary wrapping.
     */

//    /**
//     * Defines some arbitrary identifier for a DataModel. It can be returned as its
//     * parameter type, Q.
//     * @param <Q>
//     */
//    interface Identifier<Q> {
//        Q asBacking();
//    }
//
//    /**
//     * Defines some arbitrary set of properties for a DataModel. A use case of this is
//     * returning a Map<> of some kind (key/value). It can be returned as its parameter type,
//     * Z.
//     * @param <Z>
//     */
//    interface Properties<Z> {
//        Z asBacking();
//    }
//    Identifier<Q> getIdentifier();
//    Properties<Z> getProperties();

    /**
     * Defines some arbitrary identifier for a DataModel. It is returned as its
     * parameter type, Q.
     * @return the generic-defined Identifier object (usually a string)
     */
    Q getIdentifier();

    /**
     * Defines some arbitrary set of properties for a DataModel. A use case of this is
     * returning a Map<> of some kind (key/value). It is returned as its parameter type,
     * Z.
     * @return  the generic-defined Properties object (can be a Map, JSONObject, etc.)
     */
    Z getProperties();

}
