package net.wasabi_server.funfishing.command;

import org.bukkit.command.CommandException;

public class DebugCommand extends BaseCommand {
	public DebugCommand() {
		bePlayer = true;
		name = "debug";
		argLength = 2;
		usage = "[set amount]<- debug command";
	}

	@Override
	public void execute() throws CommandException {
		final String str = args.get(0);
		if(str.equalsIgnoreCase("set")){
			int amount = Integer.valueOf(args.get(1)).intValue();
			this.plugin.setScore(player, amount);
		}
	}

	@Override
	public boolean permission() {
		return sender.hasPermission("fishing.debug");
	}

}
