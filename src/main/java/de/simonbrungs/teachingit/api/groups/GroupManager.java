package de.simonbrungs.teachingit.api.groups;

import de.simonbrungs.teachingit.TeachingIt;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

public class GroupManager {
	private static GroupManager groupManager = null;

	public GroupManager() throws IllegalAccessException {
		if (groupManager != null)
			throw new IllegalAccessException();
		groupManager = this;
	}

	public static GroupManager getInstance() {
		return groupManager;
	}

	public Group createGroup(String pGroupName, int pSupergroup, int pPermissionheight)
			throws IllegalArgumentException {
		if (pGroupName.length() > 128)
			throw new IllegalArgumentException();
		Group group = getGroup(pGroupName);
		if (group != null)
			return group;
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		try {
			PreparedStatement preparedStatement = con
					.prepareStatement("insert into  `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix() + "groups` values (?, ?, ?, ?)");
			preparedStatement.setString(1, pGroupName);
			preparedStatement.setNull(2, 2);
			preparedStatement.setInt(3, pSupergroup);
			preparedStatement.setInt(4, pPermissionheight);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		TeachingIt.getInstance().getConnector().closeConnection(con);
		return getGroup(pGroupName);
	}

	public void removeGroup(int pGroupID) {
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		PreparedStatement preparedStatement;
		try {
			preparedStatement = con
					.prepareStatement("DELETE FROM `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix() + "groups` WHERE id = '"
							+ pGroupID + "' LIMIT 1");
			preparedStatement.execute();
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		TeachingIt.getInstance().getConnector().closeConnection(con);
	}

	public void removeGroup(String pGroupName) {
		removeGroup(getGroup(pGroupName));
	}

	public void removeGroup(Group pGroup) {
		removeGroup(pGroup.getGroupID());
	}

	public Group getGroup(int pGroupID) {
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		ResultSet resultSet;
		int groupID = -1;
		try {
			resultSet = con.createStatement()
					.executeQuery("select id from `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix() + "groups` WHERE id = '"
							+ pGroupID + "'");
			if (resultSet.next()) {
				groupID = resultSet.getInt("id");
			}
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		TeachingIt.getInstance().getConnector().closeConnection(con);
		if (groupID == -1)
			return null;
		return new Group(groupID);
	}

	public Group getGroup(String pGroupName) {
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		int groupID = -1;
		try {
			PreparedStatement prepStmt = con
					.prepareStatement("select id from `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix() + "groups` WHERE groupname = ?");
			prepStmt.setString(1, pGroupName);
			ResultSet resultSet = prepStmt.getResultSet();
			if (resultSet.next()) {
				groupID = resultSet.getInt("id");
			}
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		TeachingIt.getInstance().getConnector().closeConnection(con);
		if (groupID == -1)
			return null;
		return new Group(groupID);
	}

	public ArrayList<Group> getGroups() {
		ArrayList<Group> groups = new ArrayList<>();
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		ResultSet resultSet;
		try {
			resultSet = con.createStatement()
					.executeQuery("select id from `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix() + "groups`");
			while (resultSet.next()) {
				groups.add(new Group(resultSet.getInt("id")));
			}
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		TeachingIt.getInstance().getConnector().closeConnection(con);
		return groups;
	}
}
