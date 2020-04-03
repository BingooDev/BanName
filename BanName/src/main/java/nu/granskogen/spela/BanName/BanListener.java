package nu.granskogen.spela.BanName;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BanListener implements Listener {
	BanName pl = BanName.getInstance();
	
	@EventHandler
	public void onJoin(PreLoginEvent e) {
		Connection conn = pl.dbm.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(SQLQuery.SELECT_NAME.toString());
			st.setString(1, e.getConnection().getName().toLowerCase());
			ResultSet result = st.executeQuery();
			if (result.next()) {
				boolean isBanned = result.getBoolean("isBanned");
				if(isBanned) {
					e.setCancelReason(new TextComponent(pl.getBannedNameMessage(result.getString("operator"), result.getString("time_banned"))));
					e.setCancelled(true);
					pl.dbm.closeConnection();
					return;
				}
			}
		} catch (SQLException ex) {
			pl.dbm.closeConnection();
			ex.printStackTrace();
			return;
		}
		pl.dbm.closeConnection();
	}
}
