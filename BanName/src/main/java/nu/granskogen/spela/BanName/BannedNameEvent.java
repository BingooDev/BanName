package nu.granskogen.spela.BanName;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BannedNameEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private String operator;
	private String datetime;
	private String bannedName;
	
	public BannedNameEvent(String operator, String datetime, String bannedName) {
		super();
		this.operator = operator;
		this.datetime = datetime;
		this.bannedName = bannedName;
	}

	public String getOperator() {
		return operator;
	}

	public String getDatetime() {
		return datetime;
	}

	public String getBannedName() {
		return bannedName;
	}

	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
