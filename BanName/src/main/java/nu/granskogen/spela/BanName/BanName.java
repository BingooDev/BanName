package nu.granskogen.spela.BanName;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class BanName extends JavaPlugin {
	
	public void onEnable() {
		saveDefaultConfig();
		getCommand("banname").setExecutor(new BanNameCommand());
	}
	
	public void sendMessageToCommandSenderFromConfig(CommandSender sender, String pathOnConfig) {
		System.out.println("messages."+pathOnConfig);
		System.out.println(getConfig().getString("messages."+pathOnConfig)); 
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages."+pathOnConfig)));
	}
}
