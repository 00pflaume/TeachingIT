package de.simonbrungs.teachingit.api;

import java.util.ArrayList;

public interface Command {
	void executeCommand(String pCommandName, ArrayList<String> args);

}
