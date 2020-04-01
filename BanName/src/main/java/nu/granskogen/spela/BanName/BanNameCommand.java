package nu.granskogen.spela.BanName;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

public class BanNameCommand implements CommandExecutor {
	BanName pl = BanName.getPlugin(BanName.class);

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("BanName.ban")) {
			if (args.length < 1) {
				pl.sendMessageToCommandSenderFromConfig(sender, "syntax.banName");
				return false;
			}
			@SuppressWarnings("deprecation")
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
			
			Connection conn = pl.dbm.getConnection();
			boolean banExists = false;
			try {
				PreparedStatement st = conn.prepareStatement(SQLQuery.SELECT_NAME.toString());
				st.setString(1, offlinePlayer.getName());
				ResultSet result = st.executeQuery();
				if (result.next()) {
					banExists = true;
					// Encrypted and Base64 encoded password read from database
					boolean isBanned = result.getBoolean("isBanned");
					if(isBanned) {
						pl.sendMessageToCommandSenderFromConfig(sender, "error.alreadyBanned");
						pl.dbm.closeConnection();
						return false;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				pl.dbm.closeConnection();
				pl.sendMessageToCommandSenderFromConfig(sender, "error.SQL");
				return false;
			}
			
			String operator = "Console";
			if (sender instanceof Player) {
				Player p = (Player) sender;
				operator = p.getName();
			}
			
			
			//Save the playername and operator name to database.
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateTime = df.format(new Date());
			
			PreparedStatement st;
			try {
				if(banExists) {
					st = conn.prepareStatement(SQLQuery.SET_BANNED_NAME_TRUE.toString());
					st.setString(1, offlinePlayer.getName());
					st.execute();
					
					st = conn.prepareStatement(SQLQuery.INSERT_INTO_LOG.toString());
					st.setString(1, "uppdate_ban");
					JsonObject json = new JsonObject();
					json.addProperty("operator", operator);
					json.addProperty("isBanned", true);
					json.addProperty("banned_name", offlinePlayer.getName());
					st.setString(2, json.toString());
					st.execute();
				} else {					
					st = conn.prepareStatement(SQLQuery.INSERT_NAME.toString());
					st.setString(1, offlinePlayer.getName());
					st.setString(2, operator);
					st.setBoolean(3, true); //Status for if the playername is banned
					st.execute();
					st = conn.prepareStatement(SQLQuery.INSERT_INTO_LOG.toString());
					st.setString(1, "create_ban");
					JsonObject json = new JsonObject();
					json.addProperty("operator", operator);
					json.addProperty("isBanned", true);
					json.addProperty("banned_name", offlinePlayer.getName());
					st.setString(2, json.toString());
					st.execute();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				pl.sendMessageToCommandSenderFromConfig(sender, "error.SQL");
				pl.dbm.closeConnection();
				return false;
			}
			pl.dbm.closeConnection();
			
			if(offlinePlayer.isOnline()) {
				Player player = Bukkit.getPlayer(args[0]);
				player.kickPlayer(pl.getBannedNameMessage(operator, dateTime));
			}
			
			pl.getServer().getPluginManager().callEvent(new BannedNameEvent(operator, dateTime, offlinePlayer.getName()));
			pl.sendMessageToCommandSenderFromConfig(sender, "operatorSuccessMessage", "{name}", offlinePlayer.getName());
		} else {
			pl.sendMessageToCommandSenderFromConfig(sender, "error.noPermission");
		}
		return true;
	}

}
