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
	 * As soon as the site is builded up the data which were inputed here gets
	 * deleted
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
