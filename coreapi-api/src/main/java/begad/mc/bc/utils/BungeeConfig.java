package begad.mc.bc.utils;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class BungeeConfig {
	private File file;
	private Configuration config;
	private String messagesFilePrefix;
	private String messageFolder;
	private Map<String, Configuration> messages;
	private Map<String, File> messagesFiles;
	private boolean messagesEnabled = false;
	private Plugin plugin;
	private BungeeUpdates bungeeUpdates;

	public BungeeConfig(Plugin Plugin, BungeeUpdates BungeeUpdates, boolean enableMessages, String messagesFilePrefix, String messageFolder) throws NullPointerException {
		if (Plugin != null) {
			plugin = Plugin;
		} else {
			throw new NullPointerException("Plugin cannot be null");
		}
		bungeeUpdates = BungeeUpdates;
		if (enableMessages) {
			this.messagesFilePrefix = messagesFilePrefix;
			this.messageFolder = messageFolder;
			this.messagesEnabled = true;
			this.messages = new HashMap<>();
			this.messagesFiles = new HashMap<>();
		}
	}

	public boolean checkDataFolder() {
		if (!plugin.getDataFolder().exists()) {
			boolean result = plugin.getDataFolder().mkdir();
			if (!result) {
				plugin.getLogger().severe("Cannot make directory for data");
				return true;
			}
		}
		return false;
	}

	public boolean check() {
		file = new File(plugin.getDataFolder(), "config.yml");

		if (!file.exists()) {
			return load();
		} else {
			String cv = null;
			try {
				cv = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(plugin.getDataFolder(), "config.yml")).getString("config-version");
			} catch (IOException e) {
				if (bungeeUpdates != null) {
					plugin.getLogger().severe("Cannot access the config version, please check it, loading...");
					return load();
				}
			}
			if (cv != null && bungeeUpdates != null) {
				if (cv.compareTo(bungeeUpdates.getCompileConfigVersion()) < 0) {
					plugin.getLogger().warning("Config isn't up-to-date, please delete it and reload to generate the new version, loading...");
					return load();
				} else if (cv.compareTo(bungeeUpdates.getCompileConfigVersion()) == 0) {
					return load();
				} else if (cv.compareTo(bungeeUpdates.getCompileConfigVersion()) > 0) {
					plugin.getLogger().severe("You are using a version of a config which is higher than this version requires, if you did downgrade, reset the config by deleting it, loading...");
					return load();
				}
			} else if (bungeeUpdates != null) {
				plugin.getLogger().severe("Cannot access the config version, please check it, loading...");
				return load();
			}
		}
		return load();
	}

	private boolean load() {
		if (checkDataFolder()) {
			return false;
		}

		file = new File(plugin.getDataFolder(), "config.yml");

		if (!file.exists()) {
			try (InputStream in = plugin.getResourceAsStream("config.yml")) {
				Files.copy(in, file.toPath());
			} catch (IOException e) {
				plugin.getLogger().severe("Cannot copy config to data directory, cannot load the plugin");
				return false;
			}
		}
		if (file.exists()) {
			try {
				config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
				return true;
			} catch (IOException e) {
				plugin.getLogger().severe("Cannot load the config file, cannot load the plugin");
				return false;
			}
		}
		return false;
	}

	public Configuration get() {
		return config;
	}

	public void save() {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(plugin.getDataFolder(), "config.yml"));
		} catch (IOException e) {
			plugin.getLogger().severe("There was an error while saving the config to file");
		}
	}

	public boolean loadMessagesFile(String lang) {
		if (messagesEnabled) {
			if (lang == null) {
				return false;
			}
			if (checkDataFolder()) {
				return false;
			}
			File file = new File(plugin.getDataFolder() + "/" + messageFolder, messagesFilePrefix + lang);
			if (!file.exists()) {
				try (InputStream in = plugin.getResourceAsStream(messageFolder + "/" + messagesFilePrefix + lang + ".yml")) {
					Files.copy(in, file.toPath());
				} catch (IOException e) {
					return false;
				}
			}
			Configuration config;
			try {
				config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
			} catch (IOException e) {
				return false;
			}
			messages.put(lang.toLowerCase(), config);
			messagesFiles.put(lang.toLowerCase(), file);
			return true;
		} else {
			return false;
		}
	}

	public Configuration getMessages(String lang) {
		if (messagesEnabled) {
			return messages.get(lang.toLowerCase());
		} else {
			return null;
		}
	}

	public boolean saveMessagesFile(String lang) {
		if (messagesEnabled) {
			File file = messagesFiles.get(lang.toLowerCase());
			Configuration c = getMessages(lang);
			if (file == null) {
				return false;
			}
			try {
				ConfigurationProvider.getProvider(YamlConfiguration.class).save(c, file);
			} catch (IOException e) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

}
