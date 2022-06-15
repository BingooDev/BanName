package nu.granskogen.spela.BanName;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class CheckCommand extends Command implements TabExecutor {
	BanName pl = BanName.getInstance();

	public CheckCommand(String name) {
		super(name, "BanName.check");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission("BanName.check")) {
			pl.sendMessageToCommandSenderFromConfig(sender, "error.noPermission");
			return;
		}
		if (args.length < 1) {
			pl.sendMessageToCommandSenderFromConfig(sender, "syntax.checkName");
			return;
		}
		String name = args[0].toLowerCase();

		Connection conn = pl.dbm.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(SQLQuery.SELECT_NAME.toString());
			st.setString(1, name);
			ResultSet result = st.executeQuery();
			if (result.next()) {
				if (result.getBoolean("isBanned")) {
					pl.sendMessageToCommandSenderFromConfig(sender, "error.statusBanned", "{name}", name);
				} else {
					pl.sendMessageToCommandSenderFromConfig(sender, "error.statusNotBanned", "{name}", name);
				}

			} else {
				pl.sendMessageToCommandSenderFromConfig(sender, "error.statusNotBanned", "{name}", name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			pl.dbm.closeConnection();
			pl.sendMessageToCommandSenderFromConfig(sender, "error.SQL");
			return;
		}
		pl.dbm.closeConnection();
		return;
	}
	
	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
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
