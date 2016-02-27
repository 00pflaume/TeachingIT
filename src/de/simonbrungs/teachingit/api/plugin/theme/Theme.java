package de.simonbrungs.teachingit.api.plugin.theme;

import de.simonbrungs.teachingit.api.plugin.Plugin;
import de.simonbrungs.teachingit.api.users.TempUser;

public abstract class Theme extends Plugin {

	public abstract String getHeader();

	public abstract String getBodyStart(TempUser user);

	public abstract String getBodyEnd(TempUser user);

	public abstract ErrorPageContentGenerator getErrorPageGenerator();

}
