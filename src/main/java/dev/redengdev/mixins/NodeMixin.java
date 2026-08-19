package dev.redengdev.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;

import dev.redengdev.accessors.NodeAccessor;

@Mixin(GraphSpace.Node.class)
public class NodeMixin implements NodeAccessor {

    @Unique
    int rotationIndex = 0;

    @Override
    public int getRotationIndex() {
        return rotationIndex;
    }

    @Override
    public void setRotationIndex(int value) {
        rotationIndex = value;
    }


}
