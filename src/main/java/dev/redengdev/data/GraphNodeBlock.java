package dev.redengdev.data;

import javax.annotation.Nullable;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

import dev.redengdev.GraphNodes;

public class GraphNodeBlock implements Component<ChunkStore> {

    private String contentTags;

    public static final BuilderCodec<GraphNodeBlock> CODEC = BuilderCodec.builder(GraphNodeBlock.class, GraphNodeBlock::new)
        .append(
            new KeyedCodec<>("ContentTags", Codec.STRING),
            (state, s) -> state.contentTags = s,
            state -> state.contentTags
        ).add()
        .build();

    public GraphNodeBlock(){}

    public GraphNodeBlock(String contentTags)
    {
        this.contentTags = contentTags;
    }

    public static ComponentType<ChunkStore, GraphNodeBlock> getComponentType() {
        return GraphNodes.get().getExampleBlockComponentType();
    }

    @Nullable
    public Component<ChunkStore> clone() {
        return new GraphNodeBlock(this.contentTags);
    }
}
