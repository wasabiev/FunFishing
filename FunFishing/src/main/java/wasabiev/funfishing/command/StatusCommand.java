package wasabiev.funfishing.command;

import wasabiev.funfishing.util.Actions;

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
		String[] str = this.plugin.getTopPlayerNames().clone();
		Actions.message(sender,
				msgPrefix + "目標スコアは" + this.plugin.getScoreLimit() + "匹です。");
		if (this.plugin.beScored(player)) {
			Actions.message(sender,
					msgPrefix + "あなたは" + this.plugin.getScore(player)
							+ "匹の魚を捕まえています。");
		}
		if (!str[0].equals("")) {
			Actions.message(sender,
					msgPrefix + "現在のトップは" + this.plugin.getTopPlayerName()
							+ "(" + this.plugin.getTopScore() + ")です。");
		} else {
			Actions.message(sender, msgPrefix + "現在記録がありません。");
		}
	}

	@Override
	public boolean permission() {
		return sender.hasPermission("fishing.status");
	}
}
