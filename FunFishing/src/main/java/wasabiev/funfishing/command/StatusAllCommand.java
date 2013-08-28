package wasabiev.funfishing.command;

import org.bukkit.command.CommandException;

public class StatusAllCommand extends BaseCommand {
	public StatusAllCommand() {
		bePlayer = false;
		name = "statusall";
		argLength = 0;
		usage = "<- show all fishing status";
	}

	@Override
	public void execute() throws CommandException {
		this.plugin.showStatusAll(player);
	}

	@Override
	public boolean permission() {
		return sender.hasPermission("fishing.status");
	}

}
