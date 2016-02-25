package de.simonbrungs.teachingit.api.groups;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

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
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		ResultSet resultSet;
		int superGroupID = -1;
		try {
			resultSet = con.createStatement()
					.executeQuery("select supergroup from `" + TeachingIt.getInstance().getConnector().getDatabase()
							+ "`.`" + TeachingIt.getInstance().getConnector().getTablePrefix() + "groups` WHERE id='"
							+ groupID + "' LIMIT 1");
			if (resultSet.next()) {
				superGroupID = resultSet.getInt("supergroup");
			}
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();e.printStackTrace(new PrintWriter(sw));TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		TeachingIt.getInstance().getConnector().closeConnection(con);
		if (superGroupID != -1)
			return TeachingIt.getInstance().getGroupManager().getGroup(superGroupID);
		return null;
	}

	public String getGroupName() {
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		String groupName = null;
		try {
			ResultSet resultSet = con.createStatement()
					.executeQuery("select groupname from `" + TeachingIt.getInstance().getConnector().getDatabase()
							+ "`.`" + TeachingIt.getInstance().getConnector().getTablePrefix() + "groups` WHERE id='"
							+ groupID + "' LIMIT 1");
			if (resultSet.next()) {
				groupName = resultSet.getString("groupname");
			}
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		TeachingIt.getInstance().getConnector().closeConnection(con);
		return groupName;
	}

	public void setGroupName(String pNewGroupName) throws IllegalArgumentException {
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		if (pNewGroupName.length() > 128)
			throw new IllegalArgumentException();
		try {
			PreparedStatement preparedStatement = con
					.prepareStatement("UPDATE `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix()
							+ "groups` set groupname= ? WHERE ID='" + groupID + "' LIMIT 1");
			preparedStatement.setString(1, pNewGroupName);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();e.printStackTrace(new PrintWriter(sw));TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		TeachingIt.getInstance().getConnector().closeConnection(con);
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
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		int permissionHeight = -1;
		try {
			ResultSet resultSet = con.createStatement().executeQuery(
					"select permissionheight from `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix() + "groups` WHERE id='" + groupID
							+ "' LIMIT 1");
			if (resultSet.next()) {
				permissionHeight = resultSet.getInt("permissionheight");
			}
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();e.printStackTrace(new PrintWriter(sw));TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		TeachingIt.getInstance().getConnector().closeConnection(con);
		return permissionHeight;
	}

	public boolean addPermission(String pPermission) {
		Permission perm = Permission.createPermission(pPermission);
		if (hasPermission(perm))
			return false;
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		PreparedStatement preparedStatement;
		try {
			preparedStatement = con
					.prepareStatement("insert into  `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix() + "users` values (?, ?)");
			preparedStatement.setInt(1, groupID);
			preparedStatement.setInt(2, perm.getPermissionID());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();e.printStackTrace(new PrintWriter(sw));TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		TeachingIt.getInstance().getConnector().closeConnection(con);
		return true;
	}

	private ArrayList<Permission> getPermissions() {
		ArrayList<Permission> permissions = new ArrayList<>();
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		try {
			ResultSet resultSet = con.createStatement()
					.executeQuery("select permissionid from `" + TeachingIt.getInstance().getConnector().getDatabase()
							+ "`.`" + TeachingIt.getInstance().getConnector().getTablePrefix()
							+ "grouppermissions` WHERE groupid='" + groupID + "'");
			while (resultSet.next())
				permissions.add(new Permission(resultSet.getInt("permissionid")));
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();e.printStackTrace(new PrintWriter(sw));TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		TeachingIt.getInstance().getConnector().closeConnection(con);
		return permissions;
	}

	public ArrayList<Account> getUsersInGroup() {
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		ArrayList<Account> accounts = new ArrayList<>();
		try {
			ResultSet resultSet = con.createStatement()
					.executeQuery("select userid from `" + TeachingIt.getInstance().getConnector().getDatabase()
							+ "`.`" + TeachingIt.getInstance().getConnector().getTablePrefix()
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
			StringWriter sw = new StringWriter();e.printStackTrace(new PrintWriter(sw));TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
			return null;
		} finally {
			TeachingIt.getInstance().getConnector().closeConnection(con);
		}
		return accounts;
	}

	public void setPermissionHeight(int pPermissionHeight) {
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		try {
			PreparedStatement preparedStatement = con.prepareStatement("UPDATE `"
					+ TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
					+ TeachingIt.getInstance().getConnector().getTablePrefix() + "groups` set permissionheight='"
					+ pPermissionHeight + "' WHERE id='" + groupID + "' LIMIT 1");
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();e.printStackTrace(new PrintWriter(sw));TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		TeachingIt.getInstance().getConnector().closeConnection(con);
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
			Connection con = TeachingIt.getInstance().getConnector().createConnection();
			PreparedStatement preparedStatement;
			try {
				preparedStatement = con
						.prepareStatement("DELETE FROM `" + TeachingIt.getInstance().getConnector().getDatabase()
								+ "`.`" + TeachingIt.getInstance().getConnector().getTablePrefix()
								+ "grouppermissions` WHERE groupid = '" + groupID + "' AND permissionid = '"
								+ pPermission.getPermissionID() + "' LIMIT 1");
				preparedStatement.execute();
				ResultSet resultSet = con.createStatement().executeQuery("select permissionid from `"
						+ TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
						+ TeachingIt.getInstance().getConnector().getTablePrefix() + "grouppermissions` LIMIT 1");
				if (resultSet.next())
					return;
				preparedStatement = con
						.prepareStatement("DELETE FROM `" + TeachingIt.getInstance().getConnector().getDatabase()
								+ "`.`" + TeachingIt.getInstance().getConnector().getTablePrefix()
								+ "permissions` WHERE id = '" + pPermission.getPermissionID() + "' LIMIT 1");
				preparedStatement.execute();
			} catch (SQLException e) {
				StringWriter sw = new StringWriter();e.printStackTrace(new PrintWriter(sw));TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
			} finally {
				TeachingIt.getInstance().getConnector().closeConnection(con);
			}
		}
	}

	public int getGroupID() {
		return groupID;
	}
}
