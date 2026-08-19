package dev.redengdev.mixins;

import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.AnchorPropDistribution;
import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.PropDistribution;

@Mixin(AnchorPropDistribution.class)
public class AnchorPropDistributionMixin {

    @Shadow private PropDistribution propDistribution;
    @Shadow private boolean isReversed;

    @Inject(
        method = "distribute(Lcom/hypixel/hytale/builtin/hytalegenerator/propdistributions/PropDistribution$Context;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onDistribute(PropDistribution.Context context, CallbackInfo ci) {
        // 1. Cancel the native unsafe distribute method completely
        ci.cancel();

        if (context.anchor == null) {
            this.propDistribution.distribute(context);
        } else {
            final PropDistribution.Context rContext = context;
            final Vector3d rAnchor = new Vector3d(context.anchor);
            final Bounds3d rOffsetBounds = new Bounds3d(context.bounds);

            if (this.isReversed) {
                rOffsetBounds.offset(rAnchor);
            } else {
                rOffsetBounds.offsetOpposite(rAnchor);
            }

            final PropDistribution.Context rChildContext = new PropDistribution.Context(context);
            rChildContext.bounds = rOffsetBounds;
            rChildContext.pipe = (position, prop, _control) -> {
                final Vector3d rNewPosition = new Vector3d(position);
                if (isReversed) {
                    rNewPosition.sub(rAnchor);
                } else {
                    rNewPosition.add(rAnchor);
                }
                if (rContext.bounds.contains(rNewPosition)) {
                    rContext.pipe.accept(rNewPosition, prop, _control);
                }
            };
            this.propDistribution.distribute(rChildContext);
        }
    }
}
