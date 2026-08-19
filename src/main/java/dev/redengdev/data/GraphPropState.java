package dev.redengdev.data;

import javax.annotation.Nullable;

import org.joml.Vector3d;
import org.joml.Vector3dc;

import com.hypixel.hytale.builtin.hytalegenerator.pipe.Pipe;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;

public final class GraphPropState {
    @Nullable
    public Vector3dc rNodePosition;
    @Nullable
    public Pipe.Two<Vector3d, Prop> rContextPipe;
}
