package net.wasabi_server.funfishing.command;

import net.wasabi_server.funfishing.util.Actions;

import org.bukkit.command.CommandException;

public class StatusCommand extends BaseCommand {
	public StatusCommand() {
		bePlayer = true;
		name = "status";
		argLength = 0;
		usage = "<- show your fishing status";
	}

	@Override
	public void execute() throws CommandException {
		if (this.plugin.beScored(player)) {
			Actions.message(sender,
					msgPrefix + "あなたは" + this.plugin.getScore(player)
							+ "匹の魚を捕まえています。");
		} else {
			Actions.message(sender, msgPrefix + "あなたはまだ魚を捕まえていません。");
		}
		if (!this.plugin.getTopPlayerName().equals("")) {
			Actions.message(sender,
					msgPrefix + "現在のトップは" + this.plugin.getTopPlayerName()
							+ "（" + this.plugin.getTopScore() + "匹）です。");
		}
	}

	@Override
	public boolean permission() {
		return sender.hasPermission("fishing.status");
	}
}
