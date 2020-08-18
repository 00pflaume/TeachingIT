package de.simonbrungs.teachingit.commands;

import de.simonbrungs.teachingit.TeachingIt;
import de.simonbrungs.teachingit.api.Command;

import java.util.ArrayList;

public class ShutDown implements Command {

	@Override
	public void executeCommand(String command, ArrayList<String> args) {
		TeachingIt.getInstance().shutDown(0);
	}

}
