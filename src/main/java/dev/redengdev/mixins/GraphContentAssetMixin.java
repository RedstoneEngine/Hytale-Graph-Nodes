package dev.redengdev.mixins;

import java.util.Map;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.DensityContentAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphContentAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.GraphGeneratorAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.MaterialContentAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.PositionsContentAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.graph.PropDistributionContentAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.props.prefabprop.PrefabPropAsset;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.props.PrefabProp;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.logger.HytaleLogger;

import dev.redengdev.accessors.ContentAccessor;
import dev.redengdev.accessors.PrefabPropAccessor;
import dev.redengdev.accessors.PrefabPropAssetAccessor;
import dev.redengdev.data.SpawnerNode;

@Mixin(GraphContentAsset.class)
public abstract class GraphContentAssetMixin {

    @Unique
    SpawnerNode[] spawnerNodes = null;
    @Unique
    boolean generatedSpawnerNodes = false;

    //Modify the return value to add Content
    @Inject(method = "build", at = @At("RETURN"), cancellable = true)
    private void onBuildReturn(
        @Nonnull GraphGeneratorAsset.Argument argument, 
        CallbackInfoReturnable<GraphSpace.Content> cir
    ) {
        GraphSpace.Content content = cir.getReturnValue();

        if (prefabProp != null) {
            preBuild();
            ((ContentAccessor)content).setSpawnerNodes(spawnerNodes);
        }
    }


    //Builds once and stores any needed values
    private void preBuild() {
        generatedSpawnerNodes = true;
        if (prefabProp == null)
            return;

        Prop _prop = ((PrefabPropAssetAccessor)prefabProp).graphBuild();

        if (_prop instanceof PrefabProp prop)
        {
            spawnerNodes = ((PrefabPropAccessor)prop).generateSpawners();
            if (spawnerNodes == null)
                return;
            for (int i = 0; i < spawnerNodes.length; i++)
            {
                HytaleLogger.get("Hyxin-Example").at(Level.INFO).log("TEST" + i + ": " + spawnerNodes[i].position.toString());
            }
        }
    }

    //Previous variables
    @Shadow @Final @Mutable 
    private static AssetBuilderCodec<String, GraphContentAsset> CODEC;
    @Shadow private static Map<String, GraphContentAsset> exportedNodes;
    @Shadow private String id;
    @Shadow private AssetExtraInfo.Data data;
    @Shadow private String exportName;
    @Shadow private String importName;
    @Shadow private DensityContentAsset[] densityContentAssets;
    @Shadow private MaterialContentAsset[] materialContentAssets;
    @Shadow private PropDistributionContentAsset[] propDistributionContentAssets;
    @Shadow private PositionsContentAsset[] positionsContentAssets;
    @Shadow private String[] tags;

    //Custom Variables
    @Unique
    PrefabPropAsset prefabProp = null;

    //Accessors
    private static void setAssetId(GraphContentAsset asset, String value) {
        ((GraphContentAssetMixin) (Object) asset).id = value;
    }
    private static String getAssetId(GraphContentAsset asset) {
        return ((GraphContentAssetMixin) (Object) asset).id;
    }

    private static void setAssetData(GraphContentAsset asset, AssetExtraInfo.Data value) {
        ((GraphContentAssetMixin) (Object) asset).data = value;
    }
    private static AssetExtraInfo.Data getAssetData(GraphContentAsset asset) {
        return ((GraphContentAssetMixin) (Object) asset).data;
    }

    private static void setExportName(GraphContentAsset asset, String value) {
        ((GraphContentAssetMixin) (Object) asset).exportName = value;
    }
    private static String getExportName(GraphContentAsset asset) {
        return ((GraphContentAssetMixin) (Object) asset).exportName;
    }

    private static void setImportName(GraphContentAsset asset, String value) {
        ((GraphContentAssetMixin) (Object) asset).importName = value;
    }
    private static String getImportName(GraphContentAsset asset) {
        return ((GraphContentAssetMixin) (Object) asset).importName;
    }

    private static void setDensityContent(GraphContentAsset asset, DensityContentAsset[] value) {
        ((GraphContentAssetMixin) (Object) asset).densityContentAssets = value;
    }
    private static DensityContentAsset[] getDensityContent(GraphContentAsset asset) {
        return ((GraphContentAssetMixin) (Object) asset).densityContentAssets;
    }

    private static void setMaterialContent(GraphContentAsset asset, MaterialContentAsset[] value) {
        ((GraphContentAssetMixin) (Object) asset).materialContentAssets = value;
    }
    private static MaterialContentAsset[] getMaterialContent(GraphContentAsset asset) {
        return ((GraphContentAssetMixin) (Object) asset).materialContentAssets;
    }

    private static void setPropDistributionContent(GraphContentAsset asset, PropDistributionContentAsset[] value) {
        ((GraphContentAssetMixin) (Object) asset).propDistributionContentAssets = value;
    }
    private static PropDistributionContentAsset[] getPropDistributionContent(GraphContentAsset asset) {
        return ((GraphContentAssetMixin) (Object) asset).propDistributionContentAssets;
    }

    private static void setPositionsContent(GraphContentAsset asset, PositionsContentAsset[] value) {
        ((GraphContentAssetMixin) (Object) asset).positionsContentAssets = value;
    }
    private static PositionsContentAsset[] getPositionsContent(GraphContentAsset asset) {
        return ((GraphContentAssetMixin) (Object) asset).positionsContentAssets;
    }

    private static void setTags(GraphContentAsset asset, String[] value) {
        ((GraphContentAssetMixin) (Object) asset).tags = value;
    }
    private static String[] getTags(GraphContentAsset asset) {
        return ((GraphContentAssetMixin) (Object) asset).tags;
    }

    private static void setPrefabProp(GraphContentAsset asset, PrefabPropAsset value) {
        HytaleLogger.get("Hyxin-Example").at(Level.INFO).log("Set Variable");
        //Do the calcs here??
        ((GraphContentAssetMixin) (Object) asset).prefabProp = value;
    }
    private static PrefabPropAsset getPrefabProp(GraphContentAsset asset) {
        return ((GraphContentAssetMixin) (Object) asset).prefabProp;
    }

    private static void handleAfterDecode(GraphContentAsset asset) {
        String expName = getExportName(asset);
        if (expName != null && !expName.isEmpty()) {
            if (exportedNodes.containsKey(expName)) {
                LoggerUtil.getLogger().warning("Duplicate export name for asset: " + expName);
            }
            exportedNodes.put(expName, asset);
            LoggerUtil.getLogger().fine("Registered imported GraphContent asset with name '" + expName + "' with asset id '" + getAssetId(asset));
        }
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void onClassInit(CallbackInfo ci) {
        CODEC = (AssetBuilderCodec<String, GraphContentAsset>)AssetBuilderCodec.builder(GraphContentAsset.class, GraphContentAsset::new, Codec.STRING,
            GraphContentAssetMixin::setAssetId, GraphContentAssetMixin::getAssetId,
            GraphContentAssetMixin::setAssetData, GraphContentAssetMixin::getAssetData)
        .append(new KeyedCodec<>("ExportName", Codec.STRING, true), GraphContentAssetMixin::setExportName, GraphContentAssetMixin::getExportName)
        .add()
        .append(new KeyedCodec<>("ImportName", Codec.STRING, true), GraphContentAssetMixin::setImportName, GraphContentAssetMixin::getImportName)
        .add()
        .append(new KeyedCodec<>("DensityContent", new ArrayCodec<>(DensityContentAsset.CODEC, (x$0) -> new DensityContentAsset[x$0]), true), GraphContentAssetMixin::setDensityContent, GraphContentAssetMixin::getDensityContent)
        .add()
        .append(new KeyedCodec<>("MaterialContent", new ArrayCodec<>(MaterialContentAsset.CODEC, (x$0) -> new MaterialContentAsset[x$0]), true), GraphContentAssetMixin::setMaterialContent, GraphContentAssetMixin::getMaterialContent)
        .add()
        .append(new KeyedCodec<>("PropDistributionContent", new ArrayCodec<>(PropDistributionContentAsset.CODEC, (x$0) -> new PropDistributionContentAsset[x$0]), true), GraphContentAssetMixin::setPropDistributionContent, GraphContentAssetMixin::getPropDistributionContent)
        .add()
        .append(new KeyedCodec<>("PositionsContent", new ArrayCodec<>(PositionsContentAsset.CODEC, (x$0) -> new PositionsContentAsset[x$0]), true), GraphContentAssetMixin::setPositionsContent, GraphContentAssetMixin::getPositionsContent)
        .add()
        .append(new KeyedCodec<>("ContentTags", new ArrayCodec<>(Codec.STRING, (x$0) -> new String[x$0]), true), GraphContentAssetMixin::setTags, GraphContentAssetMixin::getTags)
        .add()
        .append(new KeyedCodec<>("PrefabNodes", PrefabPropAsset.CODEC), GraphContentAssetMixin::setPrefabProp, GraphContentAssetMixin::getPrefabProp)
        .add()
        .afterDecode(GraphContentAssetMixin::handleAfterDecode)
        .build();
        
        HytaleLogger.get("Hyxin").at(Level.INFO).log("Test, test, this init has been patched");
    }

    @Inject(method = "cleanUp", at = @At("TAIL"))
    private void addToCleanUp(CallbackInfo ci)
    {
        if (prefabProp != null)
            prefabProp.cleanUp();
    }
}
