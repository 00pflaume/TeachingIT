package de.simonbrungs.teachingit.api.groups;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import de.simonbrungs.teachingit.TeachingIt;

public class Permission {
	private int permissionID;
	private String permissionName;

	public Permission(int pPermissionID) {
		permissionID = pPermissionID;
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		ResultSet resultSet;
		try {
			resultSet = con.createStatement()
					.executeQuery("select permission from `" + TeachingIt.getInstance().getConnection().getDatabase()
							+ "`.`" + TeachingIt.getInstance().getConnection().getTablePrefix()
							+ "permissions` WHERE id='" + pPermissionID + "' LIMIT 1");
			if (resultSet.next()) {
				permissionName = resultSet.getString("permission");
			} else {
				permissionName = "";
				permissionID = -1;
			}
		} catch (SQLException e) {
			TeachingIt.getInstance().getLogger().log(Level.WARNING, e.getMessage());
		}
		TeachingIt.getInstance().getConnection().closeConnection(con);
	}

	public Permission(String pPermissionName) {
		permissionName = pPermissionName.toLowerCase();
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		ResultSet resultSet;
		try {
			PreparedStatement prepStmt = con
					.prepareStatement("select id from `" + TeachingIt.getInstance().getConnection().getDatabase()
							+ "`.`" + TeachingIt.getInstance().getConnection().getTablePrefix()
							+ "permissions` WHERE permission=? LIMIT 1");
			prepStmt.setString(1, permissionName);
			resultSet = prepStmt.executeQuery();
			if (resultSet.next()) {
				permissionID = resultSet.getInt("id");
			} else {
				permissionID = -1;
			}
		} catch (SQLException e) {
			TeachingIt.getInstance().getLogger().log(Level.WARNING, e.getMessage());
		}
		TeachingIt.getInstance().getConnection().closeConnection(con);
	}

	public static boolean permissionExists(String pPermissionName) {
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		ResultSet resultSet;
		try {
			PreparedStatement prepStmt = con
					.prepareStatement("select id from `" + TeachingIt.getInstance().getConnection().getDatabase()
							+ "`.`" + TeachingIt.getInstance().getConnection().getTablePrefix()
							+ "permissions` WHERE permission=? LIMIT 1");
			prepStmt.setString(1, pPermissionName.toLowerCase());
			resultSet = prepStmt.executeQuery();
			if (resultSet.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			TeachingIt.getInstance().getLogger().log(Level.WARNING, e.getMessage());
		}
		TeachingIt.getInstance().getConnection().closeConnection(con);
		return false;
	}

	public static boolean permissionExists(int pPermissionID) {
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		ResultSet resultSet;
		try {
			resultSet = con.createStatement()
					.executeQuery("select permission from `" + TeachingIt.getInstance().getConnection().getDatabase()
							+ "`.`" + TeachingIt.getInstance().getConnection().getTablePrefix()
							+ "permissions` WHERE id='" + pPermissionID + "' LIMIT 1");
			if (resultSet.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			TeachingIt.getInstance().getLogger().log(Level.WARNING, e.getMessage());
		}
		TeachingIt.getInstance().getConnection().closeConnection(con);
		return false;
	}

	public String getPermissionName() {
		return permissionName;
	}

	public int getPermissionID() {
		return permissionID;
	}

	public static Permission createPermission(String pPermission) throws IllegalArgumentException {
		pPermission = pPermission.toLowerCase();
		if (pPermission.length() > 512)
			throw new IllegalArgumentException();
		Permission perm = new Permission(pPermission);
		if (perm.getPermissionID() == -1) {
			Connection con = TeachingIt.getInstance().getConnection().createConnection();
			try {
				PreparedStatement preparedStatement = con
						.prepareStatement("insert into  `" + TeachingIt.getInstance().getConnection().getDatabase()
								+ "`.`" + TeachingIt.getInstance().getConnection().getTablePrefix()
								+ "users` values (?, ?, ?, ?, ?, ?)");
				preparedStatement.setNull(1, 1);
				preparedStatement.setString(2, pPermission);
				preparedStatement.executeQuery();
			} catch (SQLException e) {
				TeachingIt.getInstance().getLogger().log(Level.WARNING, e.getMessage());
			}
			TeachingIt.getInstance().getConnection().closeConnection(con);
			perm = new Permission(pPermission);
		}
		return perm;
	}
}
