package dev.redengdev;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.hypixel.hytale.server.core.asset.AssetModule;

public class ModifyWorkspace {

    static Path graphContent;
    static String defaultFileString;

    public static void loadWorkspace()
    {
        GraphNodes.LOGGER.atInfo().log("Looking for Workspace");
        Path assetsPath = AssetModule.get().getBaseAssetPack().getPackLocation();
        Path workspacesPath = assetsPath.getParent().resolve("Client").resolve("NodeEditor").resolve("Workspaces").resolve("HytaleGenerator Java");
        if (Files.exists(workspacesPath)) {
            GraphNodes.LOGGER.atInfo().log("Found Workspace");

            graphContent = workspacesPath.resolve("Graph").resolve("NodeContent").resolve("NodeContent.Graph.json");
            if (Files.exists(graphContent))
            {
                GraphNodes.LOGGER.atInfo().log("Found Graph Content");
                try
                {
                    defaultFileString = Files.readString(graphContent);
                    Files.writeString(graphContent, readFile());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void resetWorkspace()
    {
            if (Files.exists(graphContent))
            {
                GraphNodes.LOGGER.atInfo().log("Found Graph Content to Reset");
                try
                {
                    Files.writeString(graphContent, defaultFileString);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
    }

    private static String readFile()
    {
        try (InputStream inputStream = ModifyWorkspace.class.getClassLoader()
            .getResourceAsStream("NodeContent.Graph.json")) {
        
        if (inputStream == null) {
            throw new IllegalArgumentException("Resource file not found!");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String content = reader.readAllAsString();
            return content;
        }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
