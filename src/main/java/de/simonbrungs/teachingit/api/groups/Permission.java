package de.simonbrungs.teachingit.api.groups;

import de.simonbrungs.teachingit.TeachingIt;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class Permission {
	private int permissionID;
	private String permissionName;

	public Permission(int pPermissionID) {
		permissionID = pPermissionID;
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		ResultSet resultSet;
		try {
			resultSet = con.createStatement()
					.executeQuery("select permission from `" + TeachingIt.getInstance().getConnector().getDatabase()
							+ "`.`" + TeachingIt.getInstance().getConnector().getTablePrefix()
							+ "permissions` WHERE id='" + pPermissionID + "' LIMIT 1");
			if (resultSet.next()) {
				permissionName = resultSet.getString("permission");
			} else {
				permissionName = "";
				permissionID = -1;
			}
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		TeachingIt.getInstance().getConnector().closeConnection(con);
	}

	public Permission(String pPermissionName) {
		permissionName = pPermissionName.toLowerCase();
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		ResultSet resultSet;
		try {
			PreparedStatement prepStmt = con
					.prepareStatement("select id from `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix()
							+ "permissions` WHERE permission=? LIMIT 1");
			prepStmt.setString(1, permissionName);
			resultSet = prepStmt.executeQuery();
			if (resultSet.next()) {
				permissionID = resultSet.getInt("id");
			} else {
				permissionID = -1;
			}
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		TeachingIt.getInstance().getConnector().closeConnection(con);
	}

	public static boolean permissionExists(String pPermissionName) {
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		ResultSet resultSet;
		try {
			PreparedStatement prepStmt = con
					.prepareStatement("select id from `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix()
							+ "permissions` WHERE permission=? LIMIT 1");
			prepStmt.setString(1, pPermissionName.toLowerCase());
			resultSet = prepStmt.executeQuery();
			return resultSet.next();
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		TeachingIt.getInstance().getConnector().closeConnection(con);
		return false;
	}

	public static boolean permissionExists(int pPermissionID) {
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		ResultSet resultSet;
		try {
			resultSet = con.createStatement()
					.executeQuery("select permission from `" + TeachingIt.getInstance().getConnector().getDatabase()
							+ "`.`" + TeachingIt.getInstance().getConnector().getTablePrefix()
							+ "permissions` WHERE id='" + pPermissionID + "' LIMIT 1");
			return resultSet.next();
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		TeachingIt.getInstance().getConnector().closeConnection(con);
		return false;
	}

	public static Permission createPermission(String pPermission) throws IllegalArgumentException {
		pPermission = pPermission.toLowerCase();
		if (pPermission.length() > 512)
			throw new IllegalArgumentException();
		Permission perm = new Permission(pPermission);
		if (perm.getPermissionID() == -1) {
			Connection con = TeachingIt.getInstance().getConnector().createConnection();
			try {
				PreparedStatement preparedStatement = con
						.prepareStatement("insert into  `" + TeachingIt.getInstance().getConnector().getDatabase()
								+ "`.`" + TeachingIt.getInstance().getConnector().getTablePrefix()
								+ "users` values (?, ?, ?, ?, ?, ?)");
				preparedStatement.setNull(1, 1);
				preparedStatement.setString(2, pPermission);
				preparedStatement.executeQuery();
			} catch (SQLException e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
			}
			TeachingIt.getInstance().getConnector().closeConnection(con);
			perm = new Permission(pPermission);
		}
		return perm;
	}

	public String getPermissionName() {
		return permissionName;
	}

	public int getPermissionID() {
		return permissionID;
	}
}
