package dev.redengdev;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.util.Config;

import dev.redengdev.commands.ExampleCommand;
import dev.redengdev.config.ExampleConfig;
import dev.redengdev.data.GraphNodeBlock;
import dev.redengdev.events.ExampleEvent;

import javax.annotation.Nonnull;

public class GraphNodes extends JavaPlugin {
    private static GraphNodes instance;
    private ComponentType<ChunkStore, GraphNodeBlock> exampleBlockComponentType;

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static Config<ExampleConfig> config = null;

    public static GraphNodes get() {
        return instance;
    }

    public GraphNodes(@Nonnull JavaPluginInit init) {
        super(init);
        config = this.withConfig("example_config", ExampleConfig.CODEC);
    }

    @Override
    protected void setup() {
        instance = this;
        LOGGER.atInfo().log("my example plugin just loaded");
        config.save();
        this.getCommandRegistry().registerCommand(new ExampleCommand("example", "An example command"));
        if (getConfig().get().isEnabledWelcomeMessage()) {
            this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, ExampleEvent::onPlayerReady);
        }
        ModifyWorkspace.loadWorkspace();
        this.exampleBlockComponentType = this.getChunkStoreRegistry().registerComponent(GraphNodeBlock.class, "GraphNodeBlock", GraphNodeBlock.CODEC);
    }

    public static Config<ExampleConfig> getConfig() {
        return config;
    }

    @Override
    protected void shutdown() {
        ModifyWorkspace.resetWorkspace();
    }

    public ComponentType<ChunkStore, GraphNodeBlock> getExampleBlockComponentType() {
        return this.exampleBlockComponentType;
    }
}