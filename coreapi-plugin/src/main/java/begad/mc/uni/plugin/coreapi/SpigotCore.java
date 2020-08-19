package begad.mc.uni.plugin.coreapi;

import begad.mc.spi.utils.SpigotUpdates;
import begad.mc.utils.UpdateAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotCore extends JavaPlugin {
    private SpigotUpdates updates;

    @Override
    public void onLoad() {
        updates = new SpigotUpdates(this, "CoreAPI", null, null, "v1.2", UpdateAPI.SPIGET);
    }

    @Override
    public void onEnable() {
        getLogger().info("Checking for updates");
        String current = getDescription().getVersion();
        String latest = updates.getLatestVersion();
        if (latest == null) {
            getLogger().warning("Couldn't check for updates");
            updates.setMessage("Couldn't check for updates");
        } else {
            if (current.compareTo(latest) < 0) {
                getLogger().info("New version: " + latest);
                updates.setMessage("New version: " + latest);
            } else if (current.compareTo(latest) == 0) {
                getLogger().info("You are up to date");
                updates.setMessage("You are up to date");
            } else if (current.compareTo(latest) > 0) {
                if (current.compareTo(updates.getCompileCurrentVersion()) != 0) {
                    getLogger().warning("Please reinstall the plugin");
                    updates.setMessage("Please reinstall the plugin");
                } else {
                    getLogger().info("You are up to date");
                    updates.setMessage("You are up to date");
                }
            }
        }
    }

    @Override
    public void onDisable() {

    }
}
