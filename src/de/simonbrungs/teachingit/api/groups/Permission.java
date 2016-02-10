package de.simonbrungs.teachingit.api.groups;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

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
			e.printStackTrace();
		}
		TeachingIt.getInstance().getConnection().closeConnection(con);
	}

	public Permission(String pPermissionName) {
		permissionName = pPermissionName.toLowerCase();
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		ResultSet resultSet;
		try {
			resultSet = con.createStatement()
					.executeQuery("select id from `" + TeachingIt.getInstance().getConnection().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnection().getTablePrefix()
							+ "permissions` WHERE permission='" + permissionName + "' LIMIT 1");
			if (resultSet.next()) {
				permissionID = resultSet.getInt("id");
			} else {
				permissionID = -1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		TeachingIt.getInstance().getConnection().closeConnection(con);
	}

	public static boolean permissionExists(String pPermissionName) {
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		ResultSet resultSet;
		try {
			resultSet = con.createStatement()
					.executeQuery("select id from `" + TeachingIt.getInstance().getConnection().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnection().getTablePrefix()
							+ "permissions` WHERE permission='" + pPermissionName + "' LIMIT 1");
			if (resultSet.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
}
