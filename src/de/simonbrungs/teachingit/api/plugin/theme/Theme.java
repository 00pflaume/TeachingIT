package de.simonbrungs.teachingit.api.plugin.theme;

import de.simonbrungs.teachingit.api.plugin.Plugin;
import de.simonbrungs.teachingit.api.users.User;

public abstract class Theme extends Plugin {

	public abstract String getHeader();

	public abstract String getBodyStart(User user);

	public abstract String getBodyEnd(User user);

	public abstract ErrorPageContentGenerator getErrorPageGenerator();

}
