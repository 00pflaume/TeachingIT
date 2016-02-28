package de.simonbrungs.teachingit.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;

import de.simonbrungs.teachingit.TeachingIt;

public class Console {
	private HashMap<String, Command> registerdCommands = new HashMap<>();
	private final static String PREFIX = "[CONSOLE] ";

	public void registerCommand(Command pCommand, String pCommandName) {
		registerdCommands.put(pCommandName.toLowerCase(), pCommand);
	}

	public void commandsReader() {
		Scanner scanner = new Scanner(System.in);
		while (!TeachingIt.getInstance().getShouldClose()) {
			String scanned = scanner.nextLine();
			ArrayList<String> commandWithArgs = new ArrayList<String>(Arrays.asList(scanned.split(" ")));
			if (!commandWithArgs.isEmpty()) {
				Command command = registerdCommands.get(commandWithArgs.get(0).toLowerCase());
				if (command != null) {
					String commandName = commandWithArgs.get(0);
					commandWithArgs.remove(0);
					command.executeCommand(commandName, commandWithArgs);
				} else {
					TeachingIt.getInstance().getLogger().log(Level.INFO, PREFIX + "The command was not found");
				}
			}
		}
		scanner.close();
	}
}
