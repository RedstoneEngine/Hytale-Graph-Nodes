package dev.redengdev.data;

import org.joml.Vector3d;

import it.unimi.dsi.fastutil.ints.IntSet;

public class SpawnerNode
{
    public final Vector3d position;
    public final int rotationIndex;
    public final IntSet tagSet;

    public SpawnerNode(Vector3d position, int rotationIndex, IntSet tagSet)
    {
        this.position = position;
        this.rotationIndex = rotationIndex;
        this.tagSet = tagSet;
    }
}
