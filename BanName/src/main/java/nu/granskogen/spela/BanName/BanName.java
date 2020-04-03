package nu.granskogen.spela.BanName;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;

public class BanName extends Plugin {
	public DataBaseManager dbm;
	private static BanName instance;
	public ConfigManager cfgm;
	
	public void onEnable() {
		instance = this;
		loadConfigManager();
		
		getProxy().getPluginManager().registerCommand(this, new BanNameCommand("banname"));
		getProxy().getPluginManager().registerCommand(this, new UnbanNameCommand("unbanname"));
		getProxy().getPluginManager().registerCommand(this, new CheckCommand("checkname"));
		
		dbm = new DataBaseManager();
		if(!dbm.setup()) {
			System.err.println("Couln't connect to database.");
			return;
		}
		
		getProxy().getPluginManager().registerListener(this, new BanListener());
	}
	
	public void loadConfigManager() {
		cfgm = new ConfigManager();
		cfgm.setup();
	}
	 
	public void sendMessageToCommandSenderFromConfig(CommandSender sender, String pathOnConfig) {
		sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', cfgm.getConfig().getString("messages."+pathOnConfig))));
	}
	
	public void sendMessageToCommandSenderFromConfig(CommandSender sender, String pathOnConfig, String replace, String replaceTo) {
		sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', cfgm.getConfig().getString("messages."+pathOnConfig).replace(replace, replaceTo))));
	}
	
	public String getBannedNameMessage(String operator, String date) {
		String message = "";
		for (String row : cfgm.getConfig().getStringList("messages.banMessage.layout")) {
			message += row + "\n";
		}
		message = ChatColor.translateAlternateColorCodes('&', message.replace("%OPERATOR%", operator).replace("%DATE%", date));
		return message;
	}
	
	public static BanName getInstance() {
		return instance;
	}
}
