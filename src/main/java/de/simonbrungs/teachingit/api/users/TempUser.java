package de.simonbrungs.teachingit.api.users;

import java.util.HashMap;

public class TempUser {
	private final String path;
	private final String ipAddress;
	private final HashMap<String, Object> postRequests;
	private final HashMap<String, String> userVars = new HashMap<>();
	private Account account;
	private String[] test;

	public TempUser(String pPath, Account pAccount, String pIPAddress, HashMap<String, Object> pPostRequests) {
		path = pPath;
		account = pAccount;
		ipAddress = pIPAddress;
		postRequests = pPostRequests;
	}

	/**
	 * Cause of Security reasons the first sended POST is ignored. Cause of that
	 * always put in your form at first an input which is hidden
	 *
	 * @param pKey
	 * @return
	 */
	public Object getPostRequest(String pKey) {
		return postRequests.get(pKey);
	}

	/**
	 * As soon as the site is builded up this is getting deleted
	 *
	 * @param pKey   The key for the value
	 * @param pValue The value that should be set
	 */
	public void setUserVar(String pKey, String pValue) {
		userVars.put(pKey, pValue);
	}

	public String getUserVar(String pKey) {
		System.out.println(test[0] + test[1]);
		return userVars.get(pKey);
	}

	public String removeUserVar(String pKey) {
		return userVars.remove(pKey);
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

	public void setAccount(Account pAccount) {
		account = pAccount;
	}

}
