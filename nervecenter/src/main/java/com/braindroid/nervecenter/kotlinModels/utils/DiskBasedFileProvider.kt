package com.braindroid.nervecenter.kotlinModels.utils

import java.io.File

interface DiskBasedFileProvider {

    // --- Standard Values ---
    //region values
    /**
     * Suggested directory name for storing data models describing audio recordings
     */
    val modelStoreDirectoryName: String
        get() = "recordingData"

    /**
     * Suggested directory name for storing raw audio recordings
     */
    val recordingStoreDirectoryName: String
        get() = "recordings"
    //endregion

    // --- Path based interaction ---
    //region string functions
    /**
     * Return the absolute storage path for files. Usually the root of two subdirectories; one for
     * data models, one for recordings.
     *
     * Returns a null string if a path could not be retrieved for any reason.
     */
    fun absoluteBasePath(): String?
    //endregion

    // --- Audio recording file interactions ---
    //region audio recordings
    /**
     * Return a fully qualified file path for the given name.
     *
     * Returns null if the given name is unusable, or if a path could not be retrieved for any reason.
     */
    fun recordingPathForFilename(filename: String): String?

    /**
     * Returns a usable, on-disk recording file for the given filename.
     *
     * The file is not guaranteed to exist on disk yet.
     *
     * Returns null if a path could not be constructed for the given filename.
     */
    fun recordingFileForFilename(filename: String): File?
    //endregion

    // --- Recording model data file interactions ---
    //region recording model data
    /**
     * Return a fully qualified file path for the given name.
     *
     * Returns null if the given name is unusable, or if a path could not be retrieved for any reason.
     */
    fun modelPathForFilename(filename: String): String?

    /**
     * Returns a usable, on-disk data file for the given filename.
     *
     * The file is not guaranteed to exist on disk yet.
     *
     * Returns null if a path could not be constructed for the given filename.
     */
    fun modelFileForFilename(filename: String): File?
    //endregion

}