package de.simonbrungs.teachingit.api.users;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.simonbrungs.teachingit.TeachingIt;
import de.simonbrungs.teachingit.api.events.AccountDeleteEvent;
import de.simonbrungs.teachingit.api.events.AfterAccountCreationEvent;
import de.simonbrungs.teachingit.api.events.PreAccountCreationEvent;

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

	public Account getAccount(String pUsername) {
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

	public Account getAccount(int pUserID) {
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
		if (getAccount(pUserName) != null)
			return null;
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		PreAccountCreationEvent preAccountCreationEvent = new PreAccountCreationEvent(pUserName, pEmail, pActive);
		TeachingIt.getInstance().getEventExecuter().executeEvent(preAccountCreationEvent);
		if (preAccountCreationEvent.isCanceld())
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
			byte activated = 0;
			if (preAccountCreationEvent.isActivated())
				activated = 1;
			preparedStatement.setByte(6, activated);
			preparedStatement.executeUpdate();
			Account account = getAccount(pUserName);
			TeachingIt.getInstance().getEventExecuter().executeEvent(new AfterAccountCreationEvent(account));
			return account;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			TeachingIt.getInstance().getConnection().closeConnection(con);
		}
	}

	public void removeAccount(int pID) {
		removeAccount(getAccount(pID));
	}

	public boolean removeAccount(Account pAccount) {
		if (pAccount == null)
			return false;
		AccountDeleteEvent accountDeleteEvent = new AccountDeleteEvent(pAccount);
		TeachingIt.getInstance().getEventExecuter().executeEvent(accountDeleteEvent);
		if (!accountDeleteEvent.getShouldBeDeleted())
			return false;
		Connection con = TeachingIt.getInstance().getConnection().createConnection();
		try {
			PreparedStatement preparedStatement = con
					.prepareStatement("DELETE FROM `" + TeachingIt.getInstance().getConnection().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnection().getTablePrefix() + "users` WHERE id = '"
							+ pAccount.getID() + "' LIMIT 1");
			preparedStatement.executeUpdate();
			preparedStatement = con
					.prepareStatement("DELETE FROM `" + TeachingIt.getInstance().getConnection().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnection().getTablePrefix() + "usermeta` WHERE userid = '"
							+ pAccount.getID() + "'");
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			TeachingIt.getInstance().getConnection().closeConnection(con);
		}
	}

	public void removeAccount(String pUserName) {
		removeAccount(getAccount(pUserName));
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
