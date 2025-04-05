package today.netshield.velocity.config;

import lombok.Getter;
import lombok.SneakyThrows;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Getter
public class ConfigManager {
    private final File dataFolder;
    private CommentedConfigurationNode config;

    public ConfigManager(File dataFolder) {
        this.dataFolder = dataFolder;
        this.dataFolder.mkdir();

        this.config = this.loadConfig("config");
    }

    @SneakyThrows
    public CommentedConfigurationNode loadConfig(String s) {
        File file = new File(dataFolder, s + ".yml");

        if (!file.exists()) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream(s + ".yml")) {
                file.createNewFile();
                assert in != null;
                Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }

        YamlConfigurationLoader yamlConfigurationLoader = YamlConfigurationLoader.builder()
                .path(file.toPath())
                .build();

        CommentedConfigurationNode config;
        try {
            config = yamlConfigurationLoader.load();
            return config;
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reloads the configuration from disk
     *
     * @return true if the reload was successful, false otherwise
     */
    public boolean reload() {
        try {
            this.config = this.loadConfig("config");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}