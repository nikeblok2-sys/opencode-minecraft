package net.example.firstmod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.example.firstmod.ExampleMod;
import net.minecraft.server.MinecraftServer;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;

public class ModConfigManager {

    private static ModConfigManager instance;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final AtomicLong configVersion = new AtomicLong(0);
    private Path configDir;

    private ModConfigManager() {}

    public static ModConfigManager get() {
        if (instance == null) {
            instance = new ModConfigManager();
        }
        return instance;
    }

    public void init(MinecraftServer server) {
        this.configDir = server.getServerDirectory().resolve("config").resolve("firstmod");
        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            ExampleMod.LOGGER.error("Failed to create config dir", e);
        }
    }

    public <T> T loadOrCreate(String fileName, Class<T> clazz, T defaults) {
        Path file = configDir.resolve(fileName);
        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file)) {
                T result = gson.fromJson(reader, clazz);
                if (result != null) return result;
            } catch (Exception e) {
                ExampleMod.LOGGER.error("Failed to load " + fileName, e);
            }
        }
        save(fileName, defaults);
        return defaults;
    }

    public <T> void save(String fileName, T value) {
        Path file = configDir.resolve(fileName);
        try (Writer writer = Files.newBufferedWriter(file)) {
            gson.toJson(value, writer);
        } catch (Exception e) {
            ExampleMod.LOGGER.error("Failed to save " + fileName, e);
        }
    }

    public long bumpVersion() {
        return configVersion.incrementAndGet();
    }

    public long getVersion() {
        return configVersion.get();
    }
}
