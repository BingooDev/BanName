package nu.granskogen.spela.BanName;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class BanNameCommand extends Command implements TabExecutor {
	BanName pl = BanName.getInstance();

	public BanNameCommand(String name) {
		super(name);
	}


	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender.hasPermission("BanName.ban")) {
			if (args.length < 1) {
				pl.sendMessageToCommandSenderFromConfig(sender, "syntax.banName");
				return;
			}
			String name = args[0].toLowerCase();
			ProxiedPlayer offlinePlayer = pl.getProxy().getPlayer(name);
			
			Connection conn = pl.dbm.getConnection();
			boolean banExists = false;
			try {
				PreparedStatement st = conn.prepareStatement(SQLQuery.SELECT_NAME.toString());
				st.setString(1, name);
				ResultSet result = st.executeQuery();
				if (result.next()) {
					banExists = true;
					boolean isBanned = result.getBoolean("isBanned");
					if(isBanned) {
						pl.sendMessageToCommandSenderFromConfig(sender, "error.alreadyBanned");
						pl.dbm.closeConnection();
						return;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				pl.dbm.closeConnection();
				pl.sendMessageToCommandSenderFromConfig(sender, "error.SQL");
				return;
			}
			
			String operator = "Console";
			if (sender instanceof ProxiedPlayer) {
				ProxiedPlayer p = (ProxiedPlayer) sender;
				operator = p.getName();
			}
			
			
			//Save the playername and operator name to database.
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateTime = df.format(new Date());
			
			PreparedStatement st;
			try {
				if(banExists) {
					st = conn.prepareStatement(SQLQuery.SET_BANNED_NAME_TRUE.toString());
					st.setString(1, operator);
					st.setString(2, name);
					st.execute();
					
					PreparedStatement st2 = conn.prepareStatement(SQLQuery.INSERT_INTO_LOG.toString());
					st2.setString(1, "uppdate_ban");
					JsonObject json = new JsonObject();
					json.addProperty("operator", operator);
					json.addProperty("isBanned", true);
					json.addProperty("banned_name", name);
					st2.setString(2, json.toString());
					st2.execute();
				} else {					
					st = conn.prepareStatement(SQLQuery.INSERT_NAME.toString());
					st.setString(1, name);
					st.setString(2, operator);
					st.setBoolean(3, true); //Status for if the playername is banned
					st.execute();
					
					PreparedStatement st2 = conn.prepareStatement(SQLQuery.INSERT_INTO_LOG.toString());
					st2.setString(1, "create_ban");
					JsonObject json = new JsonObject();
					json.addProperty("operator", operator);
					json.addProperty("isBanned", true);
					json.addProperty("banned_name", name);
					st2.setString(2, json.toString());
					st2.execute();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				pl.sendMessageToCommandSenderFromConfig(sender, "error.SQL");
				pl.dbm.closeConnection();
				return;
			}
			pl.dbm.closeConnection();
			
			if(offlinePlayer != null && offlinePlayer.isConnected()) {
				offlinePlayer.disconnect(new TextComponent(pl.getBannedNameMessage(operator, dateTime)));
			}
			
			pl.getProxy().getPluginManager().callEvent(new BannedNameEvent(operator, dateTime, name));
			pl.sendMessageToCommandSenderFromConfig(sender, "operatorSuccessMessage", "{name}", name);
		} else {
			pl.sendMessageToCommandSenderFromConfig(sender, "error.noPermission");
		}
		return;
	}
	
	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args)
    {
        final String lastArg = ( args.length > 0 ) ? args[args.length - 1].toLowerCase( Locale.ROOT ) : "";
        return Iterables.transform( Iterables.filter( ProxyServer.getInstance().getPlayers(), new Predicate<ProxiedPlayer>()
        {
            @Override
            public boolean apply(ProxiedPlayer player)
            {
                return player.getName().toLowerCase( Locale.ROOT ).startsWith( lastArg );
            }
        } ), new Function<ProxiedPlayer, String>()
        {
            @Override
            public String apply(ProxiedPlayer player)
            {
                return player.getName();
            }
        } );
    }

}
