package de.simonbrungs.teachingit.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import de.simonbrungs.teachingit.TeachingIt;
import de.simonbrungs.teachingit.utilities.StringToArrayList;

public class Console {
	private HashMap<String, Command> registerdCommands = new HashMap<>();

	public void registerCommand(Command pCommand, String pCommandName) {
		registerdCommands.put(pCommandName.toLowerCase(), pCommand);
	}

	public void commandsReader() {
		Scanner scanner = new Scanner(System.in);
		while (!TeachingIt.getInstance().getShouldClose()) {
			String scanned = scanner.nextLine();
			ArrayList<String> commandWithArgs = StringToArrayList.stringToArrayList(scanned);
			if (!commandWithArgs.isEmpty()) {
				Command command = registerdCommands.get(commandWithArgs.get(0).toLowerCase());
				if (command != null) {
					String commandName = commandWithArgs.get(0);
					commandWithArgs.remove(0);
					command.executeCommand(commandName, commandWithArgs);
				} else {
					System.out.println("The command was not found");
				}
			}
		}
		scanner.close();
	}
}
