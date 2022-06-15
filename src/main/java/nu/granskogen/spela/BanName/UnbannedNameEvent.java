package nu.granskogen.spela.BanName;

import net.md_5.bungee.api.plugin.Event;

public class UnbannedNameEvent extends Event {
	private String operator;
	private String datetime;
	private String unbannedName;
	
	public UnbannedNameEvent(String operator, String datetime, String bannedName) {
		super();
		this.operator = operator;
		this.datetime = datetime;
		this.unbannedName = bannedName;
	}

	public String getOperator() {
		return operator;
	}

	public String getDatetime() {
		return datetime;
	}

	public String getBannedName() {
		return unbannedName;
	}
}
