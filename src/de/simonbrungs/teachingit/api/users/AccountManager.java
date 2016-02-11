package de.simonbrungs.teachingit.api.users;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.simonbrungs.teachingit.TeachingIt;
import de.simonbrungs.teachingit.api.events.CreateAccountEvent;

public class AccountManager {
	public Account loginUser(String pUsername, String pPassword) {
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		try {
			PreparedStatement prepStmt = con.prepareStatement(
					"select id, activated from " + TeachingIt.getInstance().getConnection().getDatabase() + ".`"
							+ TeachingIt.getInstance().getConnection().getTablePrefix()
							+ "users` WHERE user= ? AND password= ? LIMIT 1");
			prepStmt.setString(1, pUsername);
			prepStmt.setString(2, encryptPassword(pPassword));
			ResultSet resultSet = prepStmt.executeQuery();
			if (resultSet.next()) {
				Account account = new Account(resultSet.getInt("id"));
				if (resultSet.getByte("activated") == 1) {
					return account;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			TeachingIt.getInstance().getConnection().closeConnection(con);
		}
	}

	public Account getUser(String pUsername) {
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		try {
			PreparedStatement prepStmt = con.prepareStatement("select id, activated from "
					+ TeachingIt.getInstance().getConnection().getDatabase() + ".`"
					+ TeachingIt.getInstance().getConnection().getTablePrefix() + "users` WHERE user= ? LIMIT 1");
			prepStmt.setString(1, pUsername);
			ResultSet resultSet = prepStmt.executeQuery();
			if (resultSet.next()) {
				return new Account(resultSet.getInt("id"));
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			TeachingIt.getInstance().getConnection().closeConnection(con);
		}
	}

	public Account getUser(int pUserID) {
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		try {
			ResultSet resultSet = con.createStatement()
					.executeQuery("select id from `" + TeachingIt.getInstance().getConnection().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnection().getTablePrefix() + "users` WHERE id='" + pUserID
							+ "' LIMIT 1");
			if (resultSet.next()) {
				return new Account(pUserID);
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			TeachingIt.getInstance().getConnection().closeConnection(con);
		}
	}

	public Account createAccount(String pUserName, String pEmail, String pPassword, boolean pActive) {
		if (getUser(pUserName) != null)
			return null;
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		CreateAccountEvent createAccountEvent = new CreateAccountEvent(pUserName, pEmail, pActive);
		TeachingIt.getInstance().getEventExecuter().executeEvent(createAccountEvent);
		if (createAccountEvent.isCanceld())
			return null;
		try {
			PreparedStatement preparedStatement = con.prepareStatement("insert into  `"
					+ TeachingIt.getInstance().getConnection().getDatabase() + "`.`"
					+ TeachingIt.getInstance().getConnection().getTablePrefix() + "users` values (?, ?, ?, ?, ?, ?)");
			preparedStatement.setString(1, pUserName);
			preparedStatement.setString(2, pEmail);
			preparedStatement.setString(3, encryptPassword(pPassword));
			preparedStatement.setLong(4, System.currentTimeMillis() / 1000L);
			preparedStatement.setInt(5, 0);
			preparedStatement.setNull(3, 5);
			preparedStatement.setInt(6, 1);
			preparedStatement.executeUpdate();
			return getUser(pUserName);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			TeachingIt.getInstance().getConnection().closeConnection(con);
		}
	}

	public void removeUser(int pID) {

	}

	public void removeUser(String pUserName) {

	}

	public String encryptPassword(String pPassword) {
		MessageDigest mDigest;
		try {
			mDigest = MessageDigest.getInstance("SHA1");
			byte[] result = mDigest.digest(pPassword.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < result.length; i++) {
				sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}
