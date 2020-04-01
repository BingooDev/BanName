package nu.granskogen.spela.BanName;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class BanName extends JavaPlugin {
	public DataBaseManager dbm;
	
	public void onEnable() {
		saveDefaultConfig();
		getCommand("banname").setExecutor(new BanNameCommand());
		
		dbm = new DataBaseManager();
		if(!dbm.setup()) {
			this.setEnabled(false);
			return;
		}
		
		getServer().getPluginManager().registerEvents(new BanListener(), this);
	}
	
	public void sendMessageToCommandSenderFromConfig(CommandSender sender, String pathOnConfig) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages."+pathOnConfig)));
	}
	
	public void sendMessageToCommandSenderFromConfig(CommandSender sender, String pathOnConfig, String replace, String replaceTo) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages."+pathOnConfig).replace(replace, replaceTo)));
	}
	
	public String getBannedNameMessage(String operator, String date) {
		String message = "";
		for (String row : getConfig().getStringList("messages.banMessage.layout")) {
			message += row + "\n";
		}
		message = ChatColor.translateAlternateColorCodes('&', message.replace("%OPERATOR%", operator).replace("%DATE%", date));
		return message;
	}
}
