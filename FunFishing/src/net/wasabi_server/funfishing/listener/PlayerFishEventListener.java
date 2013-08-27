package net.wasabi_server.funfishing.listener;

import java.util.logging.Logger;

import net.wasabi_server.funfishing.FunFishing;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import com.avaje.ebeaninternal.server.deploy.BeanDescriptor.EntityType;

public class PlayerFishEventListener implements Listener {

	private static final Logger log = FunFishing.log;
	private static final String msgprefix = FunFishing.msgPrefix;
	private static final String logprefix = FunFishing.logPrefix;

	private final FunFishing plugin;

	public PlayerFishEventListener(final FunFishing plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerFish(PlayerFishEvent event) {
		Player p = event.getPlayer();
		if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
			plugin.addScore(p);
		}
	}
}
