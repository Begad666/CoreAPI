package begad.bc.utils;

import begad.utils.UpdateAPI;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.md_5.bungee.api.plugin.Plugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class BungeeUpdates {
	private Plugin Plugin;
	private String PluginName;
	private String ConfigVersion;
	private String CurrentVersion;
	private String BaseLink;
	private boolean SL;
	private String Message = "You are up to date";

	public BungeeUpdates(Plugin plugin, String pluginName, String pluginID, String configVersion, String currentVersion, UpdateAPI type) {
		Plugin = plugin;
		PluginName = pluginName;
		ConfigVersion = configVersion;
		CurrentVersion = currentVersion;
		BaseLink = type.Link + pluginID;
		SL = type.LikeSpiget;
	}

	/**
	 * Why are you reading this? The name tells you what does it do
	 */
	public boolean IsUpdateRequired() {
		String latest = getLatestVersion();
		return !latest.equals(CurrentVersion);
	}

	public JsonObject getInfo() {
		if (SL) {
			try {
				URL url = new URL(BaseLink + "/versions/latest?" + System.currentTimeMillis());
				HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
				connection.setUseCaches(true);
				connection.addRequestProperty("User-Agent", PluginName + " Plugin Updater | " + getCompileCurrentVersion());
				connection.setDoOutput(true);
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuilder content = new StringBuilder();
				String input;
				while ((input = br.readLine()) != null) {
					content.append(input);
				}
				br.close();
				try {
					JsonObject updateInfo;
					updateInfo = new Gson().fromJson(content.toString(), JsonObject.class);
					return updateInfo;
				} catch (JsonParseException e) {
					e.printStackTrace();
					return null;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	public String getLatestVersion() {
		try {
			URL url = new URL(BaseLink + (SL ? ("/versions/latest?" + System.currentTimeMillis()) : ""));
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setUseCaches(true);
			connection.addRequestProperty("User-Agent", PluginName + " Plugin Updater | " + getCompileCurrentVersion());
			connection.setDoOutput(true);
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder content = new StringBuilder();
			String input;
			while ((input = br.readLine()) != null) {
				content.append(input);
			}
			br.close();
			if (SL) {
				try {
					JsonObject updateInfo;
					updateInfo = new Gson().fromJson(content.toString(), JsonObject.class);
					return updateInfo.get("name").getAsString();
				} catch (JsonParseException e) {
					e.printStackTrace();
					return null;
				}
			} else {
				return content.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean DownloadAndInstall() {
		if (SL) {
			File jar = new File(Plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
			try {
				URL url = new URL(BaseLink + "/download");
				ReadableByteChannel channel = Channels.newChannel(url.openStream());
				FileOutputStream stream = new FileOutputStream(jar, false);
				stream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
				return true;
			} catch (IOException e) {
				return false;
			}
		} else {
			return false;
		}
	}

	public String getCompileCurrentVersion() {
		return CurrentVersion;
	}

	public String getCompileConfigVersion() {
		return ConfigVersion;
	}

	public void setMessage(String msg) {
		Message = msg;
	}

	public String getMessage() {
		return Message;
	}

}
