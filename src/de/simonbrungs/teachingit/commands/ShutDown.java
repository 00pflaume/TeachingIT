package de.simonbrungs.teachingit.commands;

import java.util.ArrayList;

import de.simonbrungs.teachingit.TeachingIt;
import de.simonbrungs.teachingit.api.Command;

public class ShutDown implements Command {

	@Override
	public void executeCommand(String command, ArrayList<String> args) {
		TeachingIt.getInstance().shutDown();
	}

}
