package de.simonbrungs.teachingit.api;

import de.simonbrungs.teachingit.TeachingIt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;

public class Console {
	private final static String PREFIX = "[CONSOLE] ";
	private static Console instance = null;
	private final HashMap<String, Command> registerdCommands = new HashMap<>();

	public Console() throws IllegalAccessException {
		if (instance != null)
			throw new IllegalAccessException();
		instance = this;
	}

	public static Console getInstance() {
		return instance;
	}

	public void registerCommand(Command pCommand, String pCommandName) {
		registerdCommands.put(pCommandName.toLowerCase(), pCommand);
	}

	public void commandsReader() {
		Scanner scanner = new Scanner(System.in);
		while (!TeachingIt.getInstance().getShouldClose()) {
			String scanned = scanner.nextLine();
			String removed = scanned;
			while (!(removed = scanned.replace("  ", " ")).equals(scanned))
				scanned = removed;
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
