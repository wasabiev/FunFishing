package wasabiev.funfishing.util;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wasabiev.funfishing.FunFishing;

public class Actions {

	// Logger
	private static final Logger log = FunFishing.log;
	private static final String logPrefix = FunFishing.logPrefix;
	private static final String msgPrefix = FunFishing.msgPrefix;

	private final FunFishing plugin;

	public Actions(FunFishing plugin) {
		this.plugin = plugin;
	}

	/**
	 * メッセージ送信
	 */
	public static void message(CommandSender sender, String message) {
		if (sender != null && message != null) {
			sender.sendMessage(message
					.replaceAll("&([0-9a-fk-or])", "\u00A7$1"));
		}
	}
	
	public static void message(Player player, String message){
		if (player != null && message != null) {
			player.sendMessage(message
					.replaceAll("&([0-9a-fk-or])", "\u00A7$1"));
		}
	}

	public static void broadcastMessage(String message) {
		if (message != null) {
			message = message.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
			Bukkit.broadcastMessage(message);
		}
	}

	public static void permcastMessage(String permission, String message) {
		int i = 0;
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player.hasPermission(permission)) {
				Actions.message(player, message);
				i++;
			}
		}
		log.info("Received " + i + "players: " + message);
	}
	
	/**
	 * 文字列連結
	 */
	public static String combine(String[] s, String glue)
    {
        int k = s.length;
        if (k == 0){ return null; }
        StringBuilder out = new StringBuilder();
        out.append(s[0]);
        for (int x = 1; x < k; x++){
            out.append(glue).append(s[x]);
        }
        return out.toString();
    }

}
