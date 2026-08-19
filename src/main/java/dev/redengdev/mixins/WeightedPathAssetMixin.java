package dev.redengdev.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.hypixel.hytale.builtin.hytalegenerator.assets.props.prefabprop.PrefabPropAsset.WeightedPathAsset;

import dev.redengdev.accessors.WeightedPathAssetAccessor;

@Mixin(WeightedPathAsset.class)
public class WeightedPathAssetMixin implements WeightedPathAssetAccessor {

    @Shadow private double weight;
    @Shadow private String path;

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public double getWeight() {
        return weight;
    }
}
