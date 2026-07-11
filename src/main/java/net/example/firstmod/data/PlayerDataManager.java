package net.example.firstmod.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.example.firstmod.ExampleMod;
import net.minecraft.server.MinecraftServer;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {

    private static PlayerDataManager instance;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Map<UUID, PlayerData> cache = new ConcurrentHashMap<>();
    private Path dataDir;

    private PlayerDataManager() {}

    public static PlayerDataManager get() {
        if (instance == null) {
            instance = new PlayerDataManager();
        }
        return instance;
    }

    public void init(MinecraftServer server) {
        this.dataDir = server.getServerDirectory().resolve("data").resolve("firstmod").resolve("players");
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            ExampleMod.LOGGER.error("Failed to create player data dir", e);
        }
    }

    public synchronized PlayerData getOrCreate(UUID playerUuid) {
        return cache.computeIfAbsent(playerUuid, uuid -> {
            Path file = dataDir.resolve(uuid.toString() + ".json");
            if (Files.exists(file)) {
                try (Reader reader = Files.newBufferedReader(file)) {
                    PlayerData data = gson.fromJson(reader, PlayerData.class);
                    if (data != null) return data;
                } catch (Exception e) {
                    ExampleMod.LOGGER.error("Failed to load player data for " + uuid, e);
                }
            }
            return new PlayerData();
        });
    }

    public synchronized void save(UUID playerUuid) {
        PlayerData data = cache.get(playerUuid);
        if (data == null) return;
        Path file = dataDir.resolve(playerUuid.toString() + ".json");
        try (Writer writer = Files.newBufferedWriter(file)) {
            gson.toJson(data, writer);
        } catch (Exception e) {
            ExampleMod.LOGGER.error("Failed to save player data for " + playerUuid, e);
        }
    }

    public synchronized void saveAll() {
        for (UUID uuid : cache.keySet()) {
            save(uuid);
        }
    }
}
