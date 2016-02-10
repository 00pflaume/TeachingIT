package de.simonbrungs.teachingit.api.groups;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.simonbrungs.teachingit.TeachingIt;
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
		return hasPermission(new Permission(pPermission));
	}

	public boolean hasPermission(Permission pPermission) {
		if (pPermission.getPermissionID() == -1)
			return false;
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
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		ArrayList<Account> accounts = new ArrayList<>();
		try {
			ResultSet resultSet = con.createStatement()
					.executeQuery("select userid from `" + TeachingIt.getInstance().getConnection().getDatabase()
							+ "`.`" + TeachingIt.getInstance().getConnection().getTablePrefix()
							+ "users` WHERE groupid='" + groupID + "' LIMIT 1");
			while (resultSet.next()) {
				accounts.add(new Account(resultSet.getInt("userid")));
			}
			for (Group group : GroupManager.getGroups()) {
				if (group.isSuperGroup(this)) {
					accounts.addAll(group.getUsersInGroup());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			TeachingIt.getInstance().getConnection().closeConnection(con);
		}
		return accounts;
	}

	public void setPermissionHeight(int pPermissionHeight) {

	}

	public boolean isSuperGroup(Group pSuperGroup) {
		if (superGroup == null)
			return false;
		if (superGroup.equals(pSuperGroup))
			return true;
		return superGroup.isSuperGroup(pSuperGroup);
	}

	public void removePermission(String permission) {

	}

	public int getGroupID() {
		return groupID;
	}
}
