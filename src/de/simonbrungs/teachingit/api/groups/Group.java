package de.simonbrungs.teachingit.api.groups;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.simonbrungs.teachingit.TeachingIt;
import de.simonbrungs.teachingit.api.users.Account;

public class Group {
	private int groupID;

	public Group(int pGroupID) {
		groupID = pGroupID;
	}

	public boolean hasSuperGroup() {
		return getSuperGroup() != null;
	}

	public Group getSuperGroup() {
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		ResultSet resultSet;
		int superGroupID = -1;
		try {
			resultSet = con.createStatement()
					.executeQuery("select supergroup from `" + TeachingIt.getInstance().getConnection().getDatabase()
							+ "`.`" + TeachingIt.getInstance().getConnection().getTablePrefix() + "groups` WHERE id='"
							+ groupID + "' LIMIT 1");
			if (resultSet.next()) {
				superGroupID = resultSet.getInt("supergroup");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		TeachingIt.getInstance().getConnection().closeConnection(con);
		if (superGroupID != -1)
			return TeachingIt.getInstance().getGroupManager().getGroup(superGroupID);
		return null;
	}

	public String getGroupName() {
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		String groupName = null;
		try {
			ResultSet resultSet = con.createStatement()
					.executeQuery("select groupname from `" + TeachingIt.getInstance().getConnection().getDatabase()
							+ "`.`" + TeachingIt.getInstance().getConnection().getTablePrefix() + "groups` WHERE id='"
							+ groupID + "' LIMIT 1");
			if (resultSet.next()) {
				groupName = resultSet.getString("groupname");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		TeachingIt.getInstance().getConnection().closeConnection(con);
		return groupName;
	}

	public void setGroupName(String pNewGroupName) throws IllegalArgumentException {
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		if (pNewGroupName.length() > 128)
			throw new IllegalArgumentException();
		try {
			PreparedStatement preparedStatement = con
					.prepareStatement("UPDATE `" + TeachingIt.getInstance().getConnection().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnection().getTablePrefix()
							+ "groups` set groupname= ? WHERE ID='" + groupID + "' LIMIT 1");
			preparedStatement.setString(1, pNewGroupName);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		TeachingIt.getInstance().getConnection().closeConnection(con);
	}

	public boolean hasPermission(String pPermission) {
		return hasPermission(new Permission(pPermission));
	}

	public boolean hasPermission(Permission pPermission) {
		if (pPermission.getPermissionID() == -1)
			return false;
		if (getPermissions().contains(pPermission))
			return true;
		Group superGroup = getSuperGroup();
		if (superGroup == null)
			return false;
		return superGroup.hasPermission(pPermission);
	}

	public int getPermissionHeight() {
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		int permissionHeight = -1;
		try {
			ResultSet resultSet = con.createStatement().executeQuery(
					"select permissionheight from `" + TeachingIt.getInstance().getConnection().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnection().getTablePrefix() + "groups` WHERE id='" + groupID
							+ "' LIMIT 1");
			if (resultSet.next()) {
				permissionHeight = resultSet.getInt("permissionheight");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		TeachingIt.getInstance().getConnection().closeConnection(con);
		return permissionHeight;
	}

	public boolean addPermission(String pPermission) {
		Permission perm = Permission.createPermission(pPermission);
		if (hasPermission(perm))
			return false;
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		PreparedStatement preparedStatement;
		try {
			preparedStatement = con
					.prepareStatement("insert into  `" + TeachingIt.getInstance().getConnection().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnection().getTablePrefix() + "users` values (?, ?)");
			preparedStatement.setInt(1, groupID);
			preparedStatement.setInt(2, perm.getPermissionID());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		TeachingIt.getInstance().getConnection().closeConnection(con);
		return true;
	}

	private ArrayList<Permission> getPermissions() {
		ArrayList<Permission> permissions = new ArrayList<>();
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		try {
			ResultSet resultSet = con.createStatement()
					.executeQuery("select permissionid from `" + TeachingIt.getInstance().getConnection().getDatabase()
							+ "`.`" + TeachingIt.getInstance().getConnection().getTablePrefix()
							+ "grouppermissions` WHERE groupid='" + groupID + "'");
			while (resultSet.next())
				permissions.add(new Permission(resultSet.getInt("permissionid")));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		TeachingIt.getInstance().getConnection().closeConnection(con);
		return permissions;
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
			for (Group group : TeachingIt.getInstance().getGroupManager().getGroups()) {
				if (group != this) {
					if (group.isSuperGroup(this)) {
						for (Account account : group.getUsersInGroup())
							accounts.add(account);
					}
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
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		try {
			PreparedStatement preparedStatement = con.prepareStatement("UPDATE `"
					+ TeachingIt.getInstance().getConnection().getDatabase() + "`.`"
					+ TeachingIt.getInstance().getConnection().getTablePrefix() + "groups` set permissionheight='"
					+ pPermissionHeight + "' WHERE id='" + groupID + "' LIMIT 1");
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		TeachingIt.getInstance().getConnection().closeConnection(con);
	}

	public boolean isSuperGroup(Group pSuperGroup) {
		Group superGroup = getSuperGroup();
		if (superGroup == null)
			return false;
		if (superGroup.equals(pSuperGroup))
			return true;
		return superGroup.isSuperGroup(pSuperGroup);
	}

	public void removePermission(String pPermission) {
		removePermission(pPermission);
	}

	public void removePermission(Permission pPermission) {
		if (pPermission.getPermissionID() == -1) {
			Connection con = TeachingIt.getInstance().getConnection().createConnection();
			PreparedStatement preparedStatement;
			try {
				preparedStatement = con
						.prepareStatement("DELETE FROM `" + TeachingIt.getInstance().getConnection().getDatabase()
								+ "`.`" + TeachingIt.getInstance().getConnection().getTablePrefix()
								+ "grouppermissions` WHERE groupid = '" + groupID + "' AND permissionid = '"
								+ pPermission.getPermissionID() + "' LIMIT 1");
				preparedStatement.execute();
				for (Group group : TeachingIt.getInstance().getGroupManager().getGroups()) {
					if (group.hasPermission(pPermission))
						return;
				}
				preparedStatement = con
						.prepareStatement("DELETE FROM `" + TeachingIt.getInstance().getConnection().getDatabase()
								+ "`.`" + TeachingIt.getInstance().getConnection().getTablePrefix()
								+ "permissions` WHERE id = '" + pPermission.getPermissionID() + "' LIMIT 1");
				preparedStatement.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				TeachingIt.getInstance().getConnection().closeConnection(con);
			}
		}
	}

	public int getGroupID() {
		return groupID;
	}
}
