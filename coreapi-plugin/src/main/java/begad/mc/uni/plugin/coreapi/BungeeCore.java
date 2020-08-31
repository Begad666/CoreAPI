package begad.mc.uni.plugin.coreapi;

import begad.mc.bc.utils.BungeeConfig;
import begad.mc.bc.utils.BungeeUpdates;
import begad.mc.utils.UpdateAPI;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeCore extends Plugin {
	private BungeeUpdates updates;
	private BungeeConfig config;

	@Override
	public void onLoad() {
		updates = new BungeeUpdates(this, "CoreAPI", "82952", null, "1.3.3", UpdateAPI.SPIGET);
		config = new BungeeConfig(this, null, true, "", "messages");
	}

	@Override
	public void onEnable() {
		boolean result = config.check();
		config.loadMessagesFile("lang");
		if (config.get().getBoolean("check-for-updates-on-startup")) {
			String current = getDescription().getVersion();
			String latest = updates.getLatestVersion();
			if (latest == null) {
				getLogger().warning(config.getMessages("lang").getString("updates.error-check"));
				updates.setMessage(config.getMessages("lang").getString("updates.error-check"));
			} else {
				if (current.compareTo(latest) < 0) {
					getLogger().info(config.getMessages("lang").getString("updates.new") + latest);
					updates.setMessage(config.getMessages("lang").getString("updates.new") + latest);
				} else if (current.compareTo(latest) == 0) {
					getLogger().info(config.getMessages("lang").getString("updates.up-to-date"));
					updates.setMessage(config.getMessages("lang").getString("updates.up-to-date"));
				} else if (current.compareTo(latest) > 0) {
					if (current.compareTo(updates.getCompileCurrentVersion()) != 0) {
						getLogger().warning(config.getMessages("lang").getString("updates.error-changed"));
						updates.setMessage(config.getMessages("lang").getString("updates.error-changed"));
					} else {
						getLogger().info(config.getMessages("lang").getString("updates.up-to-date"));
						updates.setMessage(config.getMessages("lang").getString("updates.up-to-date"));
					}
				}
			}
		}
	}

	@Override
	public void onDisable() {

	}
}
