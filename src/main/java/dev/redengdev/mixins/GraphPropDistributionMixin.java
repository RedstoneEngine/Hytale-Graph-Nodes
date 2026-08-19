package dev.redengdev.mixins;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hypixel.hytale.builtin.hytalegenerator.GridCache;
import com.hypixel.hytale.builtin.hytalegenerator.VectorUtil;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphGenerator;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.material.MaterialCache;
import com.hypixel.hytale.builtin.hytalegenerator.pipe.Control;
import com.hypixel.hytale.builtin.hytalegenerator.pipe.Pipe;
import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.GraphPropDistribution;
import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.GraphPropDistribution.PropDistributionContent;
import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.PropDistribution;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import com.hypixel.hytale.builtin.hytalegenerator.props.StaticRotatorProp;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;

import dev.redengdev.accessors.GraphPropDistributionAccessor;
import dev.redengdev.accessors.NodeAccessor;

@Mixin(GraphPropDistribution.class)
public class GraphPropDistributionMixin implements GraphPropDistributionAccessor {

    @Unique MaterialCache materialCache;

    @Shadow @Final private static Comparator<GraphSpace.Node> nodeComparator;
    @Shadow private List<PropDistributionContent> indexedContent;
    @Shadow private boolean isEmpty;
    @Shadow private double contentRadius;
    @Shadow private GridCache<GraphSpace.ReadOnly> grid;
    @Shadow private GraphGenerator graphGenerator;

    @Inject(
        method = "distribute(Lcom/hypixel/hytale/builtin/hytalegenerator/propdistributions/PropDistribution$Context;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onDistribute(PropDistribution.Context context, CallbackInfo ci) {
        // 1. Cancel the native unsafe distribute method completely
        ci.cancel();

        //Rewritten using finals
        if (!this.isEmpty) {
            final Bounds3d rGraphBounds = new Bounds3d(context.bounds).expand(this.contentRadius);
            final Vector3d rBoundsMaxInclusive = new Vector3d(rGraphBounds.max);
            VectorUtil.nextDown(rBoundsMaxInclusive);

            final Vector3i rMinCellIndex = new Vector3i();
            final Vector3i rMaxCellIndex = new Vector3i();
            this.grid.toCellIndex(rGraphBounds.min, rMinCellIndex);
            this.grid.toCellIndex(rBoundsMaxInclusive, rMaxCellIndex);
            ++rMaxCellIndex.x;
            ++rMaxCellIndex.y;
            ++rMaxCellIndex.z;
            final Control rControl = new Control();
            final Vector3i rCellIndex = new Vector3i(rMinCellIndex);

            //Used in loop
            final Bounds3d rIntersectingCellBounds = new Bounds3d();
            final List<GraphSpace.Node> rResultList = new ArrayList<>();

            while(rCellIndex.x < rMaxCellIndex.x) {
                for(rCellIndex.y = rMinCellIndex.y; rCellIndex.y < rMaxCellIndex.y; ++rCellIndex.y) {
                    for(rCellIndex.z = rMinCellIndex.z; rCellIndex.z < rMaxCellIndex.z; ++rCellIndex.z) {
                        final GraphSpace.ReadOnly graph = (GraphSpace.ReadOnly)this.grid.getCell(rCellIndex, this.graphGenerator);

                        assert graph != null;

                        if (graph != null) {
                            this.grid.toCellBounds(rCellIndex, rIntersectingCellBounds);
                            rIntersectingCellBounds.intersect(rGraphBounds);
                            VectorUtil.nextDown(rIntersectingCellBounds.max);
                            rResultList.clear();
                            graph.getNodes(rIntersectingCellBounds, rResultList);
                            rResultList.sort(nodeComparator);

                            for(int i = 0; i < rResultList.size(); ++i) {
                                final GraphSpace.Node node = (GraphSpace.Node)rResultList.get(i);
                                if (rControl.stop) {
                                    rResultList.clear();
                                    return;
                                }

                                runOnNodeOverride(node, rControl, context);
                            }

                            rResultList.clear();
                        }
                    }
                }

                ++rCellIndex.x;
            }
        }
    }

    @Unique
    private void runOnNodeOverride(GraphSpace.Node node, Control control, PropDistribution.Context context) {
        final int contentIndex = node.content().index;
        if (contentIndex != -1) {
            final PropDistributionContent propContent = (PropDistributionContent)this.indexedContent.get(contentIndex);
            if (propContent != null) {
                //Final Var
                final Bounds3d rContentBounds = new Bounds3d();

                rContentBounds.min.set(-propContent.radius);
                rContentBounds.max.set(propContent.radius);
                rContentBounds.offset(node.position());
                if (rContentBounds.intersects(context.bounds)) {

                    //Final Vars
                    final PropDistribution.Context rChildContext = new PropDistribution.Context(context);
                    final Vector3d rAnchor = new Vector3d(node.position());
                    final double contentRadiusSquared = propContent.rangeSquared;
                    final Pipe.Two<Vector3d, Prop> rContextPipe = context.pipe;
                    final Vector3dc rNodePosition = node.position();

                    rChildContext.bounds = rContentBounds;
                    rChildContext.anchor = rAnchor;
                    rChildContext.graphNode = node;

                    //Local call w/ variables
                    rChildContext.pipe = (position, prop, _control) -> {
                        final double distanceSqrToNode = position.distanceSquared(rNodePosition);
                        if (!(distanceSqrToNode >= contentRadiusSquared)) {
                            assert rContextPipe != null;

                            if (((NodeAccessor)node).getRotationIndex() == 0)
                                rContextPipe.accept(position, prop, _control);
                            else
                            {
                                //I hope I don't need a material cache :( (I do)
                                Prop middleManProp = new StaticRotatorProp(prop, RotationTuple.get(((NodeAccessor)node).getRotationIndex()), materialCache);
                                rContextPipe.accept(position, middleManProp, _control);
                            }
                        }
                    };

                    propContent.propDistribution.distribute(rChildContext);
                }
            }
        }
    }

    @Override
    public void setMaterialCache(MaterialCache materialCache) {
        this.materialCache = materialCache;
    }
}
