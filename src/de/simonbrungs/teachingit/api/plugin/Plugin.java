package de.simonbrungs.teachingit.api.plugin;

public interface Plugin {
	public abstract String getName();

	public abstract String getVersion();

	public abstract String getAuthor();

	public abstract void onEnable();

	public abstract void onDisable();
}
