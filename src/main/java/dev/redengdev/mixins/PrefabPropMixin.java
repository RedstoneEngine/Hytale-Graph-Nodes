package dev.redengdev.mixins;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.hypixel.hytale.builtin.hytalegenerator.WeightedMap;
import com.hypixel.hytale.builtin.hytalegenerator.props.PrefabProp;
import com.hypixel.hytale.builtin.hytalegenerator.rng.RngField;
import com.hypixel.hytale.math.util.FastRandom;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.prefab.PrefabRotation;
import com.hypixel.hytale.server.core.prefab.selection.buffer.PrefabBufferCall;
import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.IPrefabBuffer;

import dev.redengdev.accessors.PrefabPropAccessor;
import dev.redengdev.data.SpawnerNode;

@Mixin(PrefabProp.class)
public class PrefabPropMixin implements PrefabPropAccessor {

    @Unique
    private static int nodeBlockId = -1;

    @Shadow private WeightedMap<List<IPrefabBuffer>> prefabPool;
    @Shadow private RngField rngField;
    @Shadow private FastRandom random;

    @Override
    public SpawnerNode[] generateSpawners()
    {
        if (this.prefabPool.size() == 0)
            return null;

        if (nodeBlockId == -1)
            nodeBlockId = BlockType.getAssetMap().getIndex("GraphNode");

        List<SpawnerNode> spawnerNodes = new ArrayList<SpawnerNode>();

        this.random.setSeed((long)this.rngField.get(0, 0, 0));
        PrefabBufferCall callInstance = new PrefabBufferCall(this.random, PrefabRotation.ROTATION_0);
        IPrefabBuffer prefab = this.pickPrefab(this.random);

        if (prefab == null)
            return null;

        prefab.forEach(IPrefabBuffer.iterateAllColumns(), (x, y, z, blockId, holder, support, rotation, filler, call, fluidId, fluidLevel) -> {
                if (blockId == nodeBlockId)
                {
                    int rotationIndex = RotationTuple.compose(RotationTuple.of(Rotation.OneEighty, Rotation.None), RotationTuple.get(rotation)).index();
                    spawnerNodes.add(new SpawnerNode(
                        new Vector3d(x, y, z),
                        rotationIndex, null));
                }
            }, (cx, cz, entityWrappers, buffer) -> {
            }, (x, y, z, path, fitHeightmap, inheritSeed, inheritHeightCondition, weights, rotation, t) -> {
            }, callInstance);
        
        if (spawnerNodes.size() == 0)
            return null;

        return spawnerNodes.toArray(new SpawnerNode[0]);
    }

    @Shadow
    private IPrefabBuffer pickPrefab(@Nonnull Random rand) {
        return null;
    }
}
