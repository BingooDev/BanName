package nu.granskogen.spela.BanName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.leoko.advancedban.manager.TimeManager;
import me.leoko.advancedban.manager.UUIDManager;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;

public class BanNameCommand implements CommandExecutor {
	BanName pl = BanName.getPlugin(BanName.class);

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("BanName.ban") || sender.hasPermission("BanName.*") || sender.hasPermission("*")
				|| sender.isOp()) {
			if (args.length < 1) {
				pl.sendMessageToCommandSenderFromConfig(sender, "syntax.banName");
				return false;
			}
			@SuppressWarnings("deprecation")
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
			if (pl.getConfig().contains("bannedNames." + offlinePlayer.getName())) {
				pl.sendMessageToCommandSenderFromConfig(sender, "error.alreadyBanned");
				return false;
			}
			String uuid = UUIDManager.get().getUUID(args[0]);
			String operator = "CONSOLE";
			if (!(sender instanceof Player)) {
				Player p = (Player) sender;
				operator = p.getName();
			}
			//Use AdvancedBan to kick the player.
			if(offlinePlayer.isOnline()) {				
				new Punishment(args[0], uuid, pl.getConfig().getString("messages.kickReason"), operator,
						PunishmentType.KICK, TimeManager.getTime(), -1, null, -1);
			} else {
				new Punishment(args[0], uuid, pl.getConfig().getString("messages.kickReason"), operator,
						PunishmentType.WARNING, TimeManager.getTime(), -1, null, -1);
			}
			
			//Save the playername, operator name and date to config.yml.
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateTime = df.format(new Date());
			pl.getConfig().set("users."+args[0]+".operator", operator);
			pl.getConfig().set("users."+args[0]+".date", dateTime);
			pl.saveConfig();
			pl.sendMessageToCommandSenderFromConfig(sender, "operatorSuccessMessage");
		} else {
			pl.sendMessageToCommandSenderFromConfig(sender, "error.noPermission");
		}
		return true;
	}

}
