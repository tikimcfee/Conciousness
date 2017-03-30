package com.braindroid.nervecenter.utils.sampling.strategies;

import android.graphics.Path;

public class PathHolder {
    public final Path minPath;
    public final Path maxPath;

    public PathHolder(Path minPath, Path maxPath) {
        this.minPath = minPath;
        this.maxPath = maxPath;
    }

    @Override
    public String toString() {
        return "PathHolder{" +
                "minPath=" + minPath +
                ", maxPath=" + maxPath +
                '}';
    }
}
