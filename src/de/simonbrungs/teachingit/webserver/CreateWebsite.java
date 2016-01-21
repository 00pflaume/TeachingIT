package de.simonbrungs.teachingit.webserver;

import de.simonbrungs.teachingit.TeachingIt;
import de.simonbrungs.teachingit.api.events.HeaderCreateEvent;

public class CreateWebsite {
	public static String createHeader() {
		String header = "<html><head>";
		HeaderCreateEvent headerCreateEvent = new HeaderCreateEvent(header);
		TeachingIt.getInstance().getEventExecuter().executeEvent(headerCreateEvent);
		return headerCreateEvent.getHeader() + TeachingIt.getInstance().getTheme().getHeader() + "</head>";
	}

	public static String createBody() {
		return null;
	}

}
