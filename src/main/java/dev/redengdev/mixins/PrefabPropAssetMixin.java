package dev.redengdev.mixins;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.hypixel.hytale.builtin.hytalegenerator.WeightedMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.props.prefabprop.PrefabPropAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.props.prefabprop.PrefabPropAsset.WeightedPathAsset;
import com.hypixel.hytale.builtin.hytalegenerator.material.MaterialCache;
import com.hypixel.hytale.builtin.hytalegenerator.props.EmptyProp;
import com.hypixel.hytale.builtin.hytalegenerator.props.PrefabProp;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import com.hypixel.hytale.builtin.hytalegenerator.rng.SeedBox;
import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.IPrefabBuffer;

import dev.redengdev.accessors.PrefabPropAssetAccessor;
import dev.redengdev.accessors.WeightedPathAssetAccessor;

@Mixin(PrefabPropAsset.class)
public class PrefabPropAssetMixin implements PrefabPropAssetAccessor {

    @Shadow private WeightedPathAsset[] weightedPrefabPathAssets;

    @Shadow
    @Nullable
    private List<IPrefabBuffer> loadPrefabBuffersFrom(@Nonnull String path) {
        return null;
    }

    @Override
    public Prop graphBuild() {
        if (this.weightedPrefabPathAssets.length != 0) {

            //Evaluated once so can't do per node randomness, use weighted Content Supplier for that
            //So instead only the first path is taken, (if subPrefabs, still will only choose the first for now)

            WeightedMap<List<IPrefabBuffer>> prefabWeightedMap = new WeightedMap<>();
            List<IPrefabBuffer> pathPrefabs = this.loadPrefabBuffersFrom(((WeightedPathAssetAccessor)weightedPrefabPathAssets[0]).getPath());
            prefabWeightedMap.add(pathPrefabs, (double)1.0F);

            return new PrefabProp(prefabWeightedMap, new MaterialCache(), new SeedBox(0), this::loadPrefabBuffersFrom);
        }
        else
        {
            return EmptyProp.INSTANCE;
        }
    }
}
