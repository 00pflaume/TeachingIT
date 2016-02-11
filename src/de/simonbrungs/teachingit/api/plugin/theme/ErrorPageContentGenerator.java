package de.simonbrungs.teachingit.api.plugin.theme;

import de.simonbrungs.teachingit.api.events.ContentCreateEvent;

public abstract class ErrorPageContentGenerator {
	public abstract ContentCreateEvent getErrorPageNotFound(ContentCreateEvent contentCreateEvent);

	public abstract ContentCreateEvent getErrorAccesDenied(ContentCreateEvent contentCreateEvent);
}
