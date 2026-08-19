package dev.redengdev.mixins;

import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.graph.contentsuppliers.ContentSupplier;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.ContentNodeAction;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;

import dev.redengdev.accessors.ContentAccessor;
import dev.redengdev.accessors.NodeAccessor;
import dev.redengdev.data.SpawnerNode;

@Mixin(ContentNodeAction.class)
public class ContentNodeActionMixin {

    @Shadow private ContentSupplier contentSupplier;

    @Inject(method = "run", at = @At("TAIL"))
    private void onRun(@Nonnull GraphSpace graphSpace, @Nonnull GraphSpace.Node node, CallbackInfo ci) {
        SpawnerNode[] spawnerNodes = ((ContentAccessor)this.contentSupplier.get(node)).getSpawnerNodes();
        RotationTuple nodeRotation = RotationTuple.get(((NodeAccessor)node).getRotationIndex());
        HytaleLogger.get("Hyxin-Example").at(Level.INFO).log("Running Content Node Action");
        if (spawnerNodes != null)
        {
            HytaleLogger.get("Hyxin-Example").at(Level.INFO).log("Running Spawner Nodes");
            for (SpawnerNode spawnerNode : spawnerNodes) {
                Vector3d taskPosition = new Vector3d(spawnerNode.position);
                nodeRotation.applyRotationTo(taskPosition);
                taskPosition.add(node.position());
                int rotationIndex = RotationTuple.compose(nodeRotation, RotationTuple.get(spawnerNode.rotationIndex)).index();
                graphSpace.schedule(() -> {
                    GraphSpace.Node newNode = graphSpace.createNode(taskPosition);
                    HytaleLogger.get("Hyxin-Example").at(Level.INFO).log("Position: " + taskPosition + "Rotation Index: " + rotationIndex);
                    ((NodeAccessor)newNode).setRotationIndex(rotationIndex);
                    graphSpace.getOrCreateEdge(node, newNode);
                });
            }
        }
    }

    @Inject(
        method = "getLongestCreatedEdgeLength", 
        at = @At("RETURN"), 
        cancellable = true
    )
    private void onGetLongestCreatedEdgeLengthReturn(CallbackInfoReturnable<Double> cir) {
        //Need to set this dynamically at some point
        cir.setReturnValue(20.0);
    }
}
