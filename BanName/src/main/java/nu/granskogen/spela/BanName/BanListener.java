package nu.granskogen.spela.BanName;

import org.bukkit.event.Listener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class BanListener implements Listener {
	BanName pl = BanName.getPlugin(BanName.class);
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Connection conn = pl.dbm.getConnection();
		Player player = e.getPlayer();
		try {
			PreparedStatement st = conn.prepareStatement(SQLQuery.SELECT_NAME.toString());
			st.setString(1, player.getName());
			ResultSet result = st.executeQuery();
			if (result.next()) {
				// Encrypted and Base64 encoded password read from database
				boolean isBanned = result.getBoolean("isBanned");
				if(isBanned) {
					player.kickPlayer(pl.getBannedNameMessage(result.getString("operator"), result.getString("time_banned")));
					pl.dbm.closeConnection();
					return;
				}
			}
		} catch (SQLException ex) {
			pl.dbm.closeConnection();
			ex.printStackTrace();
			pl.sendMessageToCommandSenderFromConfig(player, "error.SQL");
			return;
		}
		pl.dbm.closeConnection();
	}
}
