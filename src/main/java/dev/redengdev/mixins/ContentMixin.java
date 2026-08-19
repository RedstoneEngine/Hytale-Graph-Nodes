package dev.redengdev.mixins;

import java.util.logging.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.logger.HytaleLogger;

import dev.redengdev.accessors.ContentAccessor;
import dev.redengdev.data.SpawnerNode;

@Mixin(GraphSpace.Content.class)
public class ContentMixin implements ContentAccessor {

    @Unique
    SpawnerNode[] spawnerNodes = null;
    @Unique
    double furthestNode = 0.0;

    @Override
    public SpawnerNode[] getSpawnerNodes() {
        return spawnerNodes;
    }

    @Override
    public void setSpawnerNodes(SpawnerNode[] value) {
        spawnerNodes = value;
        HytaleLogger.get("Hyxin-Example").at(Level.INFO).log("Assigned Spawners");
    }

    @Override
    public double getFurthestNode() {
        return furthestNode;
    }

    @Override
    public void setFurthestNode(double value) {
        furthestNode = value;
    }
}
