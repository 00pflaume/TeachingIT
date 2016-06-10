package de.simonbrungs.teachingit.api.users;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import de.simonbrungs.teachingit.TeachingIt;
import de.simonbrungs.teachingit.api.groups.Group;
import de.simonbrungs.teachingit.connectors.MySQLConnector;

public class Account {
	private int id;

	public Account(int pID) {
		id = pID;
	}

	public String getUserName() {
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		try {
			ResultSet resultSet = con.createStatement()
					.executeQuery("select user from `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix() + "users` WHERE id='" + id
							+ "' LIMIT 1");
			if (resultSet.next()) {
				return resultSet.getString("user");
			}
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		} finally {
			TeachingIt.getInstance().getConnector().closeConnection(con);
		}
		return null;
	}

	public int getID() {
		return id;
	}

	public String getMetaInfo(String pMetaIdentifier) {
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		try {
			PreparedStatement prepStmt = con
					.prepareStatement("select metavalue from " + TeachingIt.getInstance().getConnector().getDatabase()
							+ ".`" + TeachingIt.getInstance().getConnector().getTablePrefix()
							+ "usermeta` WHERE userid= " + id + " AND metavalue= ? LIMIT 1");
			prepStmt.setString(1, pMetaIdentifier);
			ResultSet resultSet = prepStmt.executeQuery();
			if (resultSet.next()) {
				return resultSet.getString("metavalue");
			}
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		} finally {
			TeachingIt.getInstance().getConnector().closeConnection(con);
		}
		return null;
	}

	public void setUserName(String pUserName) {
		Connection con = MySQLConnector.getInstance().createConnection();
		try {
			PreparedStatement preparedStatement = con
					.prepareStatement("UPDATE `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix() + "users` set user= ? WHERE id='"
							+ id + "' LIMIT 1");
			preparedStatement.setString(1, pUserName);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			MySQLConnector.getInstance().closeConnection(con);
		}
	}

	public void setMetaInfo(String pMetaIdentifier, String pMetaValue) {
		if (getMetaInfo(pMetaIdentifier) != null)
			removeMetaInfo(pMetaIdentifier);
		Connection con = MySQLConnector.getInstance().createConnection();
		try {
			PreparedStatement preparedStatement = con.prepareStatement("insert into  `"
					+ TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
					+ TeachingIt.getInstance().getConnector().getTablePrefix() + "usermeta` values (?, ?, ?, ?)");
			preparedStatement.setString(1, pMetaIdentifier);
			preparedStatement.setString(2, pMetaValue);
			preparedStatement.setNull(3, 3);
			preparedStatement.setInt(4, id);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			MySQLConnector.getInstance().closeConnection(con);
		}
	}

	public void removeMetaInfo(String pMetaIdentifier) {
		Connection con = MySQLConnector.getInstance().createConnection();
		try {
			PreparedStatement preparedStatement = con
					.prepareStatement("DELETE FROM `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix() + "usermeta` WHERE userid = '"
							+ id + "' AND metakey = ?");
			preparedStatement.setString(1, pMetaIdentifier);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			MySQLConnector.getInstance().closeConnection(con);
		}
	}

	public byte isActivated() {
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		try {
			ResultSet resultSet = con.createStatement()
					.executeQuery("select activated from `" + TeachingIt.getInstance().getConnector().getDatabase()
							+ "`.`" + TeachingIt.getInstance().getConnector().getTablePrefix() + "users` WHERE id='"
							+ id + "' LIMIT 1");
			if (resultSet.next()) {
				return resultSet.getByte("activated");
			}
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		} finally {
			TeachingIt.getInstance().getConnector().closeConnection(con);
		}
		return 0;
	}

	public Group getGroup() {
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		try {
			ResultSet resultSet = con.createStatement()
					.executeQuery("select groupid from `" + TeachingIt.getInstance().getConnector().getDatabase()
							+ "`.`" + TeachingIt.getInstance().getConnector().getTablePrefix()
							+ "groupsusers` WHERE userid='" + id + "' LIMIT 1");
			if (resultSet.next()) {
				return new Group(resultSet.getInt("groupid"));
			} else {
				return new Group(1);
			}
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		} finally {
			TeachingIt.getInstance().getConnector().closeConnection(con);
		}
		return null;
	}

	public void setEmail(String pEmail) {
		Connection con = MySQLConnector.getInstance().createConnection();
		try {
			PreparedStatement preparedStatement = con
					.prepareStatement("UPDATE `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix()
							+ "users` set email= ? WHERE id='" + id + "' LIMIT 1");
			preparedStatement.setString(1, pEmail);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			MySQLConnector.getInstance().closeConnection(con);
		}
	}

	public String getEmail() {
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		try {
			ResultSet resultSet = con.createStatement()
					.executeQuery("select email from `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix() + "users` WHERE id='" + id
							+ "' LIMIT 1");
			if (resultSet.next()) {
				return resultSet.getString("email");
			}
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		} finally {
			TeachingIt.getInstance().getConnector().closeConnection(con);
		}
		return null;
	}

	/**
	 * 
	 * @param pPassword
	 *            Needs to be encrypted in SHA-1
	 */
	public void setPassword(String pPassword) {
		pPassword = TeachingIt.getInstance().getAccountManager().encryptPassword(pPassword);
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		try {
			PreparedStatement preparedStatement = con
					.prepareStatement("UPDATE `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix()
							+ "groups` set password = ? WHERE ID='" + id + "' LIMIT 1");
			preparedStatement.setString(1, pPassword);
			preparedStatement.setInt(1, id);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		} finally {
			TeachingIt.getInstance().getConnector().closeConnection(con);
		}
	}

	public boolean hasPermission(String pPermission) {
		return getGroup().hasPermission(pPermission);
	}

	public long getRegistrationDate() {
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		try {
			ResultSet resultSet = con.createStatement().executeQuery(
					"select regestrationdate from `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix() + "users` WHERE id='" + id
							+ "' LIMIT 1");
			if (resultSet.next()) {
				return resultSet.getLong("regestrationdate");
			}
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		} finally {
			TeachingIt.getInstance().getConnector().closeConnection(con);
		}
		return 0;
	}

	public void setActivated(byte status) {
		Connection con = MySQLConnector.getInstance().createConnection();
		try {
			PreparedStatement preparedStatement = con
					.prepareStatement("UPDATE `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix()
							+ "users` set activated= ? WHERE id='" + id + "' LIMIT 1");
			preparedStatement.setByte(1, status);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			MySQLConnector.getInstance().closeConnection(con);
		}
	}

	private void removeFromGroup() {
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		PreparedStatement preparedStatement;
		try {
			preparedStatement = con
					.prepareStatement("DELETE FROM `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix() + "groupsusers` WHERE userid = '"
							+ id + "' LIMIT 1");
			preparedStatement.execute();
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		TeachingIt.getInstance().getConnector().closeConnection(con);
	}

	public void setGroup(Group group) {
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		PreparedStatement preparedStatement;
		removeFromGroup();
		try {
			preparedStatement = con
					.prepareStatement("insert into  `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix() + "groupsusers` values (?, ?)");
			preparedStatement.setInt(1, id);
			preparedStatement.setInt(2, group.getGroupID());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		TeachingIt.getInstance().getConnector().closeConnection(con);
	}
}
