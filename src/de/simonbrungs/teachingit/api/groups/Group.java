package de.simonbrungs.teachingit.api.groups;

import java.util.ArrayList;

import de.simonbrungs.teachingit.api.user.Account;

public class Group {
	private Group superGroup;
	private int permissionheight;
	private ArrayList<String> permissions;
	private int groupID;
	private String groupName;

	public Group(String pGroupName, Group pSuperGroup, int pPermissionHeight, int pGroupID) {
		groupName = pGroupName;
		superGroup = pSuperGroup;
		permissionheight = pPermissionHeight;
		groupID = pGroupID;
	}

	public boolean hasSuperGroup() {
		return superGroup != null;
	}

	public Group getSuperGroup() {
		return superGroup;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String pNewGroupName) {
		groupName = pNewGroupName;
	}

	public boolean hasPermission(String pPermission) {
		pPermission = pPermission.toLowerCase();
		if (permissions.contains(pPermission))
			return true;
		if (superGroup == null)
			return false;
		return superGroup.hasPermission(pPermission);
	}

	public int getPermissionHeight() {
		return permissionheight;
	}

	public void addPermission() {

	}

	public ArrayList<Account> getUsersInGroup() {

	}

	public void setPermissionHeight(int pPermissionHeight) {

	}

	public void removePermission(String permission) {

	}

	public int getGroupID() {
		return groupID;
	}
}
