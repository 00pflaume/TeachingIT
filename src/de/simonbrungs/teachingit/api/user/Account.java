package de.simonbrungs.teachingit.api.user;

import java.util.ArrayList;

import de.simonbrungs.teachingit.api.groups.Group;

public class Account {
	private int id;

	public Account(int pID) {
		id = pID;
	}

	public String getUserName() {

	}

	public boolean isBanned() {
		return isBannedUntil() > System.currentTimeMillis() / 1000L;
	}

	public int getID() {
		return id;
	}

	public String getMetaInfo(String pMetaIdentifier) {

	}

	public void setUserName() {

	}

	public void setMetaInfo(String pMetaIdentifier) {

	}

	public void removeMetaInfo(String pMetaIdentifier) {

	}

	public void ban(int pBanUntil) {

	}

	public boolean isActivated() {

	}

	public void addGroup() {

	}

	public ArrayList<Group> getGroups() {

	}

	public void removeGroup() {

	}

	public void setEmail(String pEmail) {

	}

	public String getEmail() {

	}

	public void setPassword() {

	}

	public boolean hasPermission() {

	}

	public int getPermissionHeight() {

	}

	public void setPermissionHeight() {

	}

	public int getRegistrationDate() {

	}

	public void setActivated(byte status) {

	}

	public void setGroup() {

	}
}
