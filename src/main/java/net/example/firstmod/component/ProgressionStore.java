package net.example.firstmod.component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProgressionStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "firstmod_progression.json";

    private final Map<UUID, ProgressionData> dataMap = new HashMap<>();
    private final Path dataFile;

    public ProgressionStore(MinecraftServer server, Path dataDir) {
        this.dataFile = dataDir.resolve(FILE_NAME);
    }

    public ProgressionData get(UUID uuid) {
        return dataMap.computeIfAbsent(uuid, k -> new ProgressionData());
    }

    public void load() {
        if (!Files.exists(dataFile)) return;
        try {
            String json = Files.readString(dataFile);
            Map<String, ProgressionData> raw = GSON.fromJson(json, new TypeToken<Map<String, ProgressionData>>(){}.getType());
            if (raw != null) {
                dataMap.clear();
                for (var entry : raw.entrySet()) {
                    ProgressionData data = entry.getValue();
                    if (data.statLevels.length != StatRegistry.count()) {
                        int[] old = data.statLevels;
                        data.statLevels = new int[StatRegistry.count()];
                        System.arraycopy(old, 0, data.statLevels, 0, Math.min(old.length, StatRegistry.count()));
                    }
                    dataMap.put(UUID.fromString(entry.getKey()), data);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load progression data: " + e.getMessage());
        }
    }

    public void save() {
        try {
            Files.createDirectories(dataFile.getParent());
            Map<String, ProgressionData> raw = new HashMap<>();
            for (var entry : dataMap.entrySet()) {
                raw.put(entry.getKey().toString(), entry.getValue());
            }
            Files.writeString(dataFile, GSON.toJson(raw));
        } catch (IOException e) {
            System.err.println("Failed to save progression data: " + e.getMessage());
        }
    }

    public static ProgressionStore getOrCreate(MinecraftServer server) {
        Path dataDir = server.getWorldPath(LevelResource.ROOT).resolve("data");
        ProgressionStore store = StoreHolder.get(server);
        if (store == null) {
            store = new ProgressionStore(server, dataDir);
            store.load();
            StoreHolder.set(server, store);
        }
        return store;
    }

    private static class StoreHolder {
        private static MinecraftServer currentServer;
        private static ProgressionStore currentStore;

        static synchronized ProgressionStore get(MinecraftServer server) {
            return server == currentServer ? currentStore : null;
        }

        static synchronized void set(MinecraftServer server, ProgressionStore store) {
            currentServer = server;
            currentStore = store;
        }
    }
}
