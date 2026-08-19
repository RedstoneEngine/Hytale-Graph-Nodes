package dev.redengdev.accessors;

import dev.redengdev.data.SpawnerNode;

public interface ContentAccessor {
    SpawnerNode[] getSpawnerNodes();
    void setSpawnerNodes(SpawnerNode[] value);

    double getFurthestNode();
    void setFurthestNode(double value);
}
