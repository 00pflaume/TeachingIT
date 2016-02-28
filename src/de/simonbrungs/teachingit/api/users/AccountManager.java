package de.simonbrungs.teachingit.api.users;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import de.simonbrungs.teachingit.TeachingIt;
import de.simonbrungs.teachingit.api.events.AccountDeleteEvent;
import de.simonbrungs.teachingit.api.events.AfterAccountCreationEvent;
import de.simonbrungs.teachingit.api.events.PreAccountCreationEvent;

public class AccountManager {
	private HashMap<String, SessionKeyEntry> sessionKeys = new HashMap<>();
	private static AccountManager accountmanager = null;

	private class SessionKeyEntry {
		private long creationTime;
		private Object content;

		public SessionKeyEntry(Object pContent) {
			content = pContent;
			creationTime = System.currentTimeMillis() / 1000;
		}

		public Object getContent() {
			return content;
		}

		public long getCreationTime() {
			return creationTime;
		}
	}

	public AccountManager() throws IllegalAccessException {
		if (accountmanager != null)
			throw new IllegalAccessException();
		accountmanager = this;
		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				clear();
			}
		}, 86400000, 86400000);
	}

	public static AccountManager getInstance() {
		return accountmanager;
	}

	public void clear() {
		ArrayList<String> toRemove = new ArrayList<>();
		Set<Entry<String, SessionKeyEntry>> entrys = sessionKeys.entrySet();
		for (Entry<String, SessionKeyEntry> entry : entrys) {
			if (entry.getValue().getCreationTime() < (System.currentTimeMillis() / 1000 - 86400)) {
				toRemove.add(entry.getKey());
			}
		}
		for (String key : toRemove)
			sessionKeys.remove(key);
	}

	public Account loginUser(String pUsername, String pEncryptPassword) {
		if (pUsername == null || pEncryptPassword == null)
			return null;
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		try {
			PreparedStatement prepStmt = con.prepareStatement(
					"select id, activated from " + TeachingIt.getInstance().getConnector().getDatabase() + ".`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix()
							+ "users` WHERE user= ? AND password= ? LIMIT 1");
			prepStmt.setString(1, pUsername);
			prepStmt.setString(2, encryptPassword(pEncryptPassword));
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
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
			return null;
		} finally {
			TeachingIt.getInstance().getConnector().closeConnection(con);
		}
	}

	public Object getSessionKey(String pIPAddress, String pKey) {
		SessionKeyEntry sessionKey = sessionKeys.get(pIPAddress + pKey);
		if (sessionKey == null)
			return null;
		return sessionKey.getContent();
	}

	public Object removeSessionKey(String pIPAddress, String pKey) {
		return sessionKeys.remove(pIPAddress + pKey);
	}

	public void setSessionKey(String pIPAddress, String pKey, Object pValue) {
		sessionKeys.put(pIPAddress + pKey, new SessionKeyEntry(pValue));
	}

	public Account getAccount(String pUsername) {
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		try {
			PreparedStatement prepStmt = con.prepareStatement("select id, activated from "
					+ TeachingIt.getInstance().getConnector().getDatabase() + ".`"
					+ TeachingIt.getInstance().getConnector().getTablePrefix() + "users` WHERE user= ? LIMIT 1");
			prepStmt.setString(1, pUsername);
			ResultSet resultSet = prepStmt.executeQuery();
			if (resultSet.next()) {
				return new Account(resultSet.getInt("id"));
			} else {
				return null;
			}
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
			return null;
		} finally {
			TeachingIt.getInstance().getConnector().closeConnection(con);
		}
	}

	public Account getAccount(int pUserID) {
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		try {
			ResultSet resultSet = con.createStatement()
					.executeQuery("select id from `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix() + "users` WHERE id='" + pUserID
							+ "' LIMIT 1");
			if (resultSet.next()) {
				return new Account(pUserID);
			} else {
				return null;
			}
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
			return null;
		} finally {
			TeachingIt.getInstance().getConnector().closeConnection(con);
		}
	}

	/**
	 * 
	 * @param pUserName
	 * @param pEmail
	 * @param pPassword
	 *            Needs to be encrypted by SHA-1 to encrypt use
	 *            encryptPassword(pPassword: String)
	 * @param pActive
	 * @return Returns the account if it was created else it returns null
	 */
	public Account createAccount(String pUserName, String pEmail, String pPassword, byte pActive) {
		if (getAccount(pUserName) != null)
			return null;
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		PreAccountCreationEvent preAccountCreationEvent = new PreAccountCreationEvent(pUserName, pEmail, pActive);
		TeachingIt.getInstance().getEventExecuter().executeEvent(preAccountCreationEvent);
		if (preAccountCreationEvent.isCanceld())
			return null;
		try {
			PreparedStatement preparedStatement = con.prepareStatement("insert into  `"
					+ TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
					+ TeachingIt.getInstance().getConnector().getTablePrefix() + "users` values (?, ?, ?, ?, ?, ?)");
			preparedStatement.setString(1, pUserName);
			preparedStatement.setString(2, pEmail);
			preparedStatement.setString(3, pPassword);
			preparedStatement.setNull(4, 5);
			preparedStatement.setLong(5, System.currentTimeMillis() / 1000L);
			preparedStatement.setInt(6, 0);
			byte activated = (preAccountCreationEvent.getActivated());
			preparedStatement.setByte(6, activated);
			preparedStatement.executeUpdate();
			Account account = getAccount(pUserName);
			TeachingIt.getInstance().getEventExecuter().executeEvent(new AfterAccountCreationEvent(account));
			return account;
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
			return null;
		} finally {
			TeachingIt.getInstance().getConnector().closeConnection(con);
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
		Connection con = TeachingIt.getInstance().getConnector().createConnection();
		try {
			PreparedStatement preparedStatement = con
					.prepareStatement("DELETE FROM `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix() + "users` WHERE id = '"
							+ pAccount.getID() + "' LIMIT 1");
			preparedStatement.executeUpdate();
			preparedStatement = con
					.prepareStatement("DELETE FROM `" + TeachingIt.getInstance().getConnector().getDatabase() + "`.`"
							+ TeachingIt.getInstance().getConnector().getTablePrefix() + "usermeta` WHERE userid = '"
							+ pAccount.getID() + "'");
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
			return false;
		} finally {
			TeachingIt.getInstance().getConnector().closeConnection(con);
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
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		return null;
	}
}
