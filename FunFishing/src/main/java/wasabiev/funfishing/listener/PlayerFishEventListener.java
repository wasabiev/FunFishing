package wasabiev.funfishing.listener;

import wasabiev.funfishing.FunFishing;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class PlayerFishEventListener implements Listener {

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
