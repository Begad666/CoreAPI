package begad.mc.uni.plugin.coreapi;

import begad.mc.spi.utils.SpigotUpdates;
import begad.mc.utils.UpdateAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class SpigotCore extends JavaPlugin {
    private SpigotUpdates updates;

    @Override
    public void onLoad() {
        updates = new SpigotUpdates(this, "CoreAPI", "82952", null, "1.3.5", UpdateAPI.SPIGET);
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        FileConfiguration configuration = this.getConfig();
        File dir = new File(getDataFolder() + "/messages");
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                this.getLogger().severe("Cannot create directories");
                return;
            }
        }
        File langFile = new File(dir, "lang.yml");
        if (!langFile.exists()) {
            try (InputStream in = this.getResource("messages/lang.yml")) {
                if (in == null) {
                    this.getLogger().severe("No language file found");
                    return;
                }
                Files.copy(in, langFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfiguration langConfiguration = YamlConfiguration.loadConfiguration(langFile);
        if (configuration.getBoolean("check-for-updates-on-startup")) {
            getLogger().info(langConfiguration.getString("updates.start"));
            String current = getDescription().getVersion();
            String latest = updates.getLatestVersion();
            if (latest == null) {
                getLogger().warning(langConfiguration.getString("updates.error-check"));
                updates.setMessage(langConfiguration.getString("updates.error-check"));
            } else {
                if (current.compareTo(latest) < 0) {
                    getLogger().info(langConfiguration.getString("updates.new") + latest);
                    updates.setMessage(langConfiguration.getString("updates.new") + latest);
                } else if (current.compareTo(latest) == 0) {
                    getLogger().info(langConfiguration.getString("updates.up-to-date"));
                    updates.setMessage(langConfiguration.getString("updates.up-to-date"));
                } else if (current.compareTo(latest) > 0) {
                    if (current.compareTo(updates.getCompileCurrentVersion()) != 0) {
                        getLogger().warning(langConfiguration.getString("updates.error-changed"));
                        updates.setMessage(langConfiguration.getString("updates.error-changed"));
                    } else {
                        getLogger().info(langConfiguration.getString("updates.up-to-date"));
                        updates.setMessage(langConfiguration.getString("updates.up-to-date"));
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {

    }
}
