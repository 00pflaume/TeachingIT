package de.simonbrungs.teachingit.api.users;

import java.net.SocketAddress;
import java.util.HashMap;

public class User {
	private Account account;
	private String path;
	private SocketAddress socketAddress;
	private HashMap<String, Object> postRequests;
	private HashMap<String, String> userVars = new HashMap<>();

	public User(String pPath, Account pAccount, SocketAddress pSocketAddress, HashMap<String, Object> pPostRequests) {
		path = pPath;
		account = pAccount;
		socketAddress = pSocketAddress;
		postRequests = pPostRequests;
	}

	public Object getPostRequest(String pKey) {
		return postRequests.get(pKey);
	}

	/**
	 * As soon as the site is builded up this is getting deleted
	 */
	public void setUserVar(String pKey, String pValue) {
		userVars.put(pKey, pValue);
	}

	public String getUserVar(String pKey) {
		return userVars.get(pKey);
	}

	public String removeUserVar(String pKey) {
		return userVars.remove(pKey);
	}

	public void setAccount(Account pAccount) {

	}

	public String getStringPostRequest(String pKey) {
		Object string = postRequests.get(pKey);
		if (string == null)
			if (string instanceof String)
				return (String) string;
		return null;
	}

	public SocketAddress getSocketAddress() {
		return socketAddress;
	}

	public String getCalledPath() {
		return path;
	}

	public Account getAccount() {
		return account;
	}

}
