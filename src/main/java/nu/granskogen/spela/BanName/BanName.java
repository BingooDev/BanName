package nu.granskogen.spela.BanName;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.gson.JsonObject;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
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
			System.err.println("Couldn't connect to database.");
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
	
	public String getBannedNameMessage(String operator, String name) {
		String message = "";
		for (String row : cfgm.getConfig().getStringList("messages.banMessage.layout")) {
			message += row + "\n";
		}
		message = ChatColor.translateAlternateColorCodes('&', message.replace("%OPERATOR%", operator).replace("%NAME%", name));
		return message;
	}
	
	public static BanName getInstance() {
		return instance;
	}
	
	public String getBanNotification(String operator, String name) {
		String message = "";
		for (String row : cfgm.getConfig().getStringList("messages.banMessage.notifications.layout")) {
			message += row + "\n";
		}
		message = ChatColor.translateAlternateColorCodes('&', message.replace("%OPERATOR%", operator).replace("%NAME%", name));
		return message;
	}
	
	
	public void sendNotificationToValidOnlinePlayers(ProxiedPlayer operator, String name) {
		for(ProxiedPlayer player : getProxy().getPlayers()) {
			if (operator != null && player == operator)
				continue;
			String operatorName;
			if(operator == null)
				operatorName = "Console";
			else
				operatorName = operator.getName();
			if(player.hasPermission("BanName.notify"))
				player.sendMessage(new TextComponent(getBanNotification(operatorName, name)));
		}
	}
	
	public void sendNotificationToDiscord(String operator, String name, String time) {
		int port = 3003;
		try (Socket socket = new Socket("localhost", port)) {

			OutputStream output = socket.getOutputStream();
			JsonObject obj = new JsonObject();
			
			obj.addProperty("type","BanName"); 
			obj.addProperty("punished", name);
			obj.addProperty("operator", operator);
			obj.addProperty("time", time);
			output.write(obj.toString().getBytes());

			socket.close();
		} catch (UnknownHostException ex) {

			System.out.println("Server not found: " + ex.getMessage());

		} catch (IOException ex) {

			System.out.println("I/O error: " + ex.getMessage());
		}
	}
	
	public void sendUnBanNotificationToDiscord(String operator, String name, String time) {
		int port = 3003;
		try (Socket socket = new Socket("localhost", port)) {

			OutputStream output = socket.getOutputStream();
			JsonObject obj = new JsonObject();
			
			obj.addProperty("type","UnBanName"); 
			obj.addProperty("punished", name);
			obj.addProperty("operator", operator);
			obj.addProperty("time", time);
			output.write(obj.toString().getBytes());

			socket.close();
		} catch (UnknownHostException ex) {

			System.out.println("Server not found: " + ex.getMessage());

		} catch (IOException ex) {

			System.out.println("I/O error: " + ex.getMessage());
		}
	}
}
