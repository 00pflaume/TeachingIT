package de.simonbrungs.teachingit.api;

import java.util.ArrayList;

public interface Command {
	public abstract void executeCommand(ArrayList<String> args);

}
