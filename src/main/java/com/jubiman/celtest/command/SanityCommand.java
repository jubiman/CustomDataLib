package com.jubiman.celtest.command;

import com.jubiman.celtest.sanity.SanityPlayer;
import com.jubiman.celtest.sanity.SanityPlayersHandler;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.StringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class SanityCommand extends ModularChatCommand {
	public SanityCommand() {
		super("sanity", "Changing your sanity value", PermissionLevel.ADMIN, true, new CmdParameter("string", new StringParameterHandler()), new CmdParameter("int", new IntParameterHandler(), true));
	}

	@Override
	public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog commandLog) {
		SanityPlayer player = SanityPlayersHandler.getPlayer(serverClient.authentication);
		switch ((String) args[0]) {
			case "set":
				player.setSanity((int) args[1]);
				break;
			case "add":
				player.addSanity((int) args[1]);
				break;
			case "remove":
				player.removeSanity((int) args[1]);
				break;
			case "get":
				commandLog.add("Sanity: " + player.getSanity());
				return;
		}
		commandLog.add("Sanity set to " + player.getSanity());
	}
}
