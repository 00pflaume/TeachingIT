package de.simonbrungs.teachingit.api.users;

import java.util.HashMap;

public class TempUser {
	private Account account;
	private String path;
	private String ipAddress;
	private HashMap<String, Object> postRequests;
	private HashMap<String, String> userVars = new HashMap<>();

	public TempUser(String pPath, Account pAccount, String pIPAddress, HashMap<String, Object> pPostRequests) {
		path = pPath;
		account = pAccount;
		ipAddress = pIPAddress;
		postRequests = pPostRequests;
	}

	public Object getPostRequest(String pKey) {
		return postRequests.get(pKey);
	}

	/**
	 * As soon as the site is builded up this is getting deleted
	 * 
	 * @param pKey
	 *            The key for the value
	 * @param pValue
	 *            The value that should be set
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
		account = pAccount;
	}

	public String getStringPostRequest(String pKey) {
		Object string = postRequests.get(pKey);
		if (string == null)
			if (string instanceof String)
				return (String) string;
		return null;
	}

	public String getIPAddress() {
		return ipAddress;
	}

	public String getCalledPath() {
		return path;
	}

	public Account getAccount() {
		return account;
	}

}
