package begad.utils;

import com.google.common.base.Preconditions;

public enum UpdateAPI {
	SPIGET("https://api.spiget.org/v2/resources/", true),
	SPIGOT("http://api.spigotmc.org/legacy/update.php?resource=", false);

	public final String Link;
	public final boolean LikeSpiget;

	UpdateAPI(String baseResourceLink, boolean likeSpiget) {
		Preconditions.checkArgument(baseResourceLink != null, "baseResourceLink");
		this.Link = baseResourceLink;
		this.LikeSpiget = likeSpiget;
	}
}
