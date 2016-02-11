package de.simonbrungs.teachingit.api.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import de.simonbrungs.teachingit.TeachingIt;
import de.simonbrungs.teachingit.api.groups.Group;

public class Account {
	private int id;

	public Account(int pID) {
		id = pID;
	}

	public String getUserName() {

	}

	public int getID() {
		return id;
	}

	public String getMetaInfo(String pMetaIdentifier) {

	}

	public void setUserName() {

	}

	public boolean setMetaInfo(String pMetaIdentifier) {

	}

	public boolean removeMetaInfo(String pMetaIdentifier) {

	}

	public boolean isActivated() {

	}

	public void addGroup() {

	}

	public Group getGroup() {

	}

	public void setEmail(String pEmail) {

	}

	public String getEmail() {

	}

	public void setPassword() {

	}

	public boolean hasPermission(String pPermission) {
		return getGroup().hasPermission(pPermission);
	}

	public void setPermissionHeight() {

	}

	public long getRegistrationDate() {

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
