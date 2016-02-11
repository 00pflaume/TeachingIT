package de.simonbrungs.teachingit.api.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.simonbrungs.teachingit.TeachingIt;
import de.simonbrungs.teachingit.api.groups.Group;

public class Account {
	private int id;

	public Account(int pID) {
		id = pID;
	}

	public String getUserName() {
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		try {
			ResultSet resultSet = con.createStatement()
					.executeQuery("select user from `" + TeachingIt.getInstance().getConnection().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnection().getTablePrefix() + "users` WHERE id='" + id
							+ "' LIMIT 1");
			if (resultSet.next()) {
				return resultSet.getString("user");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			TeachingIt.getInstance().getConnection().closeConnection(con);
		}
		return null;
	}

	public int getID() {
		return id;
	}

	public String getMetaInfo(int pMetaInfoID) {
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		try {
			ResultSet resultSet = con.createStatement()
					.executeQuery("select email from `" + TeachingIt.getInstance().getConnection().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnection().getTablePrefix() + "users` WHERE userid='" + id
							+ "' LIMIT 1");
			if (resultSet.next()) {
				return resultSet.getString("email");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			TeachingIt.getInstance().getConnection().closeConnection(con);
		}
		return null;
	}

	public String getMetaInfo(String pMetaIdentifier) {
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		try {
			ResultSet resultSet = con.createStatement()
					.executeQuery("select email from `" + TeachingIt.getInstance().getConnection().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnection().getTablePrefix() + "users` WHERE userid='" + id
							+ "' LIMIT 1");
			if (resultSet.next()) {
				return resultSet.getString("email");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			TeachingIt.getInstance().getConnection().closeConnection(con);
		}
		return null;
	}

	public void setUserName() {

	}

	public boolean addMetaInfo(String pMetaIdentifier) {
		if (getMetaInfo(pMetaIdentifier) != null)
			return false;

	}

	public void removeMetaInfo(String pMetaIdentifier) {

	}

	public boolean isActivated() {
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		try {
			ResultSet resultSet = con.createStatement()
					.executeQuery("select activated from `" + TeachingIt.getInstance().getConnection().getDatabase()
							+ "`.`" + TeachingIt.getInstance().getConnection().getTablePrefix() + "users` WHERE id='"
							+ id + "' LIMIT 1");
			if (resultSet.next()) {
				return resultSet.getByte("activated") == 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			TeachingIt.getInstance().getConnection().closeConnection(con);
		}
		return false;
	}

	public Group getGroup() {
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		try {
			ResultSet resultSet = con.createStatement()
					.executeQuery("select groupid from `" + TeachingIt.getInstance().getConnection().getDatabase()
							+ "`.`" + TeachingIt.getInstance().getConnection().getTablePrefix()
							+ "groupsusers` WHERE userid='" + id + "' LIMIT 1");
			if (resultSet.next()) {
				return new Group(resultSet.getInt("groupid"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			TeachingIt.getInstance().getConnection().closeConnection(con);
		}
		return null;
	}

	public void setEmail(String pEmail) {

	}

	public String getEmail() {
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		try {
			ResultSet resultSet = con.createStatement()
					.executeQuery("select email from `" + TeachingIt.getInstance().getConnection().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnection().getTablePrefix() + "users` WHERE id='" + id
							+ "' LIMIT 1");
			if (resultSet.next()) {
				return resultSet.getString("email");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			TeachingIt.getInstance().getConnection().closeConnection(con);
		}
		return null;
	}

	public void setPassword(String pPassword) {
		pPassword = TeachingIt.getInstance().getAccountManager().encryptPassword(pPassword);
	}

	public boolean hasPermission(String pPermission) {
		return getGroup().hasPermission(pPermission);
	}

	public void setPermissionHeight() {

	}

	public long getRegistrationDate() {
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		try {
			ResultSet resultSet = con.createStatement().executeQuery(
					"select regestrationdate from `" + TeachingIt.getInstance().getConnection().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnection().getTablePrefix() + "users` WHERE id='" + id
							+ "' LIMIT 1");
			if (resultSet.next()) {
				return resultSet.getLong("regestrationdate");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			TeachingIt.getInstance().getConnection().closeConnection(con);
		}
		return 0;
	}

	public void setActivated(byte status) {

	}

	private void removeFromGroup() {
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		PreparedStatement preparedStatement;
		try {
			preparedStatement = con
					.prepareStatement("DELETE FROM `" + TeachingIt.getInstance().getConnection().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnection().getTablePrefix()
							+ "groupsusers` WHERE userid = '" + id + "' LIMIT 1");
			preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		TeachingIt.getInstance().getConnection().closeConnection(con);
	}

	public void setGroup(Group group) {
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		PreparedStatement preparedStatement;
		removeFromGroup();
		try {
			preparedStatement = con
					.prepareStatement("insert into  `" + TeachingIt.getInstance().getConnection().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnection().getTablePrefix() + "groupsusers` values (?, ?)");
			preparedStatement.setInt(1, id);
			preparedStatement.setInt(2, group.getGroupID());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		TeachingIt.getInstance().getConnection().closeConnection(con);
	}
}
